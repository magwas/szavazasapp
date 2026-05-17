package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;

public final class PreprocessResult {
    private final GrayU8 scaledGray;
    private final Double markerTopY;

    public PreprocessResult(GrayU8 scaledGray, Double markerTopY) {
        this.scaledGray = scaledGray;
        this.markerTopY = markerTopY;
    }

    public GrayU8 getScaledGray() {
        return scaledGray;
    }

    public Double getMarkerTopY() {
        return markerTopY;
    }
}
