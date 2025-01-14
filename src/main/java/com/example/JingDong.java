package com.example;

import java.awt.*;
import java.awt.event.InputEvent;
import java.time.LocalDateTime;


public class JingDong {

    private final Robot robot; // 脚本实例
    private final double SCALE; // 缩放比例
    private final LocalDateTime target; // 目标时间
    private final int[][] pos; // 三个按钮的坐标
    private final int delay, combo, interval;
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public JingDong(double scale, String time, int[][] pos, int delay, int combo, int interval, long deff) { // 初始化
        this.SCALE = scale;
        this.target = LocalDateTime.parse(time).minusNanos(deff);;
        this.pos = pos;
        this.delay = delay;
        this.combo = combo;
        this.interval = interval;
    }

    public void run() { // 运行脚本
        robot.delay(delay * 1000);
        while (true) {
            if (LocalDateTime.now().isAfter(target)) { // 到时间了
                combo();
            }
        }
    }

    private void combo() { // 连击
        LocalDateTime limit = LocalDateTime.now().plusSeconds(combo);
        System.out.println(LocalDateTime.now());
        while (LocalDateTime.now().isBefore(limit)) {
            for (int[] pos : pos) {
                int x = pos[0], y = pos[1];
                mouseMove(x, y);
                clickA(InputEvent.BUTTON1_DOWN_MASK);
            }
        }
        System.exit(0); // 退出程序
    }

    private void mouseMove(int x, int y) { // 鼠标移动
        robot.mouseMove((int) Math.round(x / SCALE), (int) Math.round(y / SCALE));
    }

    private void clickA(int keyCode) { // 鼠标点击
        robot.mousePress(keyCode);
        robot.delay(interval);
        robot.mouseRelease(keyCode);
    }
}
