package hu.kdea.szavazas.ballotprocessor.qr;

import javax.inject.Inject;

public class ExtractQrVoteIdService {
    @Inject
    public ExtractQrVoteIdService() {
    }

    public String apply(String raw) {
        int separator = raw.indexOf('-');
        return separator < 0 ? raw : raw.substring(0, separator);
    }
}