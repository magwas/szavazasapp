package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizerService;
import hu.kdea.szavazas.ballotprocessor.common.InverterService;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import javax.inject.Inject;

public class ExtractGridRegionService {
    private final ImageNormalizerService imageNormalizer;
    private final ComputeRowProjectionService computeRowProjection;
    private final FindRawPeakService findRawPeak;

    @Inject
    public ExtractGridRegionService(ImageNormalizerService imageNormalizer,
                                    ComputeRowProjectionService computeRowProjection,
                                    FindRawPeakService findRawPeak) {
        this.imageNormalizer = imageNormalizer;
        this.computeRowProjection = computeRowProjection;
        this.findRawPeak = findRawPeak;
    }

    public GridRegionData apply(GrayU8 scaledGray, int qrCentreX, int qrBottomY, Double markerTopY) {
        GrayU8 inverted = InverterService.apply(scaledGray);
        RowBoundaryData boundary = FindGridBoundaryService.apply(computeRowProjection.apply(inverted), qrBottomY, markerTopY, findRawPeak);
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
        return new GridRegionData(imageNormalizer.apply(cropped), cropTop, qrCentreX);
    }
}
