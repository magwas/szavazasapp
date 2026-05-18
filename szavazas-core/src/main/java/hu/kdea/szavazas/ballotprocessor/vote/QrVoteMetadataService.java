package hu.kdea.szavazas.ballotprocessor.vote;

import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import javax.inject.Inject;

public class QrVoteMetadataService {
    @Inject
    public QrVoteMetadataService() {
    }

    public VoteMetadataData apply(QrData qrData) {
        return qrData.voteMetadata();
    }
}
