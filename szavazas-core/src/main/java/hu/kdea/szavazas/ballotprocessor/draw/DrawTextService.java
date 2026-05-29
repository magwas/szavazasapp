package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import javax.inject.Inject;

public class DrawTextService implements DrawConstants {
    private final SetPixelService pixelSet;

    @Inject
    public DrawTextService(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public void apply(Planar<GrayU8> image, String text, int x, int y, int color, float fontSize) {
        int cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character >= FIRST_PRINTABLE_CHARACTER && character <= LAST_PRINTABLE_CHARACTER) {
                drawCharacter(image, character, cursorX, y, color);
            }
            cursorX += CHAR_ADVANCE;
        }
    }

    private void drawCharacter(Planar<GrayU8> image, char character, int x, int y, int color) {
        int glyphIndex = (character - FIRST_PRINTABLE_CHARACTER) * GLYPH_WIDTH;
        for (int column = 0; column < GLYPH_WIDTH; column++) {
            int line = GLYPHS[glyphIndex + column];
            for (int row = 0; row < GLYPH_HEIGHT; row++) {
                if ((line & (1 << row)) != 0) {
                    pixelSet.apply(image, x + column, y + row, color);
                }
            }
        }
    }
}
