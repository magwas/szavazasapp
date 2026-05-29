package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.BallotPreprocessService;
import hu.kdea.szavazas.ballotprocessor.GrayPlanarToGrayService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionService;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoDetectionStub;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class BallotPreprocessServiceTest extends TestBase implements ArucoTestData {
    private BallotPreprocessService ballotPreprocessService;
    private ArucoDetectionService arucoDetectionService;
    private GrayPlanarToGrayService grayPlanarToGrayService;
    private ImageSaverWrapper imageSaver;

    @Override
    public void setUp() {
        arucoDetectionService = ArucoDetectionStub.stub();
        grayPlanarToGrayService = GrayPlanarToGrayStub.stub();
        imageSaver = Mockito.mock(ImageSaverWrapper.class);
        ballotPreprocessService = new BallotPreprocessService(arucoDetectionService, grayPlanarToGrayService, imageSaver);
    }

    @Test
    @DisplayName("converts input planar to gray and optionally saves as debug capture")
    public void applyConvertsToGrayAndSavesDebugCapture() {
        GrayPlanarToGrayService stubGray = GrayPlanarToGrayStub.stubWithResultForTwoCalls(PLANAR_100, GRAY_100, PLANAR_80, GRAY_80);
        ArucoDetectionResultData arucoResult = new ArucoDetectionResultData(PLANAR_80, 15.0);
        ArucoDetectionService stubAruco = ArucoDetectionStub.stubWithResultForArgs(PLANAR_100, GRAY_100, arucoResult);

        BallotPreprocessService service = new BallotPreprocessService(stubAruco, stubGray, imageSaver);
        PreprocessResultData result = service.apply(PLANAR_100);

        assertNotNull(result);
        assertEquals(GRAY_80, result.scaledGray());
        assertEquals(15.0, result.markerTopY(), 0.001);
        verify(imageSaver).apply(GRAY_100, "debug_capture.jpg");
        verify(imageSaver).apply(GRAY_80, "debug_warped.jpg");
    }

    @Test
    @DisplayName("returns null when ArUco detection returns null")
    public void applyWithNullArucoDetectionReturnsNull() {
        GrayPlanarToGrayService stubGray = GrayPlanarToGrayStub.stubWithResultForArgs(PLANAR_100, GRAY_100);
        ArucoDetectionService stubAruco = ArucoDetectionStub.stubWithResultForArgs(PLANAR_100, GRAY_100, null);

        BallotPreprocessService service = new BallotPreprocessService(stubAruco, stubGray, imageSaver);
        PreprocessResultData result = service.apply(PLANAR_100);

        assertNull(result);
    }
}
