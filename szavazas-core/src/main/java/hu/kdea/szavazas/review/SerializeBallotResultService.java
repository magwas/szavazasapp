package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
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
        try {
            JSONArray ballots = new JSONArray();
            ballots.put(toJson(ballotResultData));
            return ballots.toString(2);
        } catch (JSONException exception) {
            throw new IllegalStateException("Failed to serialize ballot result", exception);
        }
    }

    public String apply(String existingContent, BallotResultData ballotResultData) {
        try {
            JSONArray ballots = existingContent == null || existingContent.isBlank() ? new JSONArray() : new JSONArray(existingContent);
            ballots.put(toJson(ballotResultData));
            return ballots.toString(2);
        } catch (JSONException exception) {
            throw new IllegalStateException("Failed to serialize ballot result", exception);
        }
    }

    private JSONObject toJson(BallotResultData ballotResultData) throws JSONException {
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
