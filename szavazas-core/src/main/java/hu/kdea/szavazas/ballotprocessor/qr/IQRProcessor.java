package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import java.util.function.Consumer;

public interface IQRProcessor {
    void detect(GrayU8 image, Consumer<QrResult> onResult);
}
