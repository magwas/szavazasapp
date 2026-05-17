package hu.kdea.szavazas;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.junit.Assert;

public class BallotTestExecutor {
    private static final Pattern NUM_SUPPORT_PATTERN = Pattern.compile("\\\"numSupport\\\"\\s*:\\s*(\\d+)");
    private static final Pattern NUM_ROWS_PATTERN = Pattern.compile("\\\"numRows\\\"\\s*:\\s*(\\d+)");
    private static final Pattern X_MARK_PATTERN = Pattern.compile("\\[(\\d+)\\s*,\\s*(\\d+)\\]");
    private final File testResources = new File("src/test/resources/test_images");
    private final File debugBaseDir = new File("/tmp/ballot_debug");

    public void executeTest(String imageName) {
        File imageDebugDir = new File(debugBaseDir, imageName);
        imageDebugDir.mkdirs();
        File captureFile = new File(testResources, imageName + "_capture.jpg");
        File jsonFile = new File(testResources, imageName + ".json");
        Assert.assertTrue("Missing test image: " + captureFile, captureFile.exists());
        Assert.assertTrue("Missing JSON: " + jsonFile, jsonFile.exists());
        String jsonString = readJson(jsonFile);
        int numSupport = extractInt(NUM_SUPPORT_PATTERN, jsonString);
        int numRows = extractInt(NUM_ROWS_PATTERN, jsonString);
        Set<CellPositionData> expectedXMarks = extractXMarks(jsonString);
        Planar<GrayU8> planar = loadPlanar(captureFile);
        BallotProcessingApi api = ballotProcessingApi(imageDebugDir);
        var outcome = api.apply(planar);
        var actualResult = outcome.result();
        var error = outcome.error();
        String errorMessage = error == null ? null : error.message();
        Assert.assertNull("Processing error: " + errorMessage, errorMessage);
        Assert.assertNotNull("No result", actualResult);
        Assert.assertEquals("numSupport", numSupport, actualResult.numSupport());
        Assert.assertEquals("numRows", numRows, actualResult.numRows());
        Assert.assertEquals("X marks size", expectedXMarks.size(), actualResult.xCells().size());
        Assert.assertEquals("X marks", expectedXMarks, new HashSet<>(actualResult.xCells()));
    }

    private int extractInt(Pattern pattern, String jsonString) {
        Matcher matcher = pattern.matcher(jsonString);
        if (!matcher.find()) {
            throw new IllegalStateException("Missing numeric value in json");
        }
        return Integer.parseInt(matcher.group(1));
    }

    private Set<CellPositionData> extractXMarks(String jsonString) {
        Matcher matcher = X_MARK_PATTERN.matcher(jsonString);
        Set<CellPositionData> result = new HashSet<>();
        while (matcher.find()) {
            result.add(new CellPositionData(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));
        }
        return result;
    }

    private BallotProcessingApi ballotProcessingApi(File imageDebugDir) {
        JvmTestSzavazasComponent component = DaggerJvmTestSzavazasComponent.builder().outputDir(imageDebugDir).build();
        return component.ballotProcessingApi();
    }

    private Planar<GrayU8> loadPlanar(File imageFile) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load " + imageFile.getAbsolutePath(), exception);
        }
        if (bufferedImage == null) {
            throw new IllegalStateException("Failed to load " + imageFile.getAbsolutePath());
        }
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, bufferedImage.getWidth(), bufferedImage.getHeight(), 3);
        ConvertBufferedImage.convertFrom(bufferedImage, planar, true);
        return planar;
    }

    private String readJson(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read " + file.getAbsolutePath(), exception);
        }
    }
}
