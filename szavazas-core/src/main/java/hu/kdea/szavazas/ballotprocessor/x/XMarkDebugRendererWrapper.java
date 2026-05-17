package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRenderer;
import java.util.List;
import javax.inject.Inject;

public class XMarkDebugRendererWrapper {
    private final ImageSaver imageSaver;

    @Inject
    public XMarkDebugRendererWrapper(@DebugImageSaver ImageSaver imageSaver) {
        this.imageSaver = imageSaver;
    }

    public void render(GrayU8 gridBinary, List<CellDebugData> cells) {
        if (imageSaver == null || cells.isEmpty()) {
            return;
        }
        new XMarkDebugRenderer(imageSaver).render(gridBinary, cells);
    }
}
