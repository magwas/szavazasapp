package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import javax.inject.Inject;

public class GridRegionAndCheckboxDetectionService {
    private final GridRegionExtractService gridRegionExtract;
    private final GridDetectorStep gridDetectorStep;
    private final ImageSaver imageSaver;

    @Inject
    public GridRegionAndCheckboxDetectionService(GridRegionExtractService gridRegionExtract, GridDetectorStep gridDetectorStep, ImageSaver imageSaver) {
        this.gridRegionExtract = gridRegionExtract;
        this.gridDetectorStep = gridDetectorStep;
        this.imageSaver = imageSaver;
    }

    public GridDetectionResultData apply(GrayU8 warpedGray, QrData adjustedQr, Double markerTopY) {
        GridRegion region = gridRegionExtract.apply(warpedGray, adjustedQr.getBbox().centerX(), adjustedQr.getBbox().bottom(), markerTopY);
        if (region == null) {
            return null;
        }
        if (imageSaver != null) {
            imageSaver.save(region.getProjectionInput(), "debug_grid_input.jpg");
        }
        java.util.List<hu.kdea.szavazas.ballotprocessor.common.Rect> checkboxes = gridDetectorStep.detect(
            region.getProjectionInput(),
            region.getCropTop(),
            region.getQrCentreX(),
            adjustedQr.getNumSupport() + 1,
            adjustedQr.getNumRows()
        );
        return checkboxes == null ? null : new GridDetectionResultData(region, checkboxes);
    }
}
