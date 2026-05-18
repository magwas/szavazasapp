package hu.kdea.szavazas.ballotprocessor.test;

import boofcv.struct.image.GrayU8;

public record RectangleProjectionFixtureData(GrayU8 image, int roiX, int roiY, int roiWidth, int roiHeight) {
}
