package hu.kdea.szavazas

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.File

class TestDebugImageSaver(private val outputDir: File) : DebugImageSaver {
    init {
        outputDir.mkdirs()
    }

    override fun save(mat: Mat, fileName: String) {
        val file = File(outputDir, fileName)
        // Convert mat to BGR if necessary before saving
        val toSave = when (mat.channels()) {
            1 -> {
                val bgr = Mat()
                org.opencv.imgproc.Imgproc.cvtColor(mat, bgr, org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR)
                bgr
            }
            3 -> mat
            4 -> {
                val bgr = Mat()
                org.opencv.imgproc.Imgproc.cvtColor(mat, bgr, org.opencv.imgproc.Imgproc.COLOR_RGBA2BGR)
                bgr
            }
            else -> mat
        }
        Imgcodecs.imwrite(file.absolutePath, toSave)
        if (toSave != mat) toSave.release()
    }
}