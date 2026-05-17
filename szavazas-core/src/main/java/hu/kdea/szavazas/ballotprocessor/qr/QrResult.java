package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.Rect;

public final class QrResult {
    private final String raw;
    private final int numSupport;
    private final int numCandidates;
    private final Rect boundingBox;

    public QrResult(String raw, int numSupport, int numCandidates, Rect boundingBox) {
        this.raw = raw;
        this.numSupport = numSupport;
        this.numCandidates = numCandidates;
        this.boundingBox = boundingBox;
    }

    public String getRaw() {
        return raw;
    }

    public int getNumSupport() {
        return numSupport;
    }

    public int getNumCandidates() {
        return numCandidates;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }
}
