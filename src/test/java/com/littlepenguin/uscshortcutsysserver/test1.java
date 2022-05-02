package com.littlepenguin.uscshortcutsysserver;

import com.littlepenguin.uscshortcutsysserver.exception.CheckCodeException;
import com.littlepenguin.uscshortcutsysserver.exception.WebDriverNotMatchException;
import com.littlepenguin.uscshortcutsysserver.services.SeleniumService;
import com.littlepenguin.uscshortcutsysserver.utils.SpringContextUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class test1 {
    @Test
    void testSelenium() throws CheckCodeException, IOException, InterruptedException {
        SeleniumService seleniumService1 = (SeleniumService) SpringContextUtils.getBean("SeleniumService");
        //SeleniumService seleniumService2 = (SeleniumService) SpringContextUtils.getBean("SeleniumService");
        Thread thread1 = new Thread(() -> {
            try {
                seleniumService1.obtainSK();
                Thread.sleep(10000);
            } catch (CheckCodeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (WebDriverNotMatchException e) {
                e.printStackTrace();
            }
        });
        /*
        Thread thread2 = new Thread(() -> {
            try {
                seleniumService2.obtainSK();
                Thread.sleep(1000);
            } catch (CheckCodeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        */


        thread1.start();
        //thread2.start();
        //thread2.join();
        thread1.join();
    }
    @Test
    void testSelenium2() throws CheckCodeException, IOException, InterruptedException, WebDriverNotMatchException {
        SeleniumService seleniumService1 = (SeleniumService) SpringContextUtils.getBean("SeleniumService");
        seleniumService1.obtainSK();
    }
}
