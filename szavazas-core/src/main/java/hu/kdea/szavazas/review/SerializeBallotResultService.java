package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SerializeBallotResultService {
    private static final String TAG = "SerializeBallotResult";
    private final LoggerWrapper loggerWrapper;

    @Inject
    public SerializeBallotResultService(LoggerWrapper loggerWrapper) {
        this.loggerWrapper = loggerWrapper;
    }

    public String apply(BallotResultData ballotResultData) {
        return apply(null, ballotResultData);
    }

    public String apply(String existingContent, BallotResultData ballotResultData) {
        try {
            JSONObject content = content(existingContent);
            JSONArray ballots = ballots(content);
            JSONObject vote = vote(content, ballotResultData.voteMetadata(), ballotResultData.raw());
            ballots.put(ballot(ballotResultData));
            content.put("vote", vote);
            content.put("ballots", ballots);
            return content.toString(2);
        } catch (JSONException exception) {
            throw new IllegalStateException("Failed to serialize ballot result", exception);
        }
    }

    private JSONObject content(String existingContent) throws JSONException {
        return existingContent == null || existingContent.isBlank() ? new JSONObject() : new JSONObject(existingContent);
    }

    private JSONArray ballots(JSONObject content) throws JSONException {
        return content.has("ballots") ? content.getJSONArray("ballots") : new JSONArray();
    }

    private JSONObject vote(JSONObject content, VoteMetadataData voteMetadataData, String raw) throws JSONException {
        if (!content.has("vote")) {
            return vote(voteMetadataData);
        }
        JSONObject storedVote = content.getJSONObject("vote");
        if (conflicts(storedVote, voteMetadataData)) {
            loggerWrapper.w(
                TAG,
                "Metadata conflict while preserving stored vote metadata: stored vote metadata differs from incoming ballot metadata; raw=" + raw
            );
        }
        return storedVote;
    }

    private boolean conflicts(JSONObject storedVote, VoteMetadataData voteMetadataData) throws JSONException {
        return !(
            equals(storedVote.optString("voteId"), voteMetadataData.voteId()) &&
            equals(storedVote.optString("voteName"), voteMetadataData.voteName()) &&
            storedVote.optInt("candidateCount") == voteMetadataData.candidateCount() &&
            equals(storedVote.optJSONArray("candidates"), new JSONArray(voteMetadataData.candidates())) &&
            storedVote.optInt("supportColumnCount") == voteMetadataData.supportColumnCount() &&
            equals(storedVote.optJSONArray("issuedBallotIds"), new JSONArray(voteMetadataData.issuedBallotIds()))
        );
    }

    private boolean equals(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }

    private boolean equals(JSONArray first, JSONArray second) throws JSONException {
        if (first == null || second == null || first.length() != second.length()) {
            return first == second;
        }
        for (int index = 0; index < first.length(); index++) {
            if (!equals(first.get(index), second.get(index))) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Object first, Object second) throws JSONException {
        if (first == null || second == null) {
            return first == second;
        }
        if (first instanceof JSONObject firstObject && second instanceof JSONObject secondObject) {
            return equals(firstObject, secondObject);
        }
        if (first instanceof JSONArray firstArray && second instanceof JSONArray secondArray) {
            return equals(firstArray, secondArray);
        }
        return first.equals(second);
    }

    private boolean equals(JSONObject first, JSONObject second) throws JSONException {
        if (first.length() != second.length()) {
            return false;
        }
        JSONArray names = first.names();
        if (names == null) {
            return true;
        }
        for (int index = 0; index < names.length(); index++) {
            String name = names.getString(index);
            if (!second.has(name) || !equals(first.get(name), second.get(name))) {
                return false;
            }
        }
        return true;
    }

    private JSONObject vote(VoteMetadataData voteMetadataData) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("voteId", voteMetadataData.voteId());
        jsonObject.put("voteName", voteMetadataData.voteName());
        jsonObject.put("candidateCount", voteMetadataData.candidateCount());
        jsonObject.put("candidates", new JSONArray(voteMetadataData.candidates()));
        jsonObject.put("supportColumnCount", voteMetadataData.supportColumnCount());
        jsonObject.put("issuedBallotIds", new JSONArray(voteMetadataData.issuedBallotIds()));
        return jsonObject;
    }

    private JSONObject ballot(BallotResultData ballotResultData) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("raw", ballotResultData.raw());
        jsonObject.put("numSupport", ballotResultData.numSupport());
        jsonObject.put("numRows", ballotResultData.numRows());
        jsonObject.put("xCells", xCells(ballotResultData.xCells()));
        return jsonObject;
    }

    private JSONArray xCells(List<CellPositionData> xCells) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (CellPositionData cellPositionData : xCells) {
            JSONObject cell = new JSONObject();
            cell.put("row", cellPositionData.row());
            cell.put("col", cellPositionData.col());
            jsonArray.put(cell);
        }
        return jsonArray;
    }
}
