package com.littlepenguin.uscshortcutsysserver.selenium;

/**
 * @author 38122
 */

public enum Signal{

    //工作组调整 刷新信号
    ADJUST_FRESH("ADJUST_FRESH"),
    //工作组调整 预热信号
    ADJUST_PREHEAT("ADJUST_PREHEAT"),
    //工作组调整 释放信号
    ADJUST_RELEASE("ADJUST_RELEASE"),

    //工作信号 补充
    WORK_SUPPLEMENT("WORK_SUPPLEMENT"),
    //工作信号 替换
    WORK_REPLACE("WORK_REPLACE"),
    ;
    Signal(String s) {
    }
}
