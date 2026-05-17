package hu.kdea.szavazas;

import android.content.Context;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BallotFileRepository {
    private final Context context;

    public BallotFileRepository(Context context) {
        this.context = context;
    }

    public void save(BallotResultData ballotResultData) {
        try {
            File directory = context.getExternalFilesDir(null);
            if (directory == null) {
                directory = context.getFilesDir();
            }
            directory.mkdirs();
            File file = new File(directory, voteName(ballotResultData.raw()) + ".json");
            JSONArray ballots = file.exists() ? new JSONArray(read(file)) : new JSONArray();
            ballots.put(toJson(ballotResultData));
            write(file, ballots.toString(2));
        } catch (IOException | JSONException exception) {
            throw new IllegalStateException("Failed to save ballot result", exception);
        }
    }

    private String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private void write(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private String voteName(String raw) {
        int separator = raw.indexOf('-');
        return separator < 0 ? raw : raw.substring(0, separator);
    }

    private JSONObject toJson(BallotResultData ballotResultData) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("raw", ballotResultData.raw());
        jsonObject.put("numSupport", ballotResultData.numSupport());
        jsonObject.put("numRows", ballotResultData.numRows());
        JSONArray xCells = new JSONArray();
        for (CellPositionData cellPositionData : ballotResultData.xCells()) {
            JSONObject cell = new JSONObject();
            cell.put("row", cellPositionData.row());
            cell.put("col", cellPositionData.col());
            xCells.put(cell);
        }
        jsonObject.put("xCells", xCells);
        return jsonObject;
    }
}
