package hu.kdea.szavazas

import boofcv.alg.filter.binary.ThresholdImageOps
import boofcv.alg.filter.blur.BlurImageOps
import boofcv.struct.image.GrayF32
import boofcv.struct.image.GrayU8

object ImageNormalizer {
    fun normalizeAndThreshold(gray: GrayU8, sigma: Double = 101.0): GrayU8 {
        val f32 = convertToF32(gray)
        val blur = applyGaussian(f32, sigma)
        val div = divideAndClip(f32, blur)
        return convertToU8AndThreshold(div)
    }

    private fun convertToF32(gray: GrayU8): GrayF32 {
        val f32 = GrayF32(gray.width, gray.height)
        for (y in 0 until gray.height)
            for (x in 0 until gray.width)
                f32.set(x, y, gray.get(x, y).toFloat())
        return f32
    }

    private fun applyGaussian(f32: GrayF32, sigma: Double): GrayF32 {
        val blur = GrayF32(f32.width, f32.height)
        BlurImageOps.gaussian(f32, blur, sigma, -1, null)
        return blur
    }

    private fun divideAndClip(f32: GrayF32, blur: GrayF32): GrayF32 {
        val out = GrayF32(f32.width, f32.height)
        for (y in 0 until f32.height)
            for (x in 0 until f32.width)
                out.set(x, y, (f32.get(x, y) / blur.get(x, y) * 255f).coerceIn(0f, 255f))
        return out
    }

    private fun convertToU8AndThreshold(f32: GrayF32): GrayU8 {
        val u8 = GrayU8(f32.width, f32.height)
        for (y in 0 until f32.height)
            for (x in 0 until f32.width)
                u8.set(x, y, f32.get(x, y).toInt())
        val binary = GrayU8(f32.width, f32.height)
        ThresholdImageOps.threshold(u8, binary, 128, true)
        return binary
    }
}