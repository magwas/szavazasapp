package hu.kdea.szavazas.ballotprocessor;

public interface BallotProcessingOutcome {
    final class Success implements BallotProcessingOutcome {
        private final BallotResultData result;

        public Success(BallotResultData result) {
            this.result = result;
        }

        public BallotResultData getResult() {
            return result;
        }
    }

    final class Failure implements BallotProcessingOutcome {
        private final BallotErrorData error;

        public Failure(BallotErrorData error) {
            this.error = error;
        }

        public BallotErrorData getError() {
            return error;
        }
    }
}
