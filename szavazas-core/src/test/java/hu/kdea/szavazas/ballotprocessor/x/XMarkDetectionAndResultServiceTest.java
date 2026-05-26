package hu.kdea.szavazas.ballotprocessor.x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class XMarkDetectionAndResultServiceTest extends TestBase implements XDetectTestData {

    private XMarkDetectionAndResultService xMarkDetectionAndResultService;
    private XMarkDetectService xMarkDetectService;

    @Override
    public void setUp() {
        xMarkDetectService = Mockito.mock(XMarkDetectService.class);
        xMarkDetectionAndResultService = new XMarkDetectionAndResultService(xMarkDetectService);
    }

    @Test
    @DisplayName("The review screen uses the detected ballot row count instead of the QR row count")
    public void applyReturnsDetectedBallotRowCount() {
        GridRegionData region = Mockito.mock(GridRegionData.class);
        when(region.projectionInput()).thenReturn(null);
        when(region.qrCentreX()).thenReturn(0);
        when(region.cropTop()).thenReturn(0);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        when(gridResult.region()).thenReturn(region);
        when(gridResult.checkboxes()).thenReturn(List.of(new RectangleData(0, 0, 10, 10), new RectangleData(0, 10, 10, 10)));
        QrData qrData = new QrData("raw", SAMPLE_VOTE_METADATA, null);
        when(xMarkDetectService.apply(null, List.of(new RectangleData(0, 0, 10, 10), new RectangleData(0, 10, 10, 10)), 0, 0, 3, 3))
            .thenReturn(SAMPLE_MARKS);
        XMarkDetectionResultData result = xMarkDetectionAndResultService.apply(gridResult, qrData);
        assertNotNull(result);
        assertEquals(SAMPLE_MARKS, result.marks());
        assertNotNull(result.ballotResult());
        assertEquals("raw", result.ballotResult().raw());
        assertEquals(SAMPLE_VOTE_METADATA, result.ballotResult().voteMetadata());
        assertEquals(0, result.ballotResult().numRows());
    }

    @Test
    @DisplayName("returns empty marks when no X detected")
    public void applyWithNoMarks() {
        GridRegionData region = Mockito.mock(GridRegionData.class);
        when(region.projectionInput()).thenReturn(null);
        when(region.qrCentreX()).thenReturn(0);
        when(region.cropTop()).thenReturn(0);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        when(gridResult.region()).thenReturn(region);
        when(gridResult.checkboxes()).thenReturn(List.of());
        QrData qrData = new QrData("raw", SAMPLE_VOTE_METADATA, null);
        when(xMarkDetectService.apply(null, List.of(), 0, 0, 3, 3))
            .thenReturn(List.of());
        XMarkDetectionResultData result = xMarkDetectionAndResultService.apply(gridResult, qrData);
        assertNotNull(result);
        assertEquals(0, result.marks().size());
    }
}
