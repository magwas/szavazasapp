package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar

/**
 * 5x7 pixel font renderer (public domain, Adafruit GFX derived).
 * Glyph data lives in [BitmapFont5x7Data].
 */
object BitmapFont5x7 {

    private const val GLYPH_WIDTH = 5
    private const val GLYPH_HEIGHT = 7
    private const val CHAR_ADVANCE = GLYPH_WIDTH + 1

    fun drawString(
        image: Planar<GrayU8>,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        @Suppress("UNUSED_PARAMETER") fontSize: Float = 12f
    ) {
        var cursorX = x
        for (ch in text) {
            if (ch.code in 32..126) drawChar(image, ch, cursorX, y, color)
            cursorX += CHAR_ADVANCE
        }
    }

    private fun drawChar(image: Planar<GrayU8>, ch: Char, x: Int, y: Int, color: Int) {
        val glyphIdx = (ch.code - 32) * GLYPH_WIDTH
        for (col in 0 until GLYPH_WIDTH) {
            val line = BitmapFont5x7Data.GLYPHS[glyphIdx + col]
            for (row in 0 until GLYPH_HEIGHT) {
                if ((line and (1 shl row)) != 0) {
                    DrawingUtils.setPixel(image, x + col, y + row, color)
                }
            }
        }
    }
}