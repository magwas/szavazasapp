package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;

public class ParseQrResultService {
    private final ValidateQrResultService validateQrResult;
    private final ParseQrFieldService parseQrField;
    private final ComputeQrBoundingBoxService computeQrBoundingBox;
    private final ExtractQrVoteIdService extractQrVoteId;

    @Inject
    public ParseQrResultService(
        ValidateQrResultService validateQrResult,
        ParseQrFieldService parseQrField,
        ComputeQrBoundingBoxService computeQrBoundingBox,
        ExtractQrVoteIdService extractQrVoteId
    ) {
        this.validateQrResult = validateQrResult;
        this.parseQrField = parseQrField;
        this.computeQrBoundingBox = computeQrBoundingBox;
        this.extractQrVoteId = extractQrVoteId;
    }

    public QrData apply(Result result) {
        validateQrResult.apply(result);
        String raw = result.getText();
        String[] parts = raw.split("-");
        int numSupport = parseQrField.apply(parts, 1, "support count");
        int numRows = parseQrField.apply(parts, 2, "row count");
        String voteId = extractQrVoteId.apply(raw);
        VoteMetadataData voteMetadataData = new VoteMetadataData(voteId, voteId, numRows, List.of(), numSupport, List.of(raw));
        return new QrData(raw, voteMetadataData, computeQrBoundingBox.apply(result.getResultPoints()));
    }
}