package com.example;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class WeiXin {

    private final Robot robot; // 脚本实例
    private double SCALE; // 缩放比例
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public WeiXin(double scale) {
        this.SCALE = scale;
    }

    public void run(int delay, int repeat) { // 运行脚本
        robot.delay(delay);
        for (int i = 0; i < repeat; i++) {
            mouseMove(1200,1120);
            clickA(InputEvent.BUTTON1_DOWN_MASK);
            String info = "woaizg";
            for (char c :info.toCharArray()) {
                clickB(KeyEvent.getExtendedKeyCodeForChar(c));
            }
            clickB(KeyEvent.VK_SPACE);
            mouseMove(1835,1200);
            clickA(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    private void mouseMove(int x, int y) { // 鼠标移动
        robot.mouseMove((int) Math.round(x / SCALE), (int) Math.round(y / SCALE));
    }

    private void clickA(int keyCode) { // 鼠标点击
        robot.mousePress(keyCode);
        robot.delay(100);
        robot.mouseRelease(keyCode);
    }

    private void clickB(int keyCode) { // 键盘点击
        robot.keyPress(keyCode);
        robot.delay(100);
        robot.keyRelease(keyCode);
    }
}