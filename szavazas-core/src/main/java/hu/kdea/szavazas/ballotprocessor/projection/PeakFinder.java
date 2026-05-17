package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.GridConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PeakFinder {
    private PeakFinder() {
    }

    public static List<Integer> findRawPeaks(float[] projection, int offset) {
        float maxValue = 0f;
        for (float value : projection) {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        if (maxValue <= 0f) {
            return Collections.emptyList();
        }
        float threshold = Math.max(maxValue * GridConstants.PEAK_RELATIVE_THRESHOLD, GridConstants.PEAK_THRESHOLD_MIN);
        List<Integer> peaks = new ArrayList<>();
        for (int i = 1; i < projection.length - 1; i++) {
            if (projection[i] > threshold && projection[i] >= projection[i - 1] && projection[i] >= projection[i + 1]) {
                peaks.add(i + offset);
            }
        }
        return peaks;
    }

    public static List<Integer> mergeClosePeaks(List<Integer> peaks) {
        return mergeClosePeaks(peaks, GridConstants.MERGE_CLOSE_PEAKS_DIST);
    }

    public static List<Integer> mergeClosePeaks(List<Integer> peaks, int minDist) {
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

    public static int findMaxPeak(float[] projection, int fromIdx, int toIdx) {
        int maxIdx = fromIdx;
        for (int i = fromIdx; i <= toIdx; i++) {
            if (projection[i] > projection[maxIdx]) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}
