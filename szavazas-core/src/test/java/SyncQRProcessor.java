package hu.kdea.szavazas;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.IQRProcessor;
import hu.kdea.szavazas.ballotprocessor.qr.QrResult;
import hu.kdea.szavazas.ballotprocessor.qr.ZXingQRProcessor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SyncQRProcessor implements IQRProcessor {
    @Override
    public void detect(GrayU8 image, Consumer<QrResult> onResult) {
        AtomicReference<QrResult> result = new AtomicReference<>();
        new ZXingQRProcessor().detect(image, result::set);
        onResult.accept(result.get());
    }
}
