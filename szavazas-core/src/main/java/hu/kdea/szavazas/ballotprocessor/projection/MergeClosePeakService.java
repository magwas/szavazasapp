package hu.kdea.szavazas.ballotprocessor.projection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class MergeClosePeakService implements ProjectionConstants {

    @Inject
    public MergeClosePeakService() {
    }

    public List<Integer> apply(List<Integer> peaks) {
        return apply(peaks, MERGE_CLOSE_PEAKS_DIST);
    }

    public List<Integer> apply(List<Integer> peaks, int minDist) {
        if (peaks.size() < 2) {
            return peaks;
        }
        List<Integer> sorted = new ArrayList<>(peaks);
        Collections.sort(sorted);
        List<Integer> merged = new ArrayList<>();
        int start = sorted.get(0);
        int count = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) - sorted.get(i - 1) < minDist) {
                start += sorted.get(i);
                count++;
            } else {
                merged.add(start / count);
                start = sorted.get(i);
                count = 1;
            }
        }
        merged.add(start / count);
        return merged;
    }
}
