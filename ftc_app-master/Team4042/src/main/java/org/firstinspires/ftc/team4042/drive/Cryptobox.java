package org.firstinspires.ftc.team4042.drive;

/**
 * Created by Hazel on 2/13/2018.
 */

public class Cryptobox {
    private enum GlyphColor { GREY, BROWN, NONE, EITHER }

    private GlyphColor[][] glyphs = new GlyphColor[3][4];

    /**
     * The first index is the column, the second index is the row
     * The rows are built from bottom to top, so index 0 is the
     * lowest position and index 3 the highest position
     */
    private final static GlyphColor[][] snake1 = {
            {GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN},
            {GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY},
            {GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY}};
    private final static GlyphColor[][] snake2 = {
            {GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY},
            {GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN},
            {GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN}};

    private enum Snake {
        ONE(snake1), TWO(snake2);
        private GlyphColor[][] glyphMap;
        private Snake(GlyphColor[][] glyphMap) {
            this.glyphMap = glyphMap;
        }

        public GlyphColor[][] getGlyphMap() {
            return glyphMap;
        }
    }

    public Cryptobox() {
        for (int i = 0; i < glyphs.length; i++) {
            for (int j = 0; j < glyphs[0].length; j++) {
                glyphs[i][j] = GlyphColor.NONE;
            }
        }
    }

    public void placeGlyph(GlyphColor newGlyph) {

    }

    /**
     * Places a glyph in the column indicated, at the lowest empty space
     * @param newGlyph The new glyph color to add
     * @param columnNum The column to place in
     * @return If there was space in the column to place
     */
    private boolean addGlyphToColumn(GlyphColor newGlyph, int columnNum) {
        int emptySpace = getFirstEmpty(columnNum);
        if (emptySpace != -1) {
            glyphs[columnNum][emptySpace] = newGlyph;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Places a glyph in the column indicated, at the lowest empty space,
     * if that space matches the snake target
     * @param newGlyph The new glyph color to add
     * @param columnNum The column to place in
     * @param snakeTarget The target snake to try to match
     * @return If there was space in the column to place and
     * that space matches the indicated target
     */
    private boolean addGlyphToColumn(GlyphColor newGlyph, int columnNum, Snake snakeTarget) {

        int emptySpace = getFirstEmpty(columnNum);
        if (emptySpace != -1) {
            if (snakeTarget.getGlyphMap()[columnNum][emptySpace].equals(glyphs[columnNum][emptySpace])) {
                //If it matches the target, [lace that glyph there
                glyphs[columnNum][emptySpace] = newGlyph;
                return true;
            } else {
                //If it doesn't match the target, then you can't place there so return false
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Simulates placing a glyph in the target location and returns the grey/brown possibilities for it
     * @param newGlyph The new glyph color to add
     * @param columnNum The column to place in
     * @param snakeTarget The target snake to try to match
     * @return An array of two values, where the first is the number of grey possibilities and the second the number of brown possibilities
     */
    private int[] getPossibilities(GlyphColor newGlyph, int columnNum, Snake snakeTarget) {
        int[] empties = new int[3];
        for (int i = 0; i < empties.length; i++) {
            empties[i] = getFirstEmpty(i);
        }

        int[] greyBrown = new int[] {0, 0};

        //If there's no space for the new glyph or it doesn't match the target, then there are no future possibilities if we place here
        if (empties[columnNum] == -1 || !snakeTarget.getGlyphMap()[columnNum][empties[columnNum]].equals(newGlyph)) {
            return greyBrown;
        } else {
            //Assume the glyph gets placed
            empties[columnNum]++;
        }

        for (int i = 0; i < empties.length; i++) {
            //Get the glyph possibility at the empty space
            GlyphColor possibility = snakeTarget.getGlyphMap()[columnNum][empties[i]];
            if (possibility.equals(GlyphColor.GREY)) {
                greyBrown[0]++;
            }
            else if (possibility.equals(GlyphColor.BROWN)) {
                greyBrown[1]++;
            }
        }
        
        return greyBrown;
    }

    /**
     * Gets the first empty glyph in a column
     * @param columnNum The column to look in
     * @return The first index of an empty space, or -1 if none exists
     */
    private int getFirstEmpty(int columnNum) {
        GlyphColor[] column = glyphs[columnNum];
        //Look up the column from the lowest position to the highest to find the first empty space
        for (int i = 0; i < column.length; i++) {
            if (!column[i].equals(GlyphColor.NONE)) {
                return i;
            }
        }
        return -1;
    }
}
