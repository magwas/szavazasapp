package hu.kdea.szavazas.ballotprocessor.test;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public interface BallotProcessorStage1TestData {
    Planar<GrayU8> RGB_THREE_PIXEL_PLANAR = rgbPlanar();
    GrayU8 GRAY_FOUR_BY_FOUR = grayFourByFour();
    RectangleProjectionFixtureData PROJECTION_FIXTURE = projectionFixture();
    float[] FIND_MAX_PEAK_PROJECTION = new float[]{1f, 9f, 9f, 3f, 8f};
    float[] RAW_PEAK_PROJECTION = new float[]{0f, 12f, 8f, 12f, 0f, 11f, 0f};
    float[] NO_RAW_PEAK_PROJECTION = new float[]{0f, 0f, 0f};
    float[] GRID_BOUNDARY_PROJECTION = gridBoundaryProjection();
    GrayU8 FLAT_CONTRAST_IMAGE = flatContrastImage();
    GrayU8 CONTRAST_IMAGE = contrastImage();
    GrayU8 SHARPEN_INPUT = sharpenInput();
    GrayU8 BINARIZE_BIMODAL_INPUT = binarizeBimodalInput();
    GrayU8 BINARIZE_FLAT_INPUT = binarizeFlatInput();

    private static Planar<GrayU8> rgbPlanar() {
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, 3, 1, 3);
        planar.getBand(0).set(0, 0, 255);
        planar.getBand(1).set(0, 0, 0);
        planar.getBand(2).set(0, 0, 0);
        planar.getBand(0).set(1, 0, 0);
        planar.getBand(1).set(1, 0, 255);
        planar.getBand(2).set(1, 0, 0);
        planar.getBand(0).set(2, 0, 10);
        planar.getBand(1).set(2, 0, 20);
        planar.getBand(2).set(2, 0, 30);
        return planar;
    }

    private static GrayU8 grayFourByFour() {
        GrayU8 image = new GrayU8(4, 4);
        int value = 1;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                image.set(x, y, value++);
            }
        }
        return image;
    }

    private static RectangleProjectionFixtureData projectionFixture() {
        GrayU8 image = new GrayU8(5, 5);
        int[][] values = new int[][]{
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {16, 17, 18, 19, 20},
                {21, 22, 23, 24, 25}
        };
        for (int y = 0; y < values.length; y++) {
            for (int x = 0; x < values[y].length; x++) {
                image.set(x, y, values[y][x]);
            }
        }
        return new RectangleProjectionFixtureData(image, 1, 1, 3, 3);
    }

    private static float[] gridBoundaryProjection() {
        float[] projection = new float[100];
        projection[25] = 100f;
        projection[60] = 140f;
        projection[80] = 120f;
        return projection;
    }

    private static GrayU8 flatContrastImage() {
        GrayU8 image = new GrayU8(2, 2);
        image.set(0, 0, 77);
        image.set(1, 0, 77);
        image.set(0, 1, 77);
        image.set(1, 1, 77);
        return image;
    }

    private static GrayU8 contrastImage() {
        GrayU8 image = new GrayU8(3, 1);
        image.set(0, 0, 10);
        image.set(1, 0, 20);
        image.set(2, 0, 30);
        return image;
    }

    private static GrayU8 sharpenInput() {
        GrayU8 image = new GrayU8(5, 5);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                image.set(x, y, 20);
            }
        }
        image.set(2, 2, 220);
        return image;
    }

    private static GrayU8 binarizeBimodalInput() {
        GrayU8 image = new GrayU8(2, 2);
        image.set(0, 0, 10);
        image.set(1, 0, 15);
        image.set(0, 1, 240);
        image.set(1, 1, 245);
        return image;
    }

    private static GrayU8 binarizeFlatInput() {
        GrayU8 image = new GrayU8(2, 2);
        image.set(0, 0, 90);
        image.set(1, 0, 90);
        image.set(0, 1, 90);
        image.set(1, 1, 90);
        return image;
    }
}
