package hu.kdea.szavazas.ballotprocessor.debug;

import hu.kdea.szavazas.ballotprocessor.draw.DrawRectangleService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import javax.inject.Inject;

public class XMarkDebugRendererDependenciesData {
    private final ImageSaverWrapper imageSaverWrapper;
    private final DrawTextService drawText;
    private final SetPixelService setPixel;
    private final DrawRectangleService drawRectangle;
    private final RectFillService rectFill;

    @Inject
    public XMarkDebugRendererDependenciesData(
            @DebugImageSaver ImageSaverWrapper imageSaverWrapper,
            DrawTextService drawText,
            SetPixelService setPixel,
            DrawRectangleService drawRectangle,
            RectFillService rectFill) {
        this.imageSaverWrapper = imageSaverWrapper;
        this.drawText = drawText;
        this.setPixel = setPixel;
        this.drawRectangle = drawRectangle;
        this.rectFill = rectFill;
    }

    public ImageSaverWrapper imageSaverWrapper() {
        return imageSaverWrapper;
    }

    public DrawTextService drawText() {
        return drawText;
    }

    public SetPixelService setPixel() {
        return setPixel;
    }

    public DrawRectangleService drawRectangle() {
        return drawRectangle;
    }

    public RectFillService rectFill() {
        return rectFill;
    }
}
