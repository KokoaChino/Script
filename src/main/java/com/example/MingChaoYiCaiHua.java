package com.example;

import util.MingChaoYiCaiHuaUtil;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class MingChaoYiCaiHua { // 鸣潮溢彩画

    private final Robot robot; // 脚本实例
    private final int DELAY; // 延迟时间

    /* -------------------------------------------------------------------------------- */
    private int[][] g = { // 画板
            { 0, 2, 2, 1, 2, 2, 2, 1, 2, 2 },
            { 2, 2, 3, 2, 2, 2, 3, 2, 2, 2 },
            { 2, 1, 2, 2, 2, 1, 2, 2, 0, 2 },
            { 1, 2, 2, 2, 1, 2, 2, 0, 2, 2 },
            { 2, 2, 2, 1, 2, 2, 0, 2, 2, 1 },
            { 2, 2, 1, 2, 2, 3, 2, 2, 3, 2 },
            { 2, 1, 2, 2, 3, 2, 2, 1, 2, 2 },
            { 2, 2, 2, 2, 2, 2, 3, 2, 2, 0 }
    };
    private final int step = 4; // 步数
    private int n = 4; // 颜色种数
    private int target = 0; // 目标颜色
    /* -------------------------------------------------------------------------------- */
    private List<Color> colors = new ArrayList<>(); // 颜色
    private int[][] pos3 = {
            {2033, 424}, {2033, 624}, {2033, 824}
    };
    private int[][] pos4 = {
            {2033, 424}, {2033, 624}, {2033, 824}, {2033, 1024}
    };
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public MingChaoYiCaiHua(int delay) {
        this.DELAY = delay;
    }

    private void init() { // 初始配置
        final int dis = 50, x1 = 516, y1 = 272, x2 = 1849, y2 = 1344;
        if (MingChaoYiCaiHuaUtil.isColorSimilar(MingChaoYiCaiHuaUtil.getAverageRGB(2034, 1085, 0), Color.white)) {
            n = 4;
            for (int[] pos: pos4) {
                int x = pos[0], y = pos[1];
                colors.add(MingChaoYiCaiHuaUtil.getAverageRGB(x, y, dis));
            }
        } else {
            n = 3;
            for (int[] pos: pos3) {
                int x = pos[0], y = pos[1];
                colors.add(MingChaoYiCaiHuaUtil.getAverageRGB(x, y, dis));
            }
        }
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                int x = (int) ((double) (x2 - x1) / g[i].length * (j + 0.5)) + x1;
                int y = (int) ((double) (y2 - y1) / g.length * (i + 0.5)) + y1;
                Color color = MingChaoYiCaiHuaUtil.getAverageRGB(x, y, dis);
                g[i][j] = MingChaoYiCaiHuaUtil.findClosestColorIndex(color, colors);
            }
        }
        target = MingChaoYiCaiHuaUtil.findClosestColorIndex(MingChaoYiCaiHuaUtil.getAverageRGB(813, 1433, 0), colors);
    }

    public void run(boolean is_run) {
        if (is_run) {
            final int x1 = 516, y1 = 272, x2 = 1849, y2 = 1344;
            AtomicReference<List<int[]>> path = new AtomicReference<>();
            Thread pathCalculationThread = new Thread(() -> path.set(MingChaoYiCaiHuaUtil.calculationPath(g, n, target, step)));
            pathCalculationThread.start();
//            robot.delay(DELAY * 1000);
//            try {
//                pathCalculationThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for (int[] p: path.get()) {
//                int i = p[0], j = p[1], color = p[2];
//                clickA(pos4[color][0], pos4[color][1]);
//                int x = (int) ((double) (x2 - x1) / g[i].length * (j + 0.5)) + x1;
//                int y = (int) ((double) (y2 - y1) / g.length * (i + 0.5)) + y1;
//                clickA(x, y);
//                robot.delay(3000);
//            }
        } else {
            robot.delay(DELAY * 1000);
            init();
            System.out.println("private int[][] g = { // 画板");
            for (int i = 0; i < g.length; i++) {
                System.out.print("\t{ ");
                for (int j = 0; j < g[i].length - 1; j++) {
                    System.out.print(g[i][j] + ", ");
                }
                System.out.println(g[i][g[i].length - 1] + " }" + (i == g.length - 1 ? "\n};" : ","));
            }
            String s = String.format("""
                private final int step = 4; // 步数
                private int n = %d; // 颜色种数
                private int target = %d; // 目标颜色
                                """, n, target);
            System.out.println(s);
        }
    }

    private void clickA(int x, int y) { // 鼠标点击
        robot.delay(100);
        robot.mouseMove(MingChaoYiCaiHuaUtil.to(x), MingChaoYiCaiHuaUtil.to(y));
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
    }
}