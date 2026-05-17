package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;

public record GridRegionData(GrayU8 projectionInput, int cropTop, int qrCentreX) {
}
