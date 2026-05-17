package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;

public record ErosionResultData(GrayU8 processed, GrayU8 eroded) {
}
