package util;

import tools.UnionFindSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


public class MingChaoYiCaiHuaUtil { // 鸣潮溢彩画工具类

    private static final Robot robot; // 脚本实例
    private static final double SCALE = 1.5; // 缩放比例
    private static final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private static int cnt = 0; // 回溯计数
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getBufferedImage(int x1, int y1, int x2, int y2) { // 截取图片
        x1 = to(x1);
        y1 = to(y1);
        x2 = to(x2);
        y2 = to(y2);
        Rectangle captureArea = new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
        return robot.createScreenCapture(captureArea);
    }

    public static Color getAverageRGB(BufferedImage image) { // 获取截图平均 RGB
        int width = image.getWidth(), height = image.getHeight();
        int[] res = new int[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);
                res[0] += color.getRed();
                res[1] += color.getGreen();
                res[2] += color.getBlue();
            }
        }
        for (int i = 0; i < 3; i++) res[i] /= width * height;
        return new Color(res[0], res[1], res[2]);
    }

    public static Color getAverageRGB(int x, int y, int d) { // 获取截图平均 RGB
        return getAverageRGB(getBufferedImage(x - d, y - d, x + d, y + d));
    }

    public static boolean isColorSimilar(Color color, Color target) { // 判断 RGB 是否接近目标颜色
        final int tolerance = 30;
        int r1 = color.getRed(), g1 = color.getGreen(), b1 = color.getBlue();
        int r2 = target.getRed(), g2 = target.getGreen(), b2 = target.getBlue();
        return Math.abs(r1 - r2) <= tolerance &&
                Math.abs(g1 - g2) <= tolerance &&
                Math.abs(b1 - b2) <= tolerance;
    }

    private static float calculateLabColorDifference(Color color1, Color color2) { // 计算两个颜色的差异
        int R1 = color1.getRed(), G1 = color1.getGreen(), B1 = color1.getBlue();
        int R2 = color2.getRed(), G2 = color2.getGreen(), B2 = color2.getBlue();
        float[] xyz1 = rgbToXYZ(R1, G1, B1), xyz2 = rgbToXYZ(R2, G2, B2);
        float[] lab1 = xyzToLab(xyz1), lab2 = xyzToLab(xyz2);
        float L = lab1[0] - lab2[0], A = lab1[1] - lab2[1], B = lab1[2] - lab2[2];
        return (float) Math.sqrt(L * L + A * A + B * B);
    }
    private static float[] rgbToXYZ(int R, int G, int B) {
        float[] xyz = new float[3];
        float r = R / 255.0f, g = G / 255.0f, b = B / 255.0f;
        r = (r > 0.04045) ? (float) Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92f;
        g = (g > 0.04045) ? (float) Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92f;
        b = (b > 0.04045) ? (float) Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92f;
        xyz[0] = r * 0.4124564f + g * 0.3575761f + b * 0.1804375f;
        xyz[1] = r * 0.2126729f + g * 0.7151522f + b * 0.0721750f;
        xyz[2] = r * 0.0193339f + g * 0.1191920f + b * 0.9503041f;
        return xyz;
    }
    private static float[] xyzToLab(float[] xyz) {
        float[] lab = new float[3];
        float Xn = 0.95047f, Yn = 1.00000f, Zn = 1.08883f;
        float x = xyz[0] / Xn, y = xyz[1] / Yn, z = xyz[2] / Zn;
        x = (x > 0.008856) ? (float) Math.pow(x, 1 / 3.0) : (x * 903.3f + 16) / 116;
        y = (y > 0.008856) ? (float) Math.pow(y, 1 / 3.0) : (y * 903.3f + 16) / 116;
        z = (z > 0.008856) ? (float) Math.pow(z, 1 / 3.0) : (z * 903.3f + 16) / 116;
        lab[0] = (116 * y) - 16;
        lab[1] = 500 * (x - y);
        lab[2] = 200 * (y - z);
        return lab;
    }

    public static int findClosestColorIndex(Color color, List<Color> colors) { // 返回最接近的颜色的下标
        int res = -1;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < colors.size(); i++) {
            double diff = calculateLabColorDifference(color, colors.get(i));
            if (diff < min) {
                min = diff;
                res = i;
            }
        }
        return res;
    }

    public static int to(int pos) { // 位置映射
        return (int) Math.round(pos / SCALE);
    }

    public static boolean dfs(int[][] g, int k, int target, int step, List<int[]> path, int total) { // 计算可行路径
        int n = g.length, m = g[0].length;
        if (total == n * m) return true;
        if (path.size() == step) return false;
        UnionFindSet UFS = new UnionFindSet(n * m);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                for (int i = 0; i < 4; i++) {
                    int xi = x + dx[i], yi = y + dy[i];
                    if (xi < 0 || xi >= n || yi < 0 || yi >= m) continue;
                    if (g[x][y] == g[xi][yi]) UFS.union(x * m + y, xi * m + yi);
                }
            }
        }
        Map<Integer, List<Integer>> faToPos = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int fa = UFS.find(i * m + j);
                faToPos.computeIfAbsent(fa, _ -> new ArrayList<>()).add(i * m + j);
            }
        }
        for (int fa : faToPos.keySet()) {
            List<Integer> posList = faToPos.get(fa);
            int x = posList.getFirst() / m, y = posList.getFirst() % m;
            int originalColor = g[x][y];
            for (int color = 0; color < k; color++) {
                if (color == originalColor) continue;
                for (int pos : posList) {
                    int i = pos / m, j = pos % m;
                    g[i][j] = color;
                }
                path.add(new int[]{x, y, color});
                cnt++;
                boolean result;
                if (color == target) result = dfs(g, k, target, step, path, total + posList.toArray().length);
                else if (originalColor == target) result = dfs(g, k, target, step, path, total - posList.toArray().length);
                else result = dfs(g, k, target, step, path, total);
                if (result) return true;
                for (int pos : posList) {
                    int i = pos / m, j = pos % m;
                    g[i][j] = originalColor;
                }
                path.removeLast();
            }
        }
        return false;
    }

    public static List<int[]> calculationPath(int[][] g, int k, int target, int step) { // 计算可行路径
        List<int[]> path = new ArrayList<>();
        int n = g.length, m = g[0].length, total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (g[i][j] == target) total++;
            }
        }
        dfs(g, k, target, step, path, total);
        if (!path.isEmpty()) {
            System.out.println("枚举点击次数：" + cnt);
            System.out.println("可行解：");
            for (int[] p: path) {
                System.out.println("(" + (p[0] + 1) + ", " + (p[1] + 1) + ")" + " -> " + (p[2] + 1));
            }
        }
        return path;
    }
}