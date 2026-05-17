package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public final class OvalDrawer {
    private OvalDrawer() {
    }

    public static void drawOval(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        int a = w / 2;
        int b = h / 2;
        int xc = x + a;
        int yc = y + b;
        drawOvalRegion1(image, xc, yc, a, b, color);
        drawOvalRegion2(image, xc, yc, a, b, color);
    }

    public static void fillOval(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        double a = w / 2.0;
        double b = h / 2.0;
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;
        for (int py = y; py < y + h; py++) {
            double dy = py - cy;
            if (Math.abs(dy) > b) {
                continue;
            }
            double dx = a * Math.sqrt(1.0 - (dy * dy) / (b * b));
            fillScanline(image, (int) (cx - dx), (int) (cx + dx), py, color);
        }
    }

    private static void fillScanline(Planar<GrayU8> image, int xStart, int xEnd, int y, int color) {
        for (int px = xStart; px <= xEnd; px++) {
            DrawingUtils.setPixel(image, px, y, color);
        }
    }

    private static void plot4(Planar<GrayU8> image, int xc, int yc, int dx, int dy, int color) {
        DrawingUtils.setPixel(image, xc + dx, yc + dy, color);
        DrawingUtils.setPixel(image, xc - dx, yc + dy, color);
        DrawingUtils.setPixel(image, xc + dx, yc - dy, color);
        DrawingUtils.setPixel(image, xc - dx, yc - dy, color);
    }

    private static void drawOvalRegion1(Planar<GrayU8> image, int xc, int yc, int a, int b, int color) {
        int dx = 0;
        int dy = b;
        int d1 = (b * b) - (a * a * b) + (int) (0.25 * a * a);
        int dx2 = 2 * b * b * dx;
        int dy2 = 2 * a * a * dy;
        while (dx2 < dy2) {
            plot4(image, xc, yc, dx, dy, color);
            if (d1 < 0) {
                dx++;
                dx2 += 2 * b * b;
                d1 += dx2 + b * b;
            } else {
                dx++;
                dy--;
                dx2 += 2 * b * b;
                dy2 -= 2 * a * a;
                d1 += dx2 - dy2 + b * b;
            }
        }
    }

    private static void drawOvalRegion2(Planar<GrayU8> image, int xc, int yc, int a, int b, int color) {
        int dx = 0;
        int dy = b;
        int d1 = (b * b) - (a * a * b) + (int) (0.25 * a * a);
        int dx2 = 2 * b * b * dx;
        int dy2 = 2 * a * a * dy;
        while (dx2 < dy2) {
            if (d1 < 0) {
                dx++;
                dx2 += 2 * b * b;
                d1 += dx2 + b * b;
            } else {
                dx++;
                dy--;
                dx2 += 2 * b * b;
                dy2 -= 2 * a * a;
                d1 += dx2 - dy2 + b * b;
            }
        }
        int d2 = (int) (b * b * (dx + 0.5) * (dx + 0.5) + a * a * (dy - 1) * (dy - 1) - a * a * b * b);
        while (dy >= 0) {
            plot4(image, xc, yc, dx, dy, color);
            if (d2 > 0) {
                dy--;
                dy2 -= 2 * a * a;
                d2 += a * a - dy2;
            } else {
                dy--;
                dx++;
                dx2 += 2 * b * b;
                dy2 -= 2 * a * a;
                d2 += dx2 - dy2 + a * a;
            }
        }
    }
}
