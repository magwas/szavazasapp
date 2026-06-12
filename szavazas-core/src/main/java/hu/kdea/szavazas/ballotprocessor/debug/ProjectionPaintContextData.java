package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public record ProjectionPaintContextData(
    Planar<GrayU8> canvas,
    float[] proj,
    int offset,
    float maxVal
) {}
