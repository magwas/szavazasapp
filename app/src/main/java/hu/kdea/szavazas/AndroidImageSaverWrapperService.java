package hu.kdea.szavazas;

import android.content.Context;
import android.graphics.Bitmap;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AndroidImageSaverWrapperService implements ImageSaverWrapper {
    private final Context context;

    public AndroidImageSaverWrapperService(Context context) {
        this.context = context;
    }

    @Override
    public void apply(Object image, String fileName) {
        Bitmap bitmap = toBitmap(image);
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            dir = context.getFilesDir();
        }
        dir.mkdirs();
        File file = new File(dir, fileName);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save " + file.getAbsolutePath(), exception);
        } finally {
            bitmap.recycle();
        }
    }

    private Bitmap toBitmap(Object image) {
        if (image instanceof GrayU8 grayU8) {
            Bitmap bitmap = Bitmap.createBitmap(grayU8.width, grayU8.height, Bitmap.Config.ARGB_8888);
            ConvertBitmap.grayToBitmap(grayU8, bitmap, null);
            return bitmap;
        }
        if (image instanceof Planar<?> planarImage) {
            @SuppressWarnings("unchecked")
            Planar<GrayU8> planar = (Planar<GrayU8>) planarImage;
            Bitmap bitmap = Bitmap.createBitmap(planar.width, planar.height, Bitmap.Config.ARGB_8888);
            ConvertBitmap.planarToBitmap(planar, bitmap, null);
            return bitmap;
        }
        throw new IllegalArgumentException("Unsupported image type: " + image.getClass());
    }
}
