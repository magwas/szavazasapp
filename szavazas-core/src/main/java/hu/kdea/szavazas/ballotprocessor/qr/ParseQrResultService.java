package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;

public class ParseQrResultService {
    @Inject
    public ParseQrResultService() {
    }

    public QrData apply(Result result) {
        Result validatedResult = validateResult(result);
        String raw = validatedResult.getText();
        String[] parts = raw.split("-");
        int numSupport = parsePositive(parts, 1, "support count");
        int numRows = parsePositive(parts, 2, "row count");
        VoteMetadataData voteMetadataData = new VoteMetadataData(voteId(raw), voteName(raw), numRows, List.of(), numSupport, List.of(raw));
        return new QrData(raw, voteMetadataData, box(validatedResult.getResultPoints()));
    }

    private Result validateResult(Result result) {
        if (result == null) {
            throw new IllegalArgumentException("QR result must not be null");
        }
        if (result.getText() == null) {
            throw new IllegalArgumentException("QR text must not be null");
        }
        if (result.getResultPoints() == null) {
            throw new IllegalArgumentException("QR result points must not be null");
        }
        if (result.getResultPoints().length == 0) {
            throw new IllegalArgumentException("QR result points must not be empty");
        }
        return result;
    }

    private int parsePositive(String[] parts, int index, String fieldName) {
        if (parts.length <= index || parts[index].isBlank()) {
            throw new IllegalArgumentException("QR " + fieldName + " is missing");
        }
        try {
            int value = Integer.parseInt(parts[index]);
            if (value <= 0) {
                throw new IllegalArgumentException("QR " + fieldName + " must be positive");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("QR " + fieldName + " must be numeric", exception);
        }
    }

    private RectangleData box(ResultPoint[] resultPoints) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (ResultPoint point : resultPoints) {
            if (point == null) {
                throw new IllegalArgumentException("QR result points must not contain nulls");
            }
            minX = Math.min(minX, (int) point.getX());
            minY = Math.min(minY, (int) point.getY());
            maxX = Math.max(maxX, (int) point.getX());
            maxY = Math.max(maxY, (int) point.getY());
        }
        return new RectangleData(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private String voteId(String raw) {
        return voteName(raw);
    }

    private String voteName(String raw) {
        int separator = raw.indexOf('-');
        return separator < 0 ? raw : raw.substring(0, separator);
    }
}
