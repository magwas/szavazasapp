package hu.kdea.szavazas

import boofcv.alg.filter.binary.GThresholdImageOps
import boofcv.struct.ConfigLength
import boofcv.struct.image.GrayU8

object ImageNormalizer {
    fun normalizeAndThreshold(gray: GrayU8, sigma: Double = 0.0): GrayU8 {
        // Region width ~1/30 of image size, minimum 10 pixels
        val maxDim = maxOf(gray.width, gray.height).toDouble()
        val regionWidth = maxDim /GridConstants.NORMALIZE_SIZE_DIVIDER

        val binary = GrayU8(gray.width, gray.height)

        GThresholdImageOps.localMean(
            gray, binary,
            ConfigLength.fixed(regionWidth),   // Double
            GridConstants.NORMALIZE_SCALE,                               // scale
            true,                              // down: darker pixels become 1
            null, null, null
        )

        // Force foreground to 255
        for (y in 0 until binary.height) {
            for (x in 0 until binary.width) {
                binary.set(x, y, if (binary.get(x, y) != 0) 255 else 0)
            }
        }

        return binary
    }
}