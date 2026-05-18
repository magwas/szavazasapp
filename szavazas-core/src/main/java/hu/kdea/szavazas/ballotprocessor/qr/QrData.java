package hu.kdea.szavazas.ballotprocessor.qr;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;

public record QrData(String raw, VoteMetadataData voteMetadata, RectangleData bbox) {
}
