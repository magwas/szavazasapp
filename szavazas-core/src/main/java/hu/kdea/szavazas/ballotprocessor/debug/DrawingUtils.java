package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public final class DrawingUtils {
    private DrawingUtils() {
    }

    public static void setPixel(Planar<GrayU8> image, int x, int y, int color) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) {
            return;
        }
        image.getBand(0).set(x, y, (color >> 16) & 0xFF);
        image.getBand(1).set(x, y, (color >> 8) & 0xFF);
        image.getBand(2).set(x, y, color & 0xFF);
    }

    public static void drawLine(Planar<GrayU8> image, int x0, int y0, int x1, int y1, int color) {
        int x = x0;
        int y = y0;
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        while (true) {
            setPixel(image, x, y, color);
            if (x == x1 && y == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y += sy;
            }
        }
    }

    public static void drawRect(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        drawLine(image, x, y, x + w - 1, y, color);
        drawLine(image, x + w - 1, y, x + w - 1, y + h - 1, color);
        drawLine(image, x + w - 1, y + h - 1, x, y + h - 1, color);
        drawLine(image, x, y + h - 1, x, y, color);
    }

    public static void fillRect(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        int xStart = Math.max(0, x);
        int yStart = Math.max(0, y);
        int xEnd = Math.min(image.width, x + w);
        int yEnd = Math.min(image.height, y + h);
        for (int py = yStart; py < yEnd; py++) {
            for (int px = xStart; px < xEnd; px++) {
                setPixel(image, px, py, color);
            }
        }
    }

    public static void drawOval(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        OvalDrawer.drawOval(image, x, y, w, h, color);
    }

    public static void fillOval(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        OvalDrawer.fillOval(image, x, y, w, h, color);
    }

    public static void drawString(Planar<GrayU8> image, String text, int x, int y, int color) {
        drawString(image, text, x, y, color, 12f);
    }

    public static void drawString(Planar<GrayU8> image, String text, int x, int y, int color, float fontSize) {
        BitmapFont5x7.drawString(image, text, x, y, color, fontSize);
    }
}
