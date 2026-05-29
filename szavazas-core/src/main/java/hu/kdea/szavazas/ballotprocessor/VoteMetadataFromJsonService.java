package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import hu.kdea.szavazas.review.ExtractVoteNameService;
import javax.inject.Inject;
import org.json.JSONObject;

public class VoteMetadataFromJsonService {
    private final BallotResultFileRepository ballotResultFileRepository;
    private final ExtractVoteNameService extractVoteName;
    private final ParseVoteMetadataJsonService parseVoteMetadataJson;

    @Inject
    public VoteMetadataFromJsonService(
        BallotResultFileRepository ballotResultFileRepository,
        ExtractVoteNameService extractVoteName,
        ParseVoteMetadataJsonService parseVoteMetadataJson
    ) {
        this.ballotResultFileRepository = ballotResultFileRepository;
        this.extractVoteName = extractVoteName;
        this.parseVoteMetadataJson = parseVoteMetadataJson;
    }

    public VoteMetadataData apply(String raw, VoteMetadataData fallback) {
        String fileName = extractVoteName.apply(raw) + ".json";
        String content = ballotResultFileRepository.apply(fileName);
        if (content == null || content.isBlank()) {
            return fallback;
        }
        return parseVoteMetadataJson.apply(new JSONObject(content).getJSONObject("vote"));
    }
}