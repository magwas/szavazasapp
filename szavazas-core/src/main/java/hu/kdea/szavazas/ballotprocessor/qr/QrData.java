package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public record QrData(String raw, int numSupport, int numRows, RectangleData bbox) {
}
