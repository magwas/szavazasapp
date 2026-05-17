package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.Rect;

public final class QrData {
    private final String raw;
    private final int numSupport;
    private final int numRows;
    private final Rect bbox;

    public QrData(String raw, int numSupport, int numRows, Rect bbox) {
        this.raw = raw;
        this.numSupport = numSupport;
        this.numRows = numRows;
        this.bbox = bbox;
    }

    public String getRaw() {
        return raw;
    }

    public int getNumSupport() {
        return numSupport;
    }

    public int getNumRows() {
        return numRows;
    }

    public Rect getBbox() {
        return bbox;
    }
}
