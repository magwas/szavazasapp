package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.GridConstants;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class FindGridBoundaryService implements GridConstants {
    private final FindRawPeakService findRawPeak;

    @Inject
    public FindGridBoundaryService(FindRawPeakService findRawPeak) {
        this.findRawPeak = findRawPeak;
    }

    public RowBoundaryData apply(float[] projection, int qrBottomY, Double markerTopY) {
        int searchBottomY = calcSearchBottomY(markerTopY, projection.length);
        float[] searchProjection = new float[searchBottomY - qrBottomY + 1];
        System.arraycopy(projection, qrBottomY, searchProjection, 0, searchProjection.length);
        List<Integer> peaks = findRawPeak.apply(searchProjection, qrBottomY);
        if (peaks.size() < 2) {
            return null;
        }
        List<Integer> sorted = new ArrayList<>(peaks);
        sorted.sort(Comparator.comparingDouble((Integer peak) -> projection[peak]).reversed());
        int topPeak = Math.min(sorted.get(0), sorted.get(1));
        int bottomPeak = Math.max(sorted.get(0), sorted.get(1));
        int cropTop = Math.max(0, topPeak + GridConstants.BOUNDARY_MARGIN);
        int cropBottom = Math.min(projection.length - 1, bottomPeak - GridConstants.BOUNDARY_MARGIN);
        return new RowBoundaryData(cropTop, cropBottom);
    }

    private int calcSearchBottomY(Double markerTopY, int height) {
        int margin = 20;
        if (markerTopY != null) {
            return Math.min(height - 1, (int) (markerTopY - margin));
        }
        return (int) (height * 0.95);
    }
}
