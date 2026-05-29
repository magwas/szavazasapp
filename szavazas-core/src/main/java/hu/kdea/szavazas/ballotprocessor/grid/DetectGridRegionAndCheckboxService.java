package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import javax.inject.Inject;

public class DetectGridRegionAndCheckboxService {
    private final ExtractGridRegionService extractGridRegion;
    private final DetectGridService detectGrid;
    private final ImageSaverWrapper imageSaver;

    @Inject
    public DetectGridRegionAndCheckboxService(ExtractGridRegionService extractGridRegion, DetectGridService detectGrid, @DebugImageSaver ImageSaverWrapper imageSaver) {
        this.extractGridRegion = extractGridRegion;
        this.detectGrid = detectGrid;
        this.imageSaver = imageSaver;
    }

    public GridDetectionResultData apply(GrayU8 warpedGray, QrData adjustedQr, Double markerTopY) {
        int qrCenterX = adjustedQr.bbox().x() + adjustedQr.bbox().width() / 2;
        int qrBottom = adjustedQr.bbox().y() + adjustedQr.bbox().height();
        GridRegionData region = extractGridRegion.apply(warpedGray, qrCenterX, qrBottom, markerTopY);
        if (region == null) {
            return null;
        }
        if (imageSaver != null) {
            imageSaver.apply(region.projectionInput(), "debug_grid_input.jpg");
        }
        java.util.List<RectangleData> checkboxes = detectGrid.apply(
            region.projectionInput(),
            region.cropTop(),
            region.qrCentreX(),
            adjustedQr.voteMetadata().supportColumnCount() + 1,
            adjustedQr.voteMetadata().candidateCount()
        );
        return checkboxes == null ? null : new GridDetectionResultData(region, checkboxes);
    }
}
