package hu.kdea.szavazas.ballotprocessor.qr;

import javax.inject.Inject;

public class ParseQrFieldService {
    @Inject
    public ParseQrFieldService() {
    }

    public int apply(String[] parts, int index, String fieldName) {
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
}