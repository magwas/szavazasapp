package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizer;
import hu.kdea.szavazas.ballotprocessor.common.Inverter;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import hu.kdea.szavazas.ballotprocessor.projection.RowProjectionComputer;
import javax.inject.Inject;
import kotlin.Pair;

public class GridRegionExtractService {
    @Inject
    public GridRegionExtractService() {
    }

    public GridRegion apply(GrayU8 scaledGray, int qrCentreX, int qrBottomY, Double markerTopY) {
        GrayU8 inverted = Inverter.invert(scaledGray);
        Pair<Integer, Integer> boundary = GridBoundaryFinder.find(RowProjectionComputer.compute(inverted), qrBottomY, markerTopY);
        if (boundary == null) {
            return null;
        }
        int cropTop = boundary.getFirst();
        int cropBottom = boundary.getSecond();
        int cropWidth = scaledGray.width - qrCentreX;
        int cropHeight = cropBottom - cropTop + 1;
        if (cropWidth <= 0 || cropHeight <= 0) {
            return null;
        }
        GrayU8 cropped = new GrayU8(cropWidth, cropHeight);
        for (int y = 0; y < cropHeight; y++) {
            for (int x = 0; x < cropWidth; x++) {
                cropped.set(x, y, scaledGray.get(qrCentreX + x, cropTop + y));
            }
        }
        return new GridRegion(ImageNormalizer.normalizeAndThreshold(cropped), cropTop, qrCentreX);
    }
}
