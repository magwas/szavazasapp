package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class QRDetectorStep {
    private final IQRProcessor qrProcessor;

    @Inject
    public QRDetectorStep(IQRProcessor qrProcessor) {
        this.qrProcessor = qrProcessor;
    }

    public QrData detect(GrayU8 scaledGray) throws InterruptedException {
        final QrResult[] detected = new QrResult[1];
        CountDownLatch latch = new CountDownLatch(1);
        qrProcessor.detect(scaledGray, result -> {
            detected[0] = result;
            latch.countDown();
        });
        if (latch.await(5, TimeUnit.SECONDS) && detected[0] != null) {
            return new QrData(
                detected[0].getRaw(),
                detected[0].getNumSupport(),
                detected[0].getNumCandidates(),
                detected[0].getBoundingBox()
            );
        }
        return null;
    }
}
