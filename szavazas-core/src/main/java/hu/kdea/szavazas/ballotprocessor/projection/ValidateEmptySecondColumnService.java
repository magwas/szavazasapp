package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ValidateEmptySecondColumnService {
    @Inject
    public ValidateEmptySecondColumnService() {
    }

    public boolean apply(List<EdgeSegmentData> pairs) {
        if (pairs.size() < 2) {
            return false;
        }
        List<Integer> gaps = new ArrayList<>();
        for (int i = 0; i < pairs.size() - 1; i++) {
            gaps.add(pairs.get(i + 1).start() - pairs.get(i).start());
        }
        int firstGap = gaps.get(0);
        List<Integer> otherGaps = gaps.subList(1, gaps.size());
        if (otherGaps.isEmpty()) {
            return false;
        }
        double avgOtherGap = otherGaps.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return !(Math.abs(firstGap - 2 * avgOtherGap) > avgOtherGap * 0.5);
    }
}