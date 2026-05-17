package hu.kdea.szavazas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.Logger;
import hu.kdea.szavazas.review.ReviewCellData;
import hu.kdea.szavazas.review.ReviewGridData;

public class MainActivity extends AppCompatActivity {
    private LinearLayout startScreen;
    private LinearLayout cameraScreen;
    private View reviewScreen;
    private PreviewView previewView;
    private Button readBallotsButton;
    private Button captureButton;
    private Button finishButton;
    private Button correctButton;
    private Button failedButton;
    private TextView reviewTitle;
    private GridLayout reviewGrid;
    private CameraManager cameraManager;
    private BallotProcessingApi ballotProcessingApi;
    private BallotResultData pendingResult;
    private boolean isProcessing;
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    cameraManager.start();
                } else {
                    Toast.makeText(this, R.string.camera_required, Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.setDelegate((tag, msg) -> Log.d(tag, msg));
        bindViews();
        ballotProcessingApi = DaggerAndroidSzavazasComponent.builder().context(this).build().ballotProcessingApi();
        cameraManager = new CameraManager(this, this, previewView);
        readBallotsButton.setOnClickListener(view -> showCameraScreen());
        captureButton.setOnClickListener(view -> onCaptureClicked());
        finishButton.setOnClickListener(view -> showStartScreen());
        failedButton.setOnClickListener(view -> showCameraScreen());
        correctButton.setOnClickListener(view -> savePendingResult());
        showStartScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.shutdown();
    }

    private void bindViews() {
        startScreen = findViewById(R.id.startScreen);
        cameraScreen = findViewById(R.id.cameraScreen);
        reviewScreen = findViewById(R.id.reviewScreen);
        previewView = findViewById(R.id.previewView);
        readBallotsButton = findViewById(R.id.readBallotsButton);
        captureButton = findViewById(R.id.captureButton);
        finishButton = findViewById(R.id.finishButton);
        correctButton = findViewById(R.id.correctButton);
        failedButton = findViewById(R.id.failedButton);
        reviewTitle = findViewById(R.id.reviewTitle);
        reviewGrid = findViewById(R.id.reviewGrid);
    }

    private void showStartScreen() {
        pendingResult = null;
        isProcessing = false;
        startScreen.setVisibility(View.VISIBLE);
        cameraScreen.setVisibility(View.GONE);
        reviewScreen.setVisibility(View.GONE);
    }

    private void showCameraScreen() {
        pendingResult = null;
        isProcessing = false;
        startScreen.setVisibility(View.GONE);
        cameraScreen.setVisibility(View.VISIBLE);
        reviewScreen.setVisibility(View.GONE);
        requestCameraIfNeeded();
    }

    private void showReviewScreen(BallotResultData ballotResultData) {
        ReviewGridData reviewGridData = ballotProcessingApi.review(ballotResultData);
        pendingResult = ballotResultData;
        isProcessing = false;
        startScreen.setVisibility(View.GONE);
        cameraScreen.setVisibility(View.GONE);
        reviewScreen.setVisibility(View.VISIBLE);
        reviewTitle.setText(getString(R.string.review_title, reviewGridData.voteName()));
        renderReviewGrid(reviewGridData);
    }

    private void renderReviewGrid(ReviewGridData reviewGridData) {
        reviewGrid.removeAllViews();
        reviewGrid.setColumnCount(reviewGridData.columnCount());
        reviewGrid.setRowCount(reviewGridData.rowCount());
        for (ReviewCellData reviewCellData : reviewGridData.cells()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            checkBox.setEnabled(false);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setChecked(reviewCellData.checked());
            if (reviewCellData.hidden()) {
                checkBox.setVisibility(View.INVISIBLE);
            }
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);
            params.rowSpec = GridLayout.spec(reviewCellData.row());
            params.columnSpec = GridLayout.spec(reviewCellData.col());
            checkBox.setLayoutParams(params);
            reviewGrid.addView(checkBox);
        }
    }

    private void savePendingResult() {
        if (pendingResult == null) {
            showCameraScreen();
            return;
        }
        try {
            ballotProcessingApi.save(pendingResult);
            Toast.makeText(this, R.string.saved_ballot, Toast.LENGTH_LONG).show();
            showCameraScreen();
        } catch (IllegalStateException exception) {
            Toast.makeText(this, getString(R.string.save_failed, exception.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    private void onCaptureClicked() {
        if (isProcessing) {
            Toast.makeText(this, R.string.processing, Toast.LENGTH_SHORT).show();
            return;
        }
        isProcessing = true;
        cameraManager.capturePhoto(file -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                processBallot(bitmap);
            } else {
                runOnUiThread(() -> {
                    isProcessing = false;
                    Toast.makeText(this, R.string.failed_to_load_image, Toast.LENGTH_SHORT).show();
                });
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
        Bitmap safeBitmap = forceArgb8888(bitmap);
        bitmap.recycle();
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, safeBitmap.getWidth(), safeBitmap.getHeight(), 3);
        ConvertBitmap.bitmapToPlanar(safeBitmap, planar, GrayU8.class, null);
        var outcome = ballotProcessingApi.apply(planar);
        safeBitmap.recycle();
        var result = outcome.result();
        var error = outcome.error();
        runOnUiThread(() -> {
            if (result != null) {
                showReviewScreen(result);
                return;
            }
            isProcessing = false;
            Toast.makeText(this, getString(R.string.error_message, error == null ? getString(R.string.unknown_error) : error.message()), Toast.LENGTH_LONG).show();
        });
    }

    private Bitmap forceArgb8888(Bitmap bitmap) {
        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(out).drawBitmap(bitmap, 0f, 0f, null);
        return out;
    }
}
