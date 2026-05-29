package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRendererService;
import java.util.List;
import javax.inject.Inject;

public class XMarkDebugRendererWrapper {
    private final XMarkDebugRendererService renderer;

    @Inject
    public XMarkDebugRendererWrapper(XMarkDebugRendererService renderer) {
        this.renderer = renderer;
    }

    public void render(GrayU8 gridBinary, List<CellDebugData> cells) {
        if (cells.isEmpty()) {
            return;
        }
        renderer.apply(gridBinary, cells);
    }
}
