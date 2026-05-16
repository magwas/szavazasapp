package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

object RowProjectionComputer {
    fun compute(gray: GrayU8): FloatArray {
        return FloatArray(gray.height) { y ->
            var sum = 0
            for (x in 0 until gray.width) sum += gray.get(x, y)
            sum.toFloat()
        }
    }
}