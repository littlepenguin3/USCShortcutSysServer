package com.littlepenguin.uscshortcutsysserver.utils;

import com.littlepenguin.uscshortcutsysserver.VO.Selenium;
import com.littlepenguin.uscshortcutsysserver.exception.WebDriverNotMatchException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

@Component
public class WebDriverFactory {
    @Resource
    Selenium selenium;
    public WebDriver buildWebDriver() throws WebDriverNotMatchException {
        //chrome启动逻辑
        if(StringUtils.containsIgnoreCase(selenium.chromeDriver.name, "chrome")){
            //创建配置
            System.setProperty(selenium.chromeDriver.name,selenium.chromeDriver.path);
            ChromeOptions chromeOptions=new ChromeOptions();
            if(selenium.isHeadless) {chromeOptions.addArguments("--headless");}
            //返回WebDriver
            return new ChromeDriver(chromeOptions);
        }
        throw new WebDriverNotMatchException();
    }
    public WebDriverWait buildWebDriverWait(WebDriver webDriver,long millis){
        return new WebDriverWait(webDriver, Duration.ofMillis(millis));
    }
}
