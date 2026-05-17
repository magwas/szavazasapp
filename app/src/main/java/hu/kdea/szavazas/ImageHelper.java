package hu.kdea.szavazas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import java.io.File;
import java.io.FileOutputStream;

public final class ImageHelper {
    private ImageHelper() {
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, Context context) {
        int rotation = getDisplayRotationDegrees(context);
        if (rotation == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(-rotation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int getDisplayRotationDegrees(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90 -> 90;
            case Surface.ROTATION_180 -> 180;
            case Surface.ROTATION_270 -> 270;
            default -> 0;
        };
    }

    public static Planar<GrayU8> bitmapToPlanar(Bitmap bitmap) {
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, bitmap.getWidth(), bitmap.getHeight(), 3);
        ConvertBitmap.bitmapToPlanar(bitmap, planar, null, null);
        return planar;
    }

    public static GrayU8 bitmapToGray(Bitmap bitmap) {
        GrayU8 gray = new GrayU8(bitmap.getWidth(), bitmap.getHeight());
        ConvertBitmap.bitmapToGray(bitmap, gray, null);
        return gray;
    }

    public static Bitmap grayToBitmap(GrayU8 gray) {
        Bitmap bitmap = Bitmap.createBitmap(gray.getWidth(), gray.getHeight(), Bitmap.Config.ARGB_8888);
        ConvertBitmap.grayToBitmap(gray, bitmap, null);
        return bitmap;
    }

    public static Bitmap planarToBitmap(Planar<GrayU8> planar) {
        Bitmap bitmap = Bitmap.createBitmap(planar.getWidth(), planar.getHeight(), Bitmap.Config.ARGB_8888);
        ConvertBitmap.planarToBitmap(planar, bitmap, null);
        return bitmap;
    }

    public static void saveDebugImage(Object image, String name, Context context) {
        try {
            File file = new File(context.getCacheDir(), name);
            Bitmap bitmap = toBitmap(image);
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            }
            bitmap.recycle();
            Log.d("ImageHelper", "Saved debug image: " + file.getAbsolutePath());
        } catch (Exception exception) {
            Log.e("ImageHelper", "Failed to save debug image " + name, exception);
        }
    }

    private static Bitmap toBitmap(Object image) {
        if (image instanceof GrayU8 grayU8) {
            return grayToBitmap(grayU8);
        }
        if (image instanceof Planar<?> planarImage) {
            @SuppressWarnings("unchecked")
            Planar<GrayU8> planar = (Planar<GrayU8>) planarImage;
            return planarToBitmap(planar);
        }
        throw new IllegalArgumentException("Unsupported image type");
    }
}
