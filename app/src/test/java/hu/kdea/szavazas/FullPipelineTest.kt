package hu.kdea.szavazas

import android.graphics.BitmapFactory
import boofcv.struct.image.GrayU8
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class FullPipelineTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            println("BoofCV ready")
        }

        fun loadGrayImage(resourcePath: String): GrayU8 {
            val stream = FullPipelineTest::class.java.classLoader!!
                .getResourceAsStream(resourcePath)
                ?: throw NullPointerException("Resource not found: $resourcePath")

            val bytes = stream.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: error("Failed to decode bitmap")

            val gray = GrayU8(bitmap.width, bitmap.height)
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            for (y in 0 until bitmap.height) {
                for (x in 0 until bitmap.width) {
                    val rgb = pixels[y * bitmap.width + x]
                    val r = (rgb shr 16) and 0xFF
                    val g = (rgb shr 8) and 0xFF
                    val b = rgb and 0xFF
                    val luminance = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                    gray.set(x, y, luminance)
                }
            }
            return gray
        }

        private fun testSingleImage(imageName: String) {
            val binary = loadGrayImage("test_images/${imageName}.jpg")
            assertTrue("Failed to load ${imageName}.jpg", binary.width > 0)

            val jsonStream = FullPipelineTest::class.java.classLoader!!
                .getResourceAsStream("test_images/$imageName.json")
                ?: throw NullPointerException("Missing test JSON: $imageName.json")

            val jsonString = jsonStream.bufferedReader().use { it.readText() }
            jsonStream.close()

            val numSupport = Regex("\"numSupport\"\\s*:\\s*(\\d+)")
                .find(jsonString)!!.groupValues[1].toInt()

            val numRows = Regex("\"numRows\"\\s*:\\s*(\\d+)")
                .find(jsonString)!!.groupValues[1].toInt()

            val xMarks = mutableListOf<Pair<Int, Int>>()
            Regex("\\[(\\d+)\\s*,\\s*(\\d+)\\]")
                .findAll(jsonString)
                .forEach { match ->
                    xMarks.add(match.groupValues[1].toInt() to match.groupValues[2].toInt())
                }

            val tempDir = File(System.getProperty("java.io.tmpdir"), "ballot_test_${imageName}")
            tempDir.deleteRecursively()
            tempDir.mkdirs()

            val debugSaver = object : DebugImageSaver {
                override fun save(image: Any, fileName: String) { /* no-op for tests */ }
            }

            val orchestrator = GridDetectionOrchestrator(debugSaver)
            val checkboxes = orchestrator.detect(
                binaryClosed = binary,
                searchRect = Rect(0, 0, binary.width, binary.height),
                expectedCols = numSupport + 1,
                expectedRows = numRows,
                skipBoundaries = true,
                emptySecondColumn = true
            )

            assertFalse("Grid detection failed for $imageName", checkboxes.isEmpty())
            assertEquals(
                "Grid cell count mismatch for $imageName",
                numRows * (numSupport + 1),
                checkboxes.size
            )

            val xDetector = XDetector()
            val results = checkboxes.map { box -> xDetector.detect(binary, box) }

            for ((row, col) in xMarks) {
                val idx = row * (numSupport + 1) + col
                assertTrue("Missing X mark at [$row, $col] in $imageName", results[idx])
            }

            for (row in 0 until numRows) {
                for (col in 0 until (numSupport + 1)) {
                    val idx = row * (numSupport + 1) + col
                    if (row to col !in xMarks) {
                        assertFalse("Unexpected X mark at [$row, $col] in $imageName", results[idx])
                    }
                }
            }
        }
    }

    @Test
    fun testImage3() {
        testSingleImage("image3")
    }

    @Test
    fun testImage4() {
        testSingleImage("image4")
    }

    @Test
    fun testImage5() {
        testSingleImage("image5")
    }

    @Test
    fun testImage6() {
        testSingleImage("image6")
    }
}