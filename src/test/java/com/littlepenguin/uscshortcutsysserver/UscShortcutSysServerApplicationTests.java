package com.littlepenguin.uscshortcutsysserver;

import com.littlepenguin.uscshortcutsysserver.VO.Python;
import com.littlepenguin.uscshortcutsysserver.VO.Selenium;
import com.littlepenguin.uscshortcutsysserver.domain.SK;
import com.littlepenguin.uscshortcutsysserver.exception.CheckCodeException;
import com.littlepenguin.uscshortcutsysserver.exception.WebDriverNotMatchException;
import com.littlepenguin.uscshortcutsysserver.services.SKMain;
import com.littlepenguin.uscshortcutsysserver.services.SeleniumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootTest
class UscShortcutSysServerApplicationTests {

    @Autowired
    Selenium selenium;
    @Autowired
    Python py;
    @Autowired
    ApplicationContext app;


    @Test
    void contextLoads() {
        System.out.println(py);
        System.out.println(selenium);
    }

//    @Test
//    void testSelenium() throws IOException, InterruptedException, CheckCodeException {
//        service.obtainSK();
//    }

    @Test
    void testQueue(){
        System.out.println(SKMain.skQueue.size());
    }
    @Test
    void testSelenium() throws CheckCodeException, IOException, InterruptedException, WebDriverNotMatchException {
        for (int i = 0; i < 2; i++) {
            ((SeleniumService) app.getBean("SeleniumService")).initObtainSK();
        }
        Thread.sleep(30000);
        for (SK sk : SKMain.skQueue) {
            System.out.println(sk.getValue());
        }
    }

    @Test
    void testSeleniumService() throws CheckCodeException, IOException, InterruptedException {
        SeleniumService seleniumService1 = (SeleniumService)app.getBean("SeleniumService");
        SeleniumService seleniumService2 = (SeleniumService)app.getBean("SeleniumService");
        SeleniumService seleniumService3 = (SeleniumService)app.getBean("SeleniumService");
        Thread thread1 = new Thread(() -> {
            try {
                seleniumService1.initObtainSK();
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
        Thread thread2 = new Thread(() -> {
            try {
                seleniumService2.initObtainSK();
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
        Thread thread3 = new Thread(() -> {
            try {
                seleniumService3.initObtainSK();
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
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
    }
}
