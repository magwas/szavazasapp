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
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

public class BallotTestExecutor {
    private final File testResources = new File("src/test/resources/test_images");
    private final File debugBaseDir = new File("/tmp/ballot_debug");

    public void executeTest(String imageName) {
        File imageDebugDir = new File(debugBaseDir, imageName);
        imageDebugDir.mkdirs();
        File captureFile = new File(testResources, imageName + "_capture.jpg");
        File jsonFile = new File(testResources, imageName + ".json");
        Assert.assertTrue("Missing test image: " + captureFile, captureFile.exists());
        Assert.assertTrue("Missing JSON: " + jsonFile, jsonFile.exists());
        JSONObject jsonObject = new JSONObject(readJson(jsonFile));
        JSONObject vote = jsonObject.getJSONObject("vote");
        JSONArray ballots = jsonObject.getJSONArray("ballots");
        Assert.assertEquals("single expected ballot", 1, ballots.length());
        JSONObject ballot = ballots.getJSONObject(0);
        int numSupport = vote.getInt("supportColumnCount");
        int numRows = ballot.getInt("numRows");
        Set<CellPositionData> expectedXMarks = extractXMarks(ballot.getJSONArray("xCells"));
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

    private Set<CellPositionData> extractXMarks(JSONArray xCells) {
        Set<CellPositionData> result = new HashSet<>();
        for (int index = 0; index < xCells.length(); index++) {
            JSONObject xCell = xCells.getJSONObject(index);
            result.add(new CellPositionData(xCell.getInt("row"), xCell.getInt("col")));
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
