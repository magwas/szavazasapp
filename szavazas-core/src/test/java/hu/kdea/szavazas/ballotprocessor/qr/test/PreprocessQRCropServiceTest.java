package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CropService;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.test.PreprocessQRStub;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class PreprocessQRCropServiceTest extends TestBase implements QrDecoderTestData {
    private PreprocessQRCropService preprocessQRCropService;
    private CropService cropService;
    private ImageSaverWrapper imageSaver;
    private QrProcessingService qrProcessingService;

    @Override
    public void setUp() {
        cropService = new CropService();
        imageSaver = Mockito.mock(ImageSaverWrapper.class);
        PreprocessQRService preprocessQR = PreprocessQRStub.stub();
        preprocessQRCropService = new PreprocessQRCropService(cropService, imageSaver, preprocessQR);
        qrProcessingService = QrProcessingStub.stub();
    }

    @Test
    @DisplayName("derives QR crop rectangle from ballot width and height using fixed proportions")
    public void applyDerivesCropFromFixedProportions() {
        int expectedCropX = 2 * 500 / 5;
        int expectedCropY = 0;
        int expectedCropWidth = 500 / 5;
        int expectedCropHeight = 400 / 4;
        GrayU8 qrCrop = new GrayU8(expectedCropWidth, expectedCropHeight);
        for (int y = 0; y < qrCrop.height; y++) {
            for (int x = 0; x < qrCrop.width; x++) {
                qrCrop.set(x, y, 128);
            }
        }
        QrData qrData = new QrData("ballot-5-12", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrProcessingService stubQr = QrProcessingStub.stubWithResult(new QrProcessingOutcomeData(qrData, null));

        QrCropResultData result = preprocessQRCropService.apply(WARPED_GRAY_500X400, stubQr);

        assertNotNull(result);
        assertNotNull(result.adjustedQr());
        assertEquals("ballot-5-12", result.adjustedQr().raw());
        assertEquals(SAMPLE_VOTE_METADATA, result.adjustedQr().voteMetadata());
    }

    @Test
    @DisplayName("returns null QR result when QR processing returns null")
    public void applyWithNullQrResultReturnsNull() {
        QrProcessingService stubQr = QrProcessingStub.stubWithResult(new QrProcessingOutcomeData(null, null));

        QrCropResultData result = preprocessQRCropService.apply(WARPED_GRAY_500X400, stubQr);

        assertNotNull(result);
        assertNull(result.adjustedQr());
    }

    @Test
    @DisplayName("translates QR bbox back into warped image coordinates")
    public void applyTranslatesBboxToWarpedCoordinates() {
        int cropX = 2 * 500 / 5;
        int cropY = 0;
        QrData qrData = new QrData("ballot-5-12", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrProcessingService stubQr = QrProcessingStub.stubWithResult(new QrProcessingOutcomeData(qrData, null));

        QrCropResultData result = preprocessQRCropService.apply(WARPED_GRAY_500X400, stubQr);

        assertNotNull(result);
        assertNotNull(result.adjustedQr());
        assertEquals(10 + cropX, result.adjustedQr().bbox().x());
        assertEquals(10 + cropY, result.adjustedQr().bbox().y());
        assertEquals(20, result.adjustedQr().bbox().width());
        assertEquals(20, result.adjustedQr().bbox().height());
    }
}
