package hu.kdea.szavazas.ballotprocessor.projection;

public final class ProjectionData {
    private final float[] colProj;
    private final float[] rowProj;
    private final int colOffset;
    private final int rowOffset;
    private final int roiWidth;
    private final int roiHeight;

    public ProjectionData(float[] colProj, float[] rowProj, int colOffset, int rowOffset, int roiWidth, int roiHeight) {
        this.colProj = colProj;
        this.rowProj = rowProj;
        this.colOffset = colOffset;
        this.rowOffset = rowOffset;
        this.roiWidth = roiWidth;
        this.roiHeight = roiHeight;
    }

    public float[] getColProj() {
        return colProj;
    }

    public float[] getRowProj() {
        return rowProj;
    }

    public int getColOffset() {
        return colOffset;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getRoiWidth() {
        return roiWidth;
    }

    public int getRoiHeight() {
        return roiHeight;
    }
}
