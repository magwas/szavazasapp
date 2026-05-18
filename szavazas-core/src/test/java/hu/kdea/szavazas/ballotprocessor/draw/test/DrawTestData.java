package hu.kdea.szavazas.ballotprocessor.draw.test;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public interface DrawTestData {
    int COLOR_WHITE = 0xFFFFFF;
    int COLOR_RED = 0xFF0000;
    int COLOR_GREEN = 0x00FF00;
    int COLOR_BLUE = 0x0000FF;
    int COLOR_AABBCC = 0xAABBCC;

    static Planar<GrayU8> fiveByFivePlanar() {
        return new Planar<>(GrayU8.class, 5, 5, 3);
    }

    static Planar<GrayU8> threeByThreePlanar() {
        return new Planar<>(GrayU8.class, 3, 3, 3);
    }

    static Planar<GrayU8> sixBySixPlanar() {
        return new Planar<>(GrayU8.class, 6, 6, 3);
    }

    static Planar<GrayU8> tenByTenPlanar() {
        return new Planar<>(GrayU8.class, 10, 10, 3);
    }

    static Planar<GrayU8> twentyByTenPlanar() {
        return new Planar<>(GrayU8.class, 20, 10, 3);
    }

    static Planar<GrayU8> thirtyByTenPlanar() {
        return new Planar<>(GrayU8.class, 30, 10, 3);
    }
}
