package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public final class BitmapFont5x7 {
    private static final int GLYPH_WIDTH = 5;
    private static final int GLYPH_HEIGHT = 7;
    private static final int CHAR_ADVANCE = GLYPH_WIDTH + 1;

    private BitmapFont5x7() {
    }

    public static void drawString(Planar<GrayU8> image, String text, int x, int y, int color, float fontSize) {
        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= 32 && ch <= 126) {
                drawChar(image, ch, cursorX, y, color);
            }
            cursorX += CHAR_ADVANCE;
        }
    }

    private static void drawChar(Planar<GrayU8> image, char ch, int x, int y, int color) {
        int glyphIdx = (ch - 32) * GLYPH_WIDTH;
        for (int col = 0; col < GLYPH_WIDTH; col++) {
            int line = BitmapFont5x7Data.GLYPHS[glyphIdx + col];
            for (int row = 0; row < GLYPH_HEIGHT; row++) {
                if ((line & (1 << row)) != 0) {
                    DrawingUtils.setPixel(image, x + col, y + row, color);
                }
            }
        }
    }
}
