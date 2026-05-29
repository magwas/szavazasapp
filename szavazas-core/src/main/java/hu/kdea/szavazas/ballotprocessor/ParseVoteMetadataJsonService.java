package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParseVoteMetadataJsonService {
    @Inject
    public ParseVoteMetadataJsonService() {
    }

    public VoteMetadataData apply(JSONObject vote) {
        return new VoteMetadataData(
            vote.getString("voteId"),
            vote.getString("voteName"),
            vote.getInt("candidateCount"),
            strings(vote.getJSONArray("candidates")),
            vote.getInt("supportColumnCount"),
            strings(vote.getJSONArray("issuedBallotIds"))
        );
    }

    private List<String> strings(JSONArray array) {
        java.util.ArrayList<String> strings = new java.util.ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            strings.add(array.getString(index));
        }
        return List.copyOf(strings);
    }
}