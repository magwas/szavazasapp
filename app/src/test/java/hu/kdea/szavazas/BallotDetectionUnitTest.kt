package hu.kdea.szavazas

import nu.pattern.OpenCV
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import org.junit.Assert.*

@RunWith(Parameterized::class)
class BallotDetectionUnitTest(private val imageName: String) {

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadOpenCV() {
            OpenCV.loadShared()
        }

        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun data(): Collection<Array<Any>> {
            // List all test image base names (without extension)
            return listOf(
                arrayOf("image1"),
                arrayOf("image2")
            )
        }
    }

    @Test
    fun testImage() {
        val imagePath = "test_images/$imageName.jpg"
        val jsonPath  = "test_images/$imageName.json"

        // Load binary projection image from classpath
        val imageUrl = javaClass.classLoader!!.getResource(imagePath)
            ?: throw NullPointerException("Test image not found: $imagePath")
        val binaryMat = Imgcodecs.imread(imageUrl.path, Imgcodecs.IMREAD_GRAYSCALE)
        assertFalse("Could not load $imagePath", binaryMat.empty())

        // Parse JSON metadata
        val jsonStream = javaClass.classLoader!!.getResourceAsStream(jsonPath)
            ?: throw NullPointerException("Test JSON not found: $jsonPath")
        val jsonString = jsonStream.bufferedReader().use { it.readText() }
        jsonStream.close()
        val (numSupport, numRows, xMarksList) = parseSimpleJson(jsonString)
        val expectedMarks = xMarksList.toSet()

        // Prepare debug output directory for this image
        val testDir = File(System.getProperty("java.io.tmpdir"), "ballot_test_output/$imageName")
        testDir.deleteRecursively()
        testDir.mkdirs()
        val debugSaver = TestDebugImageSaver(testDir)

        // Run grid detection
        val orchestrator = GridDetectionOrchestrator(debugSaver)
        val checkboxes = orchestrator.detect(
            binaryClosed = binaryMat,
            searchRect = Rect(0, 0, binaryMat.width(), binaryMat.height()),
            expectedCols = numSupport + 1,
            expectedRows = numRows,
            skipBoundaries = true,
            emptySecondColumn = true
        )

        assertFalse("Grid detection failed for $imageName", checkboxes.isEmpty())
        assertEquals("Grid cell count mismatch for $imageName",
            numRows * (numSupport + 1), checkboxes.size)

        // Run X detection
        val xDetector = XDetector()
        val skeletonDebug = Mat()
        Imgproc.cvtColor(binaryMat, skeletonDebug, Imgproc.COLOR_GRAY2BGR)
        val branchDebug = skeletonDebug.clone()
        val erodedDebug = skeletonDebug.clone()

        val results = checkboxes.map { box ->
            xDetector.detect(binaryMat, box, skeletonDebug, branchDebug, erodedDebug)
        }
        debugSaver.save(erodedDebug, "debug_eroded.jpg")
        debugSaver.save(skeletonDebug, "debug_skeleton.jpg")
        debugSaver.save(branchDebug, "debug_branch_points.jpg")

        // Verify expected X marks
        for ((row, col) in expectedMarks) {
            val idx = row * (numSupport + 1) + col
            assertTrue("Missing X mark at [$row, $col] in $imageName", results[idx])
        }

        // Clean up
        binaryMat.release()
        skeletonDebug.release()
        branchDebug.release()
        erodedDebug.release()
    }

    private fun parseSimpleJson(json: String): Triple<Int, Int, List<Pair<Int, Int>>> {
        val numSupport = Regex("\"numSupport\"\\s*:\\s*(\\d+)").find(json)!!.groupValues[1].toInt()
        val numRows = Regex("\"numRows\"\\s*:\\s*(\\d+)").find(json)!!.groupValues[1].toInt()
        val xMarks = mutableListOf<Pair<Int, Int>>()
        val xMarksRegex = Regex("\\[(\\d+)\\s*,\\s*(\\d+)\\]")
        xMarksRegex.findAll(json).forEach { match ->
            val row = match.groupValues[1].toInt()
            val col = match.groupValues[2].toInt()
            xMarks.add(row to col)
        }
        return Triple(numSupport, numRows, xMarks)
    }
}