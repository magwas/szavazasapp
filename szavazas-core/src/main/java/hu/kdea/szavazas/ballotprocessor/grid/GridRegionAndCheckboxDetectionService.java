package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import javax.inject.Inject;

public class GridRegionAndCheckboxDetectionService {
    private final GridRegionExtractService gridRegionExtract;
    private final GridDetectorStep gridDetectorStep;
    private final ImageSaver imageSaver;

    @Inject
    public GridRegionAndCheckboxDetectionService(GridRegionExtractService gridRegionExtract, GridDetectorStep gridDetectorStep, @DebugImageSaver ImageSaver imageSaver) {
        this.gridRegionExtract = gridRegionExtract;
        this.gridDetectorStep = gridDetectorStep;
        this.imageSaver = imageSaver;
    }

    public GridDetectionResultData apply(GrayU8 warpedGray, QrData adjustedQr, Double markerTopY) {
        int qrCenterX = adjustedQr.bbox().x() + adjustedQr.bbox().width() / 2;
        int qrBottom = adjustedQr.bbox().y() + adjustedQr.bbox().height();
        GridRegion region = gridRegionExtract.apply(warpedGray, qrCenterX, qrBottom, markerTopY);
        if (region == null) {
            return null;
        }
        if (imageSaver != null) {
            imageSaver.apply(region.projectionInput(), "debug_grid_input.jpg");
        }
        java.util.List<RectangleData> checkboxes = gridDetectorStep.detect(
            region.projectionInput(),
            region.cropTop(),
            region.qrCentreX(),
            adjustedQr.numSupport() + 1,
            adjustedQr.numRows()
        );
        return checkboxes == null ? null : new GridDetectionResultData(region, checkboxes);
    }
}
