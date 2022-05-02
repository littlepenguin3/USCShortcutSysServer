package com.littlepenguin.uscshortcutsysserver.services;

import com.littlepenguin.uscshortcutsysserver.VO.Python;
import com.littlepenguin.uscshortcutsysserver.VO.Selenium;
import com.littlepenguin.uscshortcutsysserver.domain.SK;
import com.littlepenguin.uscshortcutsysserver.exception.CheckCodeException;
import com.littlepenguin.uscshortcutsysserver.exception.WebDriverNotMatchException;
import com.littlepenguin.uscshortcutsysserver.utils.SeleniumOperationUtils;
import com.littlepenguin.uscshortcutsysserver.utils.WebDriverFactory;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

//要被多线程调用，因此为prototype
@Component("SeleniumService")
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SeleniumService {
    @Resource
    Selenium selenium;
    @Resource
    Python py;
    @Resource
    WebDriverFactory webDriverFactory;



    private static final Queue<SK> skQueue = SKMain.skQueue;
    /**
     * 获取SK，SK将加入SK队列中
     * @throws IOException
     * @throws InterruptedException
     */
    public void obtainSK() throws CheckCodeException, IOException, InterruptedException, WebDriverNotMatchException {
        //队列数量超过50直接返回
        if(skQueue.size()>=50) {return;}

        //获取webDriver和WebDriverWait
        WebDriver webDriver = webDriverFactory.buildWebDriver();
        WebDriverWait waitService = webDriverFactory.buildWebDriverWait(webDriver,selenium.waitServiceMillis);
        //访问网站
        webDriver.get(selenium.cardWeb.url);
        //输入账号
        SeleniumOperationUtils.safeSendkeysByXpath(webDriver,waitService,selenium.cardWeb.xpaths.teacherNumber,selenium.cardWeb.username);
        //输入密码
        SeleniumOperationUtils.safeSendkeysByXpath(webDriver,waitService,selenium.cardWeb.xpaths.teacherPassword,selenium.cardWeb.password);
        //获取验证码元素
        WebElement checkCodeImgElement = SeleniumOperationUtils.safeFindElementByXpath(webDriver,waitService,selenium.cardWeb.xpaths.checkCodeImg);
        //截图获得验证码
        File scrFile = checkCodeImgElement.getScreenshotAs(OutputType.FILE);
        String checkCodeStr = null;
        Integer counter = 0;
        while(checkCodeStr == null && counter<8) {
            //启用子进程 调用python脚本获取验证码
            Process process = Runtime.getRuntime().exec(py.pyLocation + ' ' + py.scriptName + ' ' + scrFile.getPath());
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //当前线程阻塞直到成功获取验证码 很快 约15ms
            process.waitFor();
            String line = null;

            while ((line = in.readLine()) != null) {
                if (StringUtils.isNumeric(line)) {
                    checkCodeStr = line;
                }
            }
            in.close();
            process.destroy();
            counter++;
        }
        //TODO: 可能是验证码图形问题，需要重新输入 8次还是不能登录
        //获取8次验证码还是没输出
        if(counter >= 8){
            throw new CheckCodeException();
        }
        //输入验证码
        SeleniumOperationUtils.safeSendkeysByXpath(webDriver,waitService,selenium.cardWeb.xpaths.checkCodeInput,checkCodeStr);
        //点击登录按钮
        SeleniumOperationUtils.safeClickByXpath(webDriver,waitService,selenium.cardWeb.xpaths.loginButton);
        //验证已经跳转可以获取sk
        SeleniumOperationUtils.ElementToBeClickableBypath(waitService,selenium.cardWeb.xpaths.validCheckElement);
        //获取sk
        SK sk = this.getSK(webDriver);
        skQueue.offer(sk);
        System.out.println(Thread.currentThread().getName()+"得到"+sk.toString()+"此时队列容量为"+skQueue.size());

        //TODO： 第二次及以后获取SK逻辑 sk应该直接加入队列 设计到多线程问题
        while(skQueue.size()<50) {
            webDriver.get(selenium.cardWeb.url);
            //验证已经跳转可以获取sk
            SeleniumOperationUtils.ElementToBeClickableBypath(waitService, selenium.cardWeb.xpaths.validCheckElement);
            sk = this.getSK(webDriver);
            skQueue.offer(sk);
            System.out.println(Thread.currentThread().getName()+"得到"+sk.toString()+"此时队列容量为"+skQueue.size());
        }
    }

    /**
     * 得到SK
     * @return SK
     */
    private SK getSK(WebDriver webDriver){
        String[] split = webDriver.getCurrentUrl().split(selenium.cardWeb.splitRegForSK);
        return new SK(split[1]);
    }


}
