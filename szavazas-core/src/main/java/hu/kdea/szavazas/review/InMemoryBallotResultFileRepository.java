package hu.kdea.szavazas.review;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class InMemoryBallotResultFileRepository implements BallotResultFileRepository {
    private final Map<String, String> files = new HashMap<>();

    @Inject
    public InMemoryBallotResultFileRepository() {
    }

    @Override
    public String apply(String fileName) {
        return files.getOrDefault(fileName, "");
    }

    @Override
    public void save(String fileName, String content) {
        files.put(fileName, content);
    }
}
