package hu.kdea.szavazas.ballotprocessor.grid.test;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.Arrays;
import java.util.List;

public interface GridTestData {
    int GRID_WIDTH_50 = 50;
    int GRID_HEIGHT_60 = 60;

    GrayU8 PROJECTION_INPUT_50X60 = projectionInput50x60();
    GrayU8 PROJECTION_INPUT_100X100 = projectionInput100x100();
    GrayU8 PROJECTION_INPUT_50X50 = projectionInput50x50();
    GrayU8 PROJECTION_INPUT_30X40 = projectionInput30x40();
    GrayU8 PROJECTION_INPUT_20X30 = projectionInput20x30();

    QrData QR_WITH_BBOX = qrWithBbox();
    QrData QR_WITH_BBOX_SMALL = qrWithBboxSmall();

    GridRegionData GRID_REGION_50X50 = gridRegion50x50();
    GridRegionData GRID_REGION_10X15 = gridRegion10x15();

    List<RectangleData> SINGLE_CHECKBOX = singleCheckbox();
    List<RectangleData> TWO_CHECKBOXES = twoCheckboxes();

    private static GrayU8 projectionInput50x60() {
        return new GrayU8(50, 60);
    }

    private static GrayU8 projectionInput100x100() {
        return new GrayU8(100, 100);
    }

    private static GrayU8 projectionInput50x50() {
        return new GrayU8(50, 50);
    }

    private static GrayU8 projectionInput30x40() {
        GrayU8 image = new GrayU8(30, 40);
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 30; x++) {
                image.set(x, y, 100);
            }
        }
        return image;
    }

    private static GrayU8 projectionInput20x30() {
        return new GrayU8(20, 30);
    }

    private static QrData qrWithBbox() {
        return new QrData("raw", new VoteMetadataData("raw", "raw", 5, List.of(), 3, List.of("raw")), new RectangleData(10, 20, 30, 40));
    }

    private static QrData qrWithBboxSmall() {
        return new QrData("raw", new VoteMetadataData("raw", "raw", 5, List.of(), 3, List.of("raw")), new RectangleData(5, 10, 15, 20));
    }

    private static GridRegionData gridRegion50x50() {
        return new GridRegionData(new GrayU8(50, 50), 5, 25);
    }

    private static GridRegionData gridRegion10x15() {
        return new GridRegionData(new GrayU8(10, 15), 10, 0);
    }

    private static List<RectangleData> singleCheckbox() {
        return Arrays.asList(new RectangleData(0, 0, 10, 10));
    }

    private static List<RectangleData> twoCheckboxes() {
        return Arrays.asList(
            new RectangleData(0, 0, 10, 10),
            new RectangleData(10, 0, 10, 10)
        );
    }
}
