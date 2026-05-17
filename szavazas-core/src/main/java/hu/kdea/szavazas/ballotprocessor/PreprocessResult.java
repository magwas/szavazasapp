package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;

public record PreprocessResult(GrayU8 scaledGray, Double markerTopY) {
}
