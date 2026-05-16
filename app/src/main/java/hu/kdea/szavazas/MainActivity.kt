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
import hu.kdea.szavazas.ballotprocessor.ImageSaver
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

        // Optional: inject an Android debug saver for debug images.
        // Set to null to disable debug output.
        val debugSaver: ImageSaver? = AndroidImageSaver(this)

        ballotProcessor = BallotProcessor(
            onResult = { result ->
                runOnUiThread {
                    Toast.makeText(this, "Results: $result", Toast.LENGTH_LONG).show()
                    isProcessing = false
                }
            },
            onError = { message ->
                runOnUiThread {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    isProcessing = false
                }
            },
            qrProcessor = ZXingQRProcessor(),
            debugSaver = debugSaver   // null = no debug images
        )

        cameraManager = CameraManager(
            context = this,
            lifecycleOwner = this,
            previewView = previewView
        )

        captureButton.setOnClickListener {
            if (!isProcessing) {
                cameraManager.capturePhoto { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        processBallot(bitmap)
                    } else {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Processing", Toast.LENGTH_SHORT).show()
            }
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            cameraManager.start()
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun processBallot(bitmap: Bitmap) {
        isProcessing = true

        // Force ARGB_8888 via Canvas (guaranteed format)
        val safeBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(safeBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        bitmap.recycle()

        val planar = Planar(GrayU8::class.java, safeBitmap.width, safeBitmap.height, 3)
        ConvertBitmap.bitmapToPlanar(safeBitmap, planar, GrayU8::class.java, null)
        ballotProcessor.process(planar)

        safeBitmap.recycle()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }
}