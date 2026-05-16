package hu.kdea.szavazas

import org.junit.Test

class FullPipelineTest {

    private val executor = BallotTestExecutor()

    @Test fun testImage3() = executor.executeTest("image3")
    @Test fun testImage4() = executor.executeTest("image4")
    @Test fun testImage5() = executor.executeTest("image5")
    @Test fun testImage6() = executor.executeTest("image6")
    @Test fun testImage7() = executor.executeTest("image7")
    @Test fun testImage8() = executor.executeTest("image8")
    @Test fun testImage9() = executor.executeTest("image9")
    @Test fun testImage10() = executor.executeTest("image10")
    @Test fun testImage11() = executor.executeTest("image11")
}