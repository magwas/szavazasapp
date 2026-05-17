package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;

public final class XDetectTestUtil {

    private XDetectTestUtil() {
    }

    public static int countBranchPoints(GrayU8 skeleton) {
        int count = 0;
        for (int y = 1; y < skeleton.height - 1; y++) {
            for (int x = 1; x < skeleton.width - 1; x++) {
                if (skeleton.get(x, y) != 0 && hasAtLeastThreeNeighbours(skeleton, x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean hasAtLeastThreeNeighbours(GrayU8 skeleton, int x, int y) {
        int neighbours = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (skeleton.get(x + dx, y + dy) != 0) {
                    neighbours++;
                }
            }
        }
        return neighbours >= 3;
    }
}
