package hu.kdea.szavazas.ballotprocessor.x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.test.LoggerWrapperStub;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class XMarkDetectServiceTest extends TestBase implements XDetectTestData {
    private XMarkDetectService xMarkDetectService;
    private XDetectService xDetectService;
    private XMarkDebugRendererWrapper xMarkDebugRendererWrapper;
    private LoggerWrapper loggerWrapper;

    @Override
    public void setUp() {
        xDetectService = XDetectStub.stub();
        xMarkDebugRendererWrapper = XMarkDebugRendererWrapperStub.stub();
        loggerWrapper = LoggerWrapperStub.stub();
        xMarkDetectService = new XMarkDetectService(xDetectService, xMarkDebugRendererWrapper, loggerWrapper);
    }

    @Test
    @DisplayName("converts each checkbox from full-image coordinates to local crop coordinates before X detection")
    public void applyConvertsCheckboxToLocalCoordinates() {
        GrayU8 projectionInput = new GrayU8(100, 100);
        List<RectangleData> fullCheckboxes = List.of(
            new RectangleData(50, 60, 20, 20),
            new RectangleData(80, 60, 20, 20)
        );
        int qrCentreX = 30;
        int cropTop = 20;
        RectangleData expectedLocalRect1 = new RectangleData(20, 40, 20, 20);
        RectangleData expectedLocalRect2 = new RectangleData(50, 40, 20, 20);
        xDetectService = XDetectStub.stubWithTwoResults(projectionInput, expectedLocalRect1, new XDetectionResultData(true, null), expectedLocalRect2, new XDetectionResultData(false, null));
        xMarkDetectService = new XMarkDetectService(xDetectService, xMarkDebugRendererWrapper, loggerWrapper);

        List<CellPositionData> marks = xMarkDetectService.apply(projectionInput, fullCheckboxes, qrCentreX, cropTop, 2, 2);

        assertEquals(1, marks.size());
        assertEquals(0, marks.get(0).row());
        assertEquals(0, marks.get(0).col());
    }

    @Test
    @DisplayName("detected X marks are converted to row and column positions using expectedCols")
    public void applyConvertsMarksToRowAndColumn() {
        GrayU8 projectionInput = new GrayU8(100, 100);
        List<RectangleData> fullCheckboxes = List.of(
            new RectangleData(50, 60, 20, 20),
            new RectangleData(80, 60, 20, 20),
            new RectangleData(50, 90, 20, 20),
            new RectangleData(80, 90, 20, 20)
        );
        xDetectService = XDetectStub.stubWithAnyResult(new XDetectionResultData(true, null));
        xMarkDetectService = new XMarkDetectService(xDetectService, xMarkDebugRendererWrapper, loggerWrapper);

        List<CellPositionData> marks = xMarkDetectService.apply(projectionInput, fullCheckboxes, 30, 20, 2, 2);

        assertEquals(4, marks.size());
        assertEquals(0, marks.get(0).row());
        assertEquals(0, marks.get(0).col());
        assertEquals(0, marks.get(1).row());
        assertEquals(1, marks.get(1).col());
        assertEquals(1, marks.get(2).row());
        assertEquals(0, marks.get(2).col());
        assertEquals(1, marks.get(3).row());
        assertEquals(1, marks.get(3).col());
    }

    @Test
    @DisplayName("debug data is accumulated and rendered once after processing all cells")
    public void applyRendersDebugDataAfterAllCells() {
        GrayU8 projectionInput = new GrayU8(100, 100);
        List<RectangleData> fullCheckboxes = List.of(
            new RectangleData(50, 60, 20, 20)
        );
        CellDebugData debugData = new CellDebugData(
            new RectangleData(50, 60, 20, 20),
            new RectangleData(52, 62, 16, 16),
            new GrayU8(16, 16),
            null,
            new GrayU8(16, 16),
            List.of(),
            true
        );
        xDetectService = XDetectStub.stubWithAnyResult(new XDetectionResultData(true, debugData));
        xMarkDetectService = new XMarkDetectService(xDetectService, xMarkDebugRendererWrapper, loggerWrapper);

        xMarkDetectService.apply(projectionInput, fullCheckboxes, 30, 20, 2, 2);

        verify(xMarkDebugRendererWrapper).render(eq(projectionInput), anyList());
    }

    @Test
    @DisplayName("debug logging occurs when detection debug data is present")
    public void applyLogsDebugData() {
        GrayU8 projectionInput = new GrayU8(100, 100);
        List<RectangleData> fullCheckboxes = List.of(
            new RectangleData(50, 60, 20, 20)
        );
        CellDebugData debugData = new CellDebugData(
            new RectangleData(50, 60, 20, 20),
            new RectangleData(52, 62, 16, 16),
            new GrayU8(16, 16),
            null,
            new GrayU8(16, 16),
            List.of(),
            true
        );
        xDetectService = XDetectStub.stubWithAnyResult(new XDetectionResultData(true, debugData));
        xMarkDetectService = new XMarkDetectService(xDetectService, xMarkDebugRendererWrapper, loggerWrapper);

        xMarkDetectService.apply(projectionInput, fullCheckboxes, 30, 20, 2, 2);

        verify(loggerWrapper).d(eq("XDetector"), any());
    }
}
