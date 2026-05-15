package hu.kdea.szavazas.qrpreprocess

import boofcv.alg.filter.binary.BinaryImageOps
import boofcv.struct.image.GrayU8

class MorphologicalClosingStep : PreprocessingStep {
    override val name = "close"
    override fun process(input: GrayU8): GrayU8 {
        var out = BinaryImageOps.dilate8(input, 1, null)
        out = BinaryImageOps.erode8(out, 1, null)
        return out
    }
}