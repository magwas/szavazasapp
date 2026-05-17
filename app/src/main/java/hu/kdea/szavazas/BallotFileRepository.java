package hu.kdea.szavazas;

import android.content.Context;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.inject.Inject;

public class BallotFileRepository implements BallotResultFileRepository {
    private final Context context;

    @Inject
    public BallotFileRepository(Context context) {
        this.context = context;
    }

    @Override
    public String apply(String fileName) {
        try {
            File file = new File(directory(), fileName);
            return file.exists() ? new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8) : "";
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read ballot result", exception);
        }
    }

    @Override
    public void save(String fileName, String content) {
        try {
            File directory = directory();
            directory.mkdirs();
            Files.write(new File(directory, fileName).toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save ballot result", exception);
        }
    }

    private File directory() {
        File directory = context.getExternalFilesDir(null);
        return directory == null ? context.getFilesDir() : directory;
    }
}
