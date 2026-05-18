package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessingStep;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class PreprocessQRServiceTest extends TestBase implements PreprocessTestData {

    private static class NamedStep implements PreprocessingStep {
        private final GrayU8 output;

        NamedStep(GrayU8 output) {
            this.output = output;
        }

        @Override
        public GrayU8 apply(GrayU8 input) {
            return output;
        }
    }

    @Test
    @DisplayName("preprocessing steps are executed in order and each step receives the previous output")
    public void applyStepsExecutedInOrder() {
        PreprocessingStep step1 = mock(PreprocessingStep.class);
        PreprocessingStep step2 = mock(PreprocessingStep.class);
        when(step1.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        when(step2.apply(PREPROCESS_OUTPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, Arrays.asList(step1, step2));
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertEquals(PREPROCESS_OUTPUT_2X2, result);
    }

    @Test
    @DisplayName("the final returned image is the output of the last step")
    public void applyReturnsLastStepOutput() {
        PreprocessingStep step = mock(PreprocessingStep.class);
        when(step.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, List.of(step));
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertEquals(PREPROCESS_OUTPUT_2X2, result);
    }

    @Test
    @DisplayName("debug saver is called after each step with the expected staged filename")
    public void applyDebugSaverCalledAfterEachStep() {
        ImageSaver debugSaver = mock(ImageSaver.class);
        PreprocessingStep step = new NamedStep(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(debugSaver, List.of(step));
        service.apply(PREPROCESS_INPUT_2X2, "testBase");
        verify(debugSaver).apply(eq(PREPROCESS_OUTPUT_2X2), eq("testBase_0_NamedStep.jpg"));
    }

    @Test
    @DisplayName("processing works when debug saver is null")
    public void applyWorksWithNullDebugSaver() {
        PreprocessingStep step = mock(PreprocessingStep.class);
        when(step.apply(PREPROCESS_INPUT_2X2)).thenReturn(PREPROCESS_OUTPUT_2X2);
        PreprocessQRService service = new PreprocessQRService(null, List.of(step));
        GrayU8 result = service.apply(PREPROCESS_INPUT_2X2, "test");
        assertNotNull(result);
    }
}
