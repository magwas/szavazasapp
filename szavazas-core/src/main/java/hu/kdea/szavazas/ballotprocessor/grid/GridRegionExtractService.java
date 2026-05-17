package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizerService;
import hu.kdea.szavazas.ballotprocessor.common.InverterService;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.projection.RowProjectionComputer;
import javax.inject.Inject;

public class GridRegionExtractService {
    private final ImageNormalizerService imageNormalizer;

    @Inject
    public GridRegionExtractService(ImageNormalizerService imageNormalizer) {
        this.imageNormalizer = imageNormalizer;
    }

    public GridRegion apply(GrayU8 scaledGray, int qrCentreX, int qrBottomY, Double markerTopY) {
        GrayU8 inverted = InverterService.apply(scaledGray);
        RowBoundaryData boundary = GridBoundaryFinder.find(RowProjectionComputer.compute(inverted), qrBottomY, markerTopY);
        if (boundary == null) {
            return null;
        }
        int cropTop = boundary.cropTop();
        int cropBottom = boundary.cropBottom();
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
        return new GridRegion(imageNormalizer.apply(cropped), cropTop, qrCentreX);
    }
}
