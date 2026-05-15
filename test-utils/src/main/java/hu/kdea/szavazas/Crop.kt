package hu.kdea.szavazas

import boofcv.struct.image.GrayU8

object Crop {
    fun crop(gray: GrayU8, x: Int, y: Int, w: Int, h: Int): GrayU8 {
        val out = GrayU8(w, h)
        for (cy in 0 until h)
            for (cx in 0 until w)
                out.set(cx, cy, gray.get(x + cx, y + cy))
        return out
    }
}