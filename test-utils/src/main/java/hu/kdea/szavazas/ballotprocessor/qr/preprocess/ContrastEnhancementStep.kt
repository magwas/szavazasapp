package hu.kdea.szavazas.ballotprocessor.qr.preprocess

import boofcv.struct.image.GrayU8

class ContrastEnhancementStep : PreprocessingStep {
    override val name = "contrast"
    override fun process(input: GrayU8): GrayU8 {
        var min = 255
        var max = 0
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val v = input.get(x, y).toInt()
                if (v < min) min = v
                if (v > max) max = v
            }
        }
        val out = GrayU8(input.width, input.height)
        if (max <= min) return input.clone()  // avoid division by zero
        val scale = 255.0 / (max - min)
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val v = input.get(x, y).toInt()
                out.set(x, y, ((v - min) * scale).toInt().coerceIn(0, 255))
            }
        }
        return out
    }
}