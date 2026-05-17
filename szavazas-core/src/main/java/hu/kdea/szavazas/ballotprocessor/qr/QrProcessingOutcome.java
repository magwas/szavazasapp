package hu.kdea.szavazas.ballotprocessor.qr;

public interface QrProcessingOutcome {
    final class Success implements QrProcessingOutcome {
        private final QrResultData result;

        public Success(QrResultData result) {
            this.result = result;
        }

        public QrResultData getResult() {
            return result;
        }
    }

    final class Failure implements QrProcessingOutcome {
        private final QrErrorData error;

        public Failure(QrErrorData error) {
            this.error = error;
        }

        public QrErrorData getError() {
            return error;
        }
    }
}
