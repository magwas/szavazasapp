package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;

public record GridRegion(GrayU8 projectionInput, int cropTop, int qrCentreX) {
}
