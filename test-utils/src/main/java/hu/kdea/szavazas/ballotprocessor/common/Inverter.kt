package hu.kdea.szavazas.ballotprocessor.common

import boofcv.struct.image.GrayU8

object Inverter {
    fun invert(gray: GrayU8): GrayU8 {
        val out = GrayU8(gray.width, gray.height)
        for (y in 0 until gray.height)
            for (x in 0 until gray.width)
                out.set(x, y, 255 - gray.get(x, y))
        return out
    }
}