package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public final class XDetectTestImageFactory {

    private XDetectTestImageFactory() {
    }

    public static GrayU8 filledImage(int width, int height, int value) {
        GrayU8 image = new GrayU8(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.set(x, y, value);
            }
        }
        return image;
    }

    public static GrayU8 denseSkeleton(int width, int height) {
        GrayU8 skeleton = new GrayU8(width, height);
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                skeleton.set(x, y, 1);
            }
        }
        return skeleton;
    }

    public static GrayU8 croppedCellSource(RectangleData outerRect) {
        GrayU8 binary = new GrayU8(30, 30);
        int innerWidth = outerRect.width() - 2 * XDetectConstants.X_MARGIN;
        int innerHeight = outerRect.height() - 2 * XDetectConstants.X_MARGIN;
        for (int y = 0; y < innerHeight; y++) {
            for (int x = 0; x < innerWidth; x++) {
                binary.set(outerRect.x() + XDetectConstants.X_MARGIN + x, outerRect.y() + XDetectConstants.X_MARGIN + y, x + y * innerWidth + 1);
            }
        }
        return binary;
    }
}
