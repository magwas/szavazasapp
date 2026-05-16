package hu.kdea.szavazas.ballotprocessor.qr.preprocess

import boofcv.alg.filter.binary.ThresholdImageOps
import boofcv.struct.image.GrayU8

class AdaptiveBinarizeStep : PreprocessingStep {
    override val name = "binarize"
    override fun process(input: GrayU8): GrayU8 {
        // Compute Otsu threshold manually
        val histogram = IntArray(256)
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                histogram[input.get(x, y).toInt() and 0xFF]++
            }
        }
        val total = input.width * input.height
        var sumB = 0
        var wB = 0
        var maximum = 0.0
        val sum1 = histogram.indices.fold(0) { acc, i -> acc + i * histogram[i] }
        var threshold = 128
        for (i in 0..255) {
            wB += histogram[i]
            if (wB == 0) continue
            val wF = total - wB
            if (wF == 0) break
            sumB += i * histogram[i]
            val mB = sumB.toDouble() / wB
            val mF = (sum1 - sumB).toDouble() / wF
            val between = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)
            if (between > maximum) {
                maximum = between
                threshold = i
            }
        }
        val out = GrayU8(input.width, input.height)
        // Use down=false, outputValue=255 ⇒ pixels ≥ threshold become 255, else 0
        ThresholdImageOps.threshold(input, out, threshold, false)

        for (y in 0 until out.height) {
            for (x in 0 until out.width) {
                if (out.get(x, y) != 0) out.set(x, y, 255)
            }
        }
        return out
    }
}