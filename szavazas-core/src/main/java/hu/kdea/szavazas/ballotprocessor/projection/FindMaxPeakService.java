package hu.kdea.szavazas.ballotprocessor.projection;

import javax.inject.Inject;

public class FindMaxPeakService {

    @Inject
    public FindMaxPeakService() {
    }

    public int apply(float[] projection, int fromIdx, int toIdx) {
        int maxIdx = fromIdx;
        for (int i = fromIdx; i <= toIdx; i++) {
            if (projection[i] > projection[maxIdx]) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}
