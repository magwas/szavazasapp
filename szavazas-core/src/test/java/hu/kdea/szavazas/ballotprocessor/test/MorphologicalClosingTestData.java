package hu.kdea.szavazas.ballotprocessor.test;

import boofcv.struct.image.GrayU8;

public interface MorphologicalClosingTestData {

    GrayU8 CENTERED_BLOCK_WITH_SINGLE_PIXEL_GAP = GrayU8TestUtil.grayU8(
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 1, 0, 1, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
    );

    GrayU8 CENTERED_BLOCK_WITHOUT_GAP = GrayU8TestUtil.grayU8(
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
    );
}
