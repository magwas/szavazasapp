package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.EnhanceContrastService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.SharpenService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class PreprocessQRServiceTest extends TestBase implements PreprocessTestData {

    @Test
    @DisplayName("preprocessing steps are executed in order and each step receives the previous output")
    public void applyStepsExecutedInOrder() {
        EnhanceContrastService enhanceContrast = mock(EnhanceContrastService.class);
        SharpenService sharpen = mock(SharpenService.class);
        AdaptiveBinarizeService adaptiveBinarize = mock(AdaptiveBinarizeService.class);
        when(enhanceContrast.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(sharpen.apply(PREPROCESS_OUTPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(adaptiveBinarize.apply(PREPROCESS_OUTPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, adaptiveBinarize, enhanceContrast, sharpen);
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertEquals(PREPROCESS_OUTPUT_2X2, result);
    }

    @Test
    @DisplayName("the final returned image is the output of the last step")
    public void applyReturnsLastStepOutput() {
        EnhanceContrastService enhanceContrast = mock(EnhanceContrastService.class);
        SharpenService sharpen = mock(SharpenService.class);
        AdaptiveBinarizeService adaptiveBinarize = mock(AdaptiveBinarizeService.class);
        when(enhanceContrast.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_INPUT_2X2);
        when(sharpen.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_INPUT_2X2);
        when(adaptiveBinarize.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, adaptiveBinarize, enhanceContrast, sharpen);
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertEquals(PREPROCESS_OUTPUT_2X2, result);
    }

    @Test
    @DisplayName("debug saver is called after each step with the expected staged filename")
    public void applyDebugSaverCalledAfterEachStep() {
        ImageSaverWrapper debugSaver = mock(ImageSaverWrapper.class);
        EnhanceContrastService enhanceContrast = mock(EnhanceContrastService.class);
        SharpenService sharpen = mock(SharpenService.class);
        AdaptiveBinarizeService adaptiveBinarize = mock(AdaptiveBinarizeService.class);
        when(enhanceContrast.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(sharpen.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(adaptiveBinarize.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(debugSaver, adaptiveBinarize, enhanceContrast, sharpen);
        service.apply(PREPROCESS_INPUT_2X2, "testBase");
        verify(debugSaver).apply(eq(PREPROCESS_OUTPUT_2X2), eq("testBase_0_EnhanceContrastService.jpg"));
        verify(debugSaver).apply(eq(PREPROCESS_OUTPUT_2X2), eq("testBase_1_SharpenService.jpg"));
        verify(debugSaver).apply(eq(PREPROCESS_OUTPUT_2X2), eq("testBase_2_AdaptiveBinarizeService.jpg"));
    }

    @Test
    @DisplayName("processing works when debug saver is null")
    public void applyWorksWithNullDebugSaver() {
        EnhanceContrastService enhanceContrast = mock(EnhanceContrastService.class);
        SharpenService sharpen = mock(SharpenService.class);
        AdaptiveBinarizeService adaptiveBinarize = mock(AdaptiveBinarizeService.class);
        when(enhanceContrast.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(sharpen.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(adaptiveBinarize.apply(any(GrayU8.class))).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, adaptiveBinarize, enhanceContrast, sharpen);
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertNotNull(result);
    }
}