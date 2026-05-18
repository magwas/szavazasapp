package hu.kdea.szavazas.ballotprocessor.aruco.test;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import java.util.List;

public interface ArucoTestData {
    int IMAGE_SIZE_100 = 100;
    int IMAGE_SIZE_200 = 200;
    int IMAGE_SIZE_80 = 80;

    Planar<GrayU8> PLANAR_100 = planar100();
    GrayU8 GRAY_100 = gray100();
    Planar<GrayU8> PLANAR_200 = planar200();
    GrayU8 GRAY_200 = gray200();
    Planar<GrayU8> PLANAR_80 = planar80();
    GrayU8 GRAY_80 = gray80();

    ArucoMarkersData MARKERS_TOP_LEFT = markersTopLeft();
    ArucoMarkersData MARKERS_FULL_FRAME = markersFullFrame();

    private static Planar<GrayU8> planar100() {
        return new Planar<>(GrayU8.class, 100, 100, 3);
    }

    private static GrayU8 gray100() {
        return new GrayU8(100, 100);
    }

    private static Planar<GrayU8> planar200() {
        return new Planar<>(GrayU8.class, 200, 200, 3);
    }

    private static GrayU8 gray200() {
        return new GrayU8(200, 200);
    }

    private static Planar<GrayU8> planar80() {
        return new Planar<>(GrayU8.class, 80, 80, 3);
    }

    private static GrayU8 gray80() {
        return new GrayU8(80, 80);
    }

    private static ArucoMarkersData markersTopLeft() {
        return new ArucoMarkersData(
            List.of(
                new Point2D_F64(0, 0),
                new Point2D_F64(99, 0),
                new Point2D_F64(99, 99),
                new Point2D_F64(0, 99)
            ),
            new Point2D_F64(0, 99),
            new Point2D_F64(99, 99)
        );
    }

    private static ArucoMarkersData markersFullFrame() {
        return new ArucoMarkersData(
            List.of(
                new Point2D_F64(10, 10),
                new Point2D_F64(190, 10),
                new Point2D_F64(190, 190),
                new Point2D_F64(10, 190)
            ),
            new Point2D_F64(10, 190),
            new Point2D_F64(190, 190)
        );
    }
}
