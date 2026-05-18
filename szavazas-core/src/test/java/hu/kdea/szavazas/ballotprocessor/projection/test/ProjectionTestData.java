package hu.kdea.szavazas.ballotprocessor.projection.test;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import java.util.Arrays;
import java.util.List;

public interface ProjectionTestData {
    int PROJECTION_LENGTH_100 = 100;

    float[] PROJECTION_100 = projection100();
    float[] PROJECTION_3X3 = projection3x3();
    float[] PROJECTION_5X5 = projection5x5();

    GrayU8 IMAGE_3X3 = image3x3();
    GrayU8 IMAGE_5X5 = image5x5();
    GrayU8 IMAGE_10X20 = image10x20();

    RectangleData ROI_FULL_3X3 = new RectangleData(0, 0, 3, 3);
    RectangleData ROI_OFFSET_2X2 = new RectangleData(1, 1, 2, 2);
    RectangleData ROI_FULL_10X20 = new RectangleData(0, 0, 10, 20);

    RowBoundaryData ROW_BOUNDARY_VALID = new RowBoundaryData(15, 5);
    RowBoundaryData ROW_BOUNDARY_MARGIN_ADJUSTED = new RowBoundaryData(12, 88);

    List<Integer> PEAKS_ISOLATED = Arrays.asList(10, 30, 50);
    List<Integer> PEAKS_CLOSE = Arrays.asList(10, 12, 14);
    List<Integer> PEAKS_MULTI_GROUP = Arrays.asList(5, 7, 20, 22, 40);
    List<Integer> PEAKS_SINGLE = Arrays.asList(42);
    List<Integer> PEAKS_EMPTY = Arrays.asList();

    List<Integer> PEAKS_FOR_PAIRING = Arrays.asList(10, 25, 40, 55);
    List<Integer> PEAKS_WITH_UNMATCHED = Arrays.asList(10, 25, 30);
    List<Integer> PEAKS_WIDE_GAP = Arrays.asList(10, 50, 100);
    List<Integer> PEAKS_WITH_LOOKAHEAD = Arrays.asList(10, 15, 30);

    List<Integer> RAW_PEAKS_FOUR = Arrays.asList(10, 20, 30, 40);
    List<Integer> RAW_PEAKS_TWO = Arrays.asList(10, 20);
    List<Integer> RAW_PEAKS_OVERLAP = Arrays.asList(10, 25, 20, 35);

    private static float[] projection100() {
        float[] projection = new float[100];
        projection[25] = 100f;
        projection[60] = 140f;
        projection[80] = 120f;
        return projection;
    }

    private static float[] projection3x3() {
        return new float[]{1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    }

    private static float[] projection5x5() {
        float[] p = new float[25];
        for (int i = 0; i < 25; i++) {
            p[i] = i + 1;
        }
        return p;
    }

    private static GrayU8 image3x3() {
        GrayU8 image = new GrayU8(3, 3);
        image.set(0, 0, 1); image.set(1, 0, 2); image.set(2, 0, 3);
        image.set(0, 1, 4); image.set(1, 1, 5); image.set(2, 1, 6);
        image.set(0, 2, 7); image.set(1, 2, 8); image.set(2, 2, 9);
        return image;
    }

    private static GrayU8 image5x5() {
        GrayU8 image = new GrayU8(5, 5);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                image.set(x, y, y * 5 + x + 1);
            }
        }
        return image;
    }

    private static GrayU8 image10x20() {
        GrayU8 image = new GrayU8(10, 20);
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                image.set(x, y, 1);
            }
        }
        return image;
    }
}
