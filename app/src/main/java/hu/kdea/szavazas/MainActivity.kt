package hu.kdea.szavazas

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import boofcv.android.ConvertBitmap
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.BallotProcessor
import hu.kdea.szavazas.ballotprocessor.ZXingQRProcessor

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var cameraManager: CameraManager
    private lateinit var ballotProcessor: BallotProcessor
    private var isProcessing = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraManager.start()
        else Toast.makeText(this, "Camera required", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)

        ballotProcessor = buildBallotProcessor()
        cameraManager = CameraManager(this, this, previewView)
        captureButton.setOnClickListener { onCaptureClicked() }
        requestCameraIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }

    private fun buildBallotProcessor(): BallotProcessor = BallotProcessor(
        onResult = { result -> toastOnUi("Results: $result") },
        onError = { message -> toastOnUi("Error: $message") },
        qrProcessor = ZXingQRProcessor(),
        debugSaver = AndroidImageSaver(this)
    )

    private fun toastOnUi(text: String) = runOnUiThread {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        isProcessing = false
    }

    private fun onCaptureClicked() {
        if (isProcessing) {
            Toast.makeText(this, "Processing", Toast.LENGTH_SHORT).show()
            return
        }
        cameraManager.capturePhoto { file ->
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            if (bitmap != null) processBallot(bitmap)
            else Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraIfNeeded() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            cameraManager.start()
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun processBallot(bitmap: Bitmap) {
        isProcessing = true
        val safeBitmap = forceArgb8888(bitmap)
        bitmap.recycle()
        val planar = Planar(GrayU8::class.java, safeBitmap.width, safeBitmap.height, 3)
        ConvertBitmap.bitmapToPlanar(safeBitmap, planar, GrayU8::class.java, null)
        ballotProcessor.process(planar)
        safeBitmap.recycle()
    }

    private fun forceArgb8888(bitmap: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Canvas(out).drawBitmap(bitmap, 0f, 0f, null)
        return out
    }
}