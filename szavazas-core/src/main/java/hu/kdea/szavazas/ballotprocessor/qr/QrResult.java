package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public record QrResult(String raw, int numSupport, int numCandidates, RectangleData boundingBox) {
}
