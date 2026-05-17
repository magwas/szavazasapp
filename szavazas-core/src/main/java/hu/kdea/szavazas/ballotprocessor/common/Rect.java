package hu.kdea.szavazas.ballotprocessor.common;

import java.util.Objects;

public final class Rect {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int centerX() {
        return x + width / 2;
    }

    public int bottom() {
        return y + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rect)) {
            return false;
        }
        Rect rect = (Rect) o;
        return x == rect.x && y == rect.y && width == rect.width && height == rect.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Rect{" + "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
    }
}
