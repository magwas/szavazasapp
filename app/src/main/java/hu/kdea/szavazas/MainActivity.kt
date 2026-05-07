package hu.kdea.szavazas

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var cameraManager: CameraManager
    private lateinit var qrProcessor: QRProcessor
    private lateinit var ballotProcessor: BallotProcessor
    private var currentNumSupport = 3
    private var currentNumRows = 11
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

        setupProcessors()
        // CameraManager onFrame lambda: simply close the frame (no live QR scanning)
        cameraManager = CameraManager(
            context = this,
            lifecycleOwner = this,
            previewView = previewView,
            onFrame = { imageProxy: ImageProxy -> imageProxy.close() }
        )
        captureButton.setOnClickListener {
            if (!isProcessing) {
                cameraManager.capturePhoto { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        // Scan QR from the captured photo to get numSupport and numRows
                        if (currentNumRows == 11 && currentNumSupport == 3) {
                            qrProcessor.processBitmap(bitmap) {
                                processBallot(bitmap)
                            }
                        } else {
                            processBallot(bitmap)
                        }
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

    private fun setupProcessors() {
        qrProcessor = QRProcessor { raw, numSupport, numCandidates ->
            currentNumSupport = numSupport
            currentNumRows = numCandidates
            runOnUiThread { Toast.makeText(this, "QR: $raw", Toast.LENGTH_SHORT).show() }
        }
        ballotProcessor = BallotProcessor(
            this,
            onResult = { results ->
                runOnUiThread {
                    Toast.makeText(this, "Results: $results", Toast.LENGTH_LONG).show()
                    isProcessing = false
                    cameraManager.resumeAnalysis()
                    qrProcessor.reset()
                }
            },
            onError = { message ->
                runOnUiThread {
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    isProcessing = false
                    cameraManager.resumeAnalysis()
                    qrProcessor.reset()
                }
            }
        )
    }

    private fun processBallot(bitmap: android.graphics.Bitmap) {
        isProcessing = true
        cameraManager.stopAnalysis()
        ballotProcessor.process(bitmap, currentNumSupport, currentNumRows)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }
}