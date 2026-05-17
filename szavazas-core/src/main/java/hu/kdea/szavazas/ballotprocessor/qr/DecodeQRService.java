package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import javax.inject.Inject;

public class DecodeQRService {
    @Inject
    public DecodeQRService() {
    }

    public Result apply(int[] pixels, int width, int height) {
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        MultiFormatReader reader = new MultiFormatReader();
        Result hybrid = decode(source, reader, true);
        return hybrid != null ? hybrid : decode(source, reader, false);
    }

    private Result decode(RGBLuminanceSource source, MultiFormatReader reader, boolean hybrid) {
        try {
            return reader.decode(hybrid
                ? new BinaryBitmap(new HybridBinarizer(source))
                : new BinaryBitmap(new GlobalHistogramBinarizer(source)));
        } catch (NotFoundException e) {
            return null;
        }
    }
}
