package hu.kdea.szavazas.ballotprocessor.qrpreprocess

import boofcv.alg.filter.blur.BlurImageOps
import boofcv.struct.image.GrayU8

class SharpeningStep : PreprocessingStep {
    override val name = "sharpen"
    override fun process(input: GrayU8): GrayU8 {
        val blur = GrayU8(input.width, input.height)
        BlurImageOps.gaussian(input, blur, 1.0, -1, null)  // small sigma
        val out = GrayU8(input.width, input.height)
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val v = input.get(x, y).toInt() + (input.get(x, y) - blur.get(x, y))
                out.set(x, y, v.coerceIn(0, 255))
            }
        }
        return out
    }
}