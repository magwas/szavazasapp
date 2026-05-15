package hu.kdea.szavazas

import boofcv.io.image.ConvertBufferedImage
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import org.junit.Assert.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class BallotTestExecutor {

    private val testResources = File("src/test/resources/test_images")
    private val debugBaseDir = File("/tmp/ballot_detector_test")

    fun executeTest(imageName: String) {
        // Per-image output directory – creates /tmp/ballot_detector_test/image3/ etc.
        val imageDebugDir = File(debugBaseDir, imageName)
        val debugSaver = FileDebugImageSaver(imageDebugDir)

        val captureFile = File(testResources, "${imageName}_capture.jpg")
        val jsonFile = File(testResources, "$imageName.json")
        assertTrue("Missing test image: $captureFile", captureFile.exists())
        assertTrue("Missing JSON: $jsonFile", jsonFile.exists())

        val jsonString = readJson(jsonFile)
        val numSupport = Regex("\"numSupport\"\\s*:\\s*(\\d+)")
            .find(jsonString)!!.groupValues[1].toInt()
        val numRows = Regex("\"numRows\"\\s*:\\s*(\\d+)")
            .find(jsonString)!!.groupValues[1].toInt()
        val expectedXMarks = mutableListOf<Pair<Int, Int>>()
        Regex("\\[(\\d+)\\s*,\\s*(\\d+)\\]")
            .findAll(jsonString)
            .forEach { match ->
                expectedXMarks.add(
                    match.groupValues[1].toInt() to match.groupValues[2].toInt()
                )
            }

        val planar = loadPlanar(captureFile)
        var actualResult: BallotResult? = null
        var errorMessage: String? = null

        val processor = BallotProcessor(
            debugSaver = debugSaver,
            onResult = { actualResult = it },
            onError = { errorMessage = it },
            qrProcessor = SyncQRProcessor()
        )
        processor.process(planar)

        assertNull("Processing error: $errorMessage", errorMessage)
        assertNotNull("No result", actualResult)
        val result = actualResult!!
        assertEquals("numSupport", numSupport, result.numSupport)
        assertEquals("numRows", numRows, result.numRows)
        assertEquals("X marks size", expectedXMarks.size, result.xCells.size)
        assertEquals("X marks", expectedXMarks.toSet(), result.xCells.toSet())
    }

    private fun loadPlanar(imageFile: File): Planar<GrayU8> {
        val bufferedImage: BufferedImage = ImageIO.read(imageFile)
            ?: error("Failed to load ${imageFile.absolutePath}")
        val planar = Planar(GrayU8::class.java, bufferedImage.width, bufferedImage.height, 3)
        ConvertBufferedImage.convertFrom(bufferedImage, planar, true)
        return planar
    }

    private fun readJson(file: File): String = file.readText()
}