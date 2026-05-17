package hu.kdea.szavazas;

import org.junit.Test;

public class FullPipelineTest {
    private final BallotTestExecutor executor = new BallotTestExecutor();

    @Test
    public void testImage3() {
        executor.executeTest("image3");
    }

    @Test
    public void testImage4() {
        executor.executeTest("image4");
    }

    @Test
    public void testImage5() {
        executor.executeTest("image5");
    }

    @Test
    public void testImage6() {
        executor.executeTest("image6");
    }

    @Test
    public void testImage7() {
        executor.executeTest("image7");
    }

    @Test
    public void testImage8() {
        executor.executeTest("image8");
    }

    @Test
    public void testImage9() {
        executor.executeTest("image9");
    }

    @Test
    public void testImage10() {
        executor.executeTest("image10");
    }

    @Test
    public void testImage11() {
        executor.executeTest("image11");
    }
    /* normalization is not good enough for this
    @Test
    public void testImage12() {
        executor.executeTest("image12");
    }*/
}
