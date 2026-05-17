package hu.kdea.szavazas.review;

import javax.inject.Inject;

public class ExtractVoteNameService {
    @Inject
    public ExtractVoteNameService() {
    }

    public String apply(String raw) {
        int separator = raw.indexOf('-');
        return separator < 0 ? raw : raw.substring(0, separator);
    }
}
