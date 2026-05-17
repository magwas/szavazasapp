package hu.kdea.szavazas.ballotprocessor.projection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class FindRawPeakService implements ProjectionConstants {
    private final MergeClosePeakService mergeClosePeakService;

    @Inject
    public FindRawPeakService(MergeClosePeakService mergeClosePeakService) {
        this.mergeClosePeakService = mergeClosePeakService;
    }

    public List<Integer> apply(float[] projection, int offset) {
        float maxValue = 0f;
        for (float value : projection) {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        if (maxValue <= 0f) {
            return Collections.emptyList();
        }
        float threshold = Math.max(maxValue * PEAK_RELATIVE_THRESHOLD, PEAK_THRESHOLD_MIN);
        List<Integer> peaks = new ArrayList<>();
        for (int i = 1; i < projection.length - 1; i++) {
            if (projection[i] > threshold && projection[i] >= projection[i - 1] && projection[i] >= projection[i + 1]) {
                peaks.add(i + offset);
            }
        }
        return peaks;
    }
}
