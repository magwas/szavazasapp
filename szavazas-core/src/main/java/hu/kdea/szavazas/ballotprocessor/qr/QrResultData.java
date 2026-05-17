package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.Rect;

public record QrResultData(String raw, int numSupport, int numRows, Rect bbox) {
}
