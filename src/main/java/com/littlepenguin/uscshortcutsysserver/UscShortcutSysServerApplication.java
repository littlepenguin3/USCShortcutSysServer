package com.littlepenguin.uscshortcutsysserver;

import com.littlepenguin.uscshortcutsysserver.VO.Selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class UscShortcutSysServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(UscShortcutSysServerApplication.class, args);
    }

}
