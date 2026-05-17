package hu.kdea.szavazas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private PreviewView previewView;
    private Button captureButton;
    private CameraManager cameraManager;
    private BallotProcessingApi ballotProcessingApi;
    private boolean isProcessing;
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    cameraManager.start();
                } else {
                    Toast.makeText(this, "Camera required", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.captureButton);
        ballotProcessingApi = DaggerAndroidSzavazasComponent.builder().context(this).build().ballotProcessingApi();
        cameraManager = new CameraManager(this, this, previewView);
        captureButton.setOnClickListener(view -> onCaptureClicked());
        requestCameraIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.shutdown();
    }

    private void toastOnUi(String text) {
        runOnUiThread(() -> {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            isProcessing = false;
        });
    }

    private void onCaptureClicked() {
        if (isProcessing) {
            Toast.makeText(this, "Processing", Toast.LENGTH_SHORT).show();
            return;
        }
        cameraManager.capturePhoto(file -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                processBallot(bitmap);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestCameraIfNeeded() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.start();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void processBallot(Bitmap bitmap) {
        isProcessing = true;
        Bitmap safeBitmap = forceArgb8888(bitmap);
        bitmap.recycle();
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, safeBitmap.getWidth(), safeBitmap.getHeight(), 3);
        ConvertBitmap.bitmapToPlanar(safeBitmap, planar, GrayU8.class, null);
        var outcome = ballotProcessingApi.apply(planar);
        safeBitmap.recycle();
        var result = outcome.result();
        var error = outcome.error();
        if (result != null) {
            toastOnUi("Results: " + result);
            return;
        }
        toastOnUi("Error: " + (error == null ? "Unknown error" : error.message()));
    }

    private Bitmap forceArgb8888(Bitmap bitmap) {
        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(out).drawBitmap(bitmap, 0f, 0f, null);
        return out;
    }
}
