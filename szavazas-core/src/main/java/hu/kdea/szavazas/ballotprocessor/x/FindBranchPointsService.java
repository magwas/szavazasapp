package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindBranchPointsService {
    @Inject
    public FindBranchPointsService() {
    }

    public List<PointData> apply(GrayU8 skeleton) {
        List<PointData> points = new ArrayList<>();
        for (int y = 1; y < skeleton.height - 1; y++) {
            for (int x = 1; x < skeleton.width - 1; x++) {
                if (skeleton.get(x, y) != 0 && neighbourCount(skeleton, x, y) >= 3) {
                    points.add(new PointData(x, y));
                }
            }
        }
        return points;
    }

    private int neighbourCount(GrayU8 skeleton, int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (skeleton.get(x + dx, y + dy) != 0) {
                    count++;
                }
            }
        }
        return count;
    }
}