package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SerializeBallotResultService {
    @Inject
    public SerializeBallotResultService() {
    }

    public String apply(BallotResultData ballotResultData) {
        return apply(null, ballotResultData);
    }

    public String apply(String existingContent, BallotResultData ballotResultData) {
        try {
            JSONObject content = content(existingContent);
            JSONArray ballots = ballots(content);
            ballots.put(ballot(ballotResultData));
            content.put("vote", vote(content, ballotResultData));
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

    private JSONObject vote(JSONObject content, BallotResultData ballotResultData) throws JSONException {
        return content.has("vote") ? mergedVote(content.getJSONObject("vote"), ballotResultData.voteMetadata()) : vote(ballotResultData.voteMetadata());
    }

    private JSONObject mergedVote(JSONObject existingVote, VoteMetadataData voteMetadataData) throws JSONException {
        JSONObject jsonObject = new JSONObject(existingVote.toString());
        jsonObject.put("voteId", voteMetadataData.voteId());
        jsonObject.put("voteName", voteMetadataData.voteName());
        jsonObject.put("candidateCount", voteMetadataData.candidateCount());
        jsonObject.put("candidates", new JSONArray(voteMetadataData.candidates()));
        jsonObject.put("supportColumnCount", voteMetadataData.supportColumnCount());
        jsonObject.put("issuedBallotIds", new JSONArray(voteMetadataData.issuedBallotIds()));
        return jsonObject;
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
