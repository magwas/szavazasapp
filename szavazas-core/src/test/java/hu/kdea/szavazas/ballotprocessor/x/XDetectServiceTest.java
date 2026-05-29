package hu.kdea.szavazas.ballotprocessor.x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import hu.kdea.szavazas.ballotprocessor.common.PointData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class XDetectServiceTest extends TestBase implements XDetectConstants, XDetectTestData {

    private XDetectService xDetectService;
    private BinaryImageOpsWrapper binaryImageOpsWrapper;

    @Override
    public void setUp() {
        binaryImageOpsWrapper = BinaryImageOpsWrapperStub.stubWithThinIdentity();
        xDetectService = new XDetectService(binaryImageOpsWrapper, new FindBranchPointsService());
    }

    @Test
    @DisplayName("returns not detected for too small rectangle")
    public void applyWithSmallRect() {
        XDetectionResultData result = xDetectService.apply(EMPTY_BINARY, MIN_SIZE_OUTER_RECT);
        assertNotNull(result);
        assertFalse(result.detected());
        assertNull(result.debug());
        verify(binaryImageOpsWrapper, never()).thin(any(), eq(-1), any());
        verify(binaryImageOpsWrapper, never()).erode8(any(), eq(ERODE_ITERATIONS), any());
    }

    @Test
    @DisplayName("returns debug data with no detection for empty inner cell")
    public void applyWithEmptyImage() {
        XDetectionResultData result = xDetectService.apply(EMPTY_BINARY, LARGE_OUTER_RECT);
        assertNotNull(result);
        assertFalse(result.detected());
        assertNotNull(result.debug());
        assertEquals(LARGE_OUTER_RECT, result.debug().outerRect());
        assertEquals(LARGE_INNER_RECT, result.debug().innerRect());
        assertNull(result.debug().erodedCell());
        assertTrue(result.debug().branchPoints().isEmpty());
        assertFalse(result.debug().xDetected());
        verify(binaryImageOpsWrapper, never()).erode8(any(), eq(ERODE_ITERATIONS), any());
    }

    @Test
    @DisplayName("returns detected when skeleton has enough branch points")
    public void applyDetectsXWhenSkeletonHasEnoughBranches() {
        int expectedBranchPoints = XDetectTestUtil.countBranchPoints(DENSE_SKELETON);
        binaryImageOpsWrapper = BinaryImageOpsWrapperStub.stubWithThinResult(DENSE_SKELETON);
        xDetectService = new XDetectService(binaryImageOpsWrapper, new FindBranchPointsService());
        XDetectionResultData result = xDetectService.apply(FILLED_BINARY, LARGE_OUTER_RECT);
        assertNotNull(result);
        assertTrue(result.detected());
        assertNotNull(result.debug());
        assertSame(DENSE_SKELETON, result.debug().skeleton());
        assertEquals(expectedBranchPoints, result.debug().branchPoints().size());
        assertEquals(new PointData(1, 1), result.debug().branchPoints().get(0));
        assertTrue(result.debug().xDetected());
        assertNull(result.debug().erodedCell());
        verify(binaryImageOpsWrapper).thin(any(), eq(-1), any());
        verify(binaryImageOpsWrapper, never()).erode8(any(), eq(ERODE_ITERATIONS), any());
    }

    @Test
    @DisplayName("copies cropped cell into debug data before thinning")
    public void applyCopiesInnerRectPixelsIntoOriginalCell() {
        XDetectionResultData result = xDetectService.apply(XDetectTestImageFactory.croppedCellSource(CROP_TEST_OUTER_RECT), CROP_TEST_OUTER_RECT);
        assertNotNull(result.debug());
        assertEquals(CROP_TEST_INNER_WIDTH, result.debug().originalCell().width);
        assertEquals(CROP_TEST_INNER_HEIGHT, result.debug().originalCell().height);
        assertEquals(1, result.debug().originalCell().get(0, 0));
        assertEquals(CROP_TEST_INNER_WIDTH * CROP_TEST_INNER_HEIGHT, result.debug().originalCell().get(CROP_TEST_INNER_WIDTH - 1, CROP_TEST_INNER_HEIGHT - 1));
    }
}
