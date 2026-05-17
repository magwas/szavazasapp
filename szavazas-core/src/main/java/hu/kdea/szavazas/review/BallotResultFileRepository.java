package hu.kdea.szavazas.review;

public interface BallotResultFileRepository {
    String apply(String fileName);
    void save(String fileName, String content);
}
