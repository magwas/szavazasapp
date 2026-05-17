package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;

public record ArucoDetectionResultData(Planar<GrayU8> warpedPlanar, Double markerTopY) {
}
