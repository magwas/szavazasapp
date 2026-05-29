package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import javax.inject.Inject;

public class ValidateQrResultService {
    @Inject
    public ValidateQrResultService() {
    }

    public void apply(Result result) {
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
        for (ResultPoint point : result.getResultPoints()) {
            if (point == null) {
                throw new IllegalArgumentException("QR result points must not contain nulls");
            }
        }
    }
}