package com.littlepenguin.uscshortcutsysserver.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumOperationUtils {
    /**
     * 安全根据Xpath输入内容
     * @param driver web驱动
     * @param wait webWait对象
     * @param xpath 元素xpath
     * @param str 输入内容
     */
    public static void safeSendkeysByXpath(WebDriver driver,WebDriverWait wait, String xpath, String str){
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        WebElement safeElement = driver.findElement(By.xpath(xpath));
        safeElement.sendKeys(str);
    }

    public static WebElement safeFindElementByXpath(WebDriver driver,WebDriverWait wait, String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        WebElement safeElement = driver.findElement(By.xpath(xpath));
        return safeElement;
    }

    public static void safeClickByXpath(WebDriver driver,WebDriverWait wait, String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        WebElement safeElement = driver.findElement(By.xpath(xpath));
        safeElement.click();
    }

    public static void ElementToBeClickableBypath(WebDriverWait wait, String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    }
}
