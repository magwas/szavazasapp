package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class QRDetectorStep {
    private final ZXingQRProcessor qrProcessor;

    @Inject
    public QRDetectorStep(ZXingQRProcessor qrProcessor) {
        this.qrProcessor = qrProcessor;
    }

    public QrData detect(GrayU8 scaledGray) throws InterruptedException {
        final QrResult[] detected = new QrResult[1];
        CountDownLatch latch = new CountDownLatch(1);
        qrProcessor.apply(scaledGray, result -> {
            detected[0] = result;
            latch.countDown();
        });
        if (latch.await(5, TimeUnit.SECONDS) && detected[0] != null) {
            return new QrData(
                detected[0].raw(),
                detected[0].numSupport(),
                detected[0].numCandidates(),
                detected[0].boundingBox()
            );
        }
        return null;
    }
}
