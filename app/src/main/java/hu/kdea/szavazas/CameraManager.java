package hu.kdea.szavazas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.inject.Inject;

public class CameraManager {
    private final Context context;
    private final LifecycleOwner lifecycleOwner;
    private final PreviewView previewView;
    private final ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private Camera camera;

    @Inject
    public CameraManager(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.previewView = previewView;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        var providerFuture = ProcessCameraProvider.getInstance(context);
        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = providerFuture.get();
                provider.unbindAll();
                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();
                camera = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                );
            } catch (Exception exception) {
                Log.e("CameraManager", "Failed to start camera", exception);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void setTorch(boolean enabled) {
        if (camera != null) {
            camera.getCameraControl().enableTorch(enabled);
        }
    }

    public void capturePhoto(Consumer<File> onPhotoTaken) {
        File file = new File(context.getCacheDir(), "ballot_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        if (imageCapture == null) {
            return;
        }
        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults output) {
                onPhotoTaken.accept(file);
            }

            @Override
            public void onError(ImageCaptureException exception) {
                Log.e("CameraManager", "Capture failed", exception);
            }
        });
    }

    public void shutdown() {
        cameraExecutor.shutdown();
    }
}
