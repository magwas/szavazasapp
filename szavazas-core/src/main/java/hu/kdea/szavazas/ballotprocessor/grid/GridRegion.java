package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;

public final class GridRegion {
    private final GrayU8 projectionInput;
    private final int cropTop;
    private final int qrCentreX;

    public GridRegion(GrayU8 projectionInput, int cropTop, int qrCentreX) {
        this.projectionInput = projectionInput;
        this.cropTop = cropTop;
        this.qrCentreX = qrCentreX;
    }

    public GrayU8 getProjectionInput() {
        return projectionInput;
    }

    public int getCropTop() {
        return cropTop;
    }

    public int getQrCentreX() {
        return qrCentreX;
    }
}
