package hu.kdea.szavazas.ballotprocessor.debug;

import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import javax.inject.Inject;

public class ProjectionDebugRendererDependenciesData {
    private final ImageSaverWrapper imageSaverWrapper;
    private final DrawTextService drawText;
    private final DrawLineService drawLine;
    private final RectFillService rectFill;
    private final FillOvalService fillOval;

    @Inject
    public ProjectionDebugRendererDependenciesData(
            @DebugImageSaver ImageSaverWrapper imageSaverWrapper,
            DrawTextService drawText,
            DrawLineService drawLine,
            RectFillService rectFill,
            FillOvalService fillOval) {
        this.imageSaverWrapper = imageSaverWrapper;
        this.drawText = drawText;
        this.drawLine = drawLine;
        this.rectFill = rectFill;
        this.fillOval = fillOval;
    }

    public ImageSaverWrapper imageSaverWrapper() {
        return imageSaverWrapper;
    }

    public DrawTextService drawText() {
        return drawText;
    }

    public DrawLineService drawLine() {
        return drawLine;
    }

    public RectFillService rectFill() {
        return rectFill;
    }

    public FillOvalService fillOval() {
        return fillOval;
    }
}
