package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import hu.kdea.szavazas.review.ExtractVoteNameService;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;
import org.json.JSONObject;

public class VoteMetadataFromJsonService {
    private final BallotResultFileRepository ballotResultFileRepository;
    private final ExtractVoteNameService extractVoteName;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public VoteMetadataFromJsonService(
        BallotResultFileRepository ballotResultFileRepository,
        ExtractVoteNameService extractVoteName,
        LoggerWrapper loggerWrapper
    ) {
        this.ballotResultFileRepository = ballotResultFileRepository;
        this.extractVoteName = extractVoteName;
        this.loggerWrapper = loggerWrapper;
    }

    public VoteMetadataData apply(String raw, VoteMetadataData fallback) {
        String fileName = extractVoteName.apply(raw) + ".json";
        String content = ballotResultFileRepository.apply(fileName);
        boolean isBlank = content == null || content.isBlank();
        loggerWrapper.d("VoteMetaFromJson", "raw=" + raw + " fileName=" + fileName + " contentIsBlank=" + isBlank + " content=" + (content == null ? "null" : content));
        if (isBlank) {
            loggerWrapper.d("VoteMetaFromJson", "USING FALLBACK: " + fallback);
            return fallback;
        }
        loggerWrapper.d("VoteMetaFromJson", "parsing JSON");
        JSONObject vote = new JSONObject(content).getJSONObject("vote");
        VoteMetadataData result = new VoteMetadataData(vote.getString("voteId"), vote.getString("voteName"), vote.getInt("candidateCount"), strings(vote.getJSONArray("candidates")), vote.getInt("supportColumnCount"), strings(vote.getJSONArray("issuedBallotIds")));
        loggerWrapper.d("VoteMetaFromJson", "FROM JSON: " + result);
        return result;
    }

    private List<String> strings(JSONArray array) {
        java.util.ArrayList<String> strings = new java.util.ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            strings.add(array.getString(index));
        }
        return List.copyOf(strings);
    }
}
