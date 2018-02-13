package org.firstinspires.ftc.team4042.drive;

import java.util.ArrayList;

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

    private GlyphPlacementSystem glyphPlacementSystem;

    public Cryptobox(GlyphPlacementSystem glyphPlacementSystem) {
        for (int i = 0; i < glyphs.length; i++) {
            for (int j = 0; j < glyphs[0].length; j++) {
                glyphs[i][j] = GlyphColor.NONE;
            }
        }

        this.glyphPlacementSystem = glyphPlacementSystem;
    }

    public void placeGlyph(GlyphColor newGlyph) {
        GlyphColor[][] predictions = new GlyphColor[3][3];
        for (int i = 0; i < glyphs.length; i++) {
            //Contains the predictions for if we put the glyph in that column
            GlyphColor[] prediction = getPrediction(newGlyph, i, Snake.ONE);
            predictions[i] = prediction;
        }

        int column = getBestColumnIndex(predictions);

        //Place at the height of one over the lowest
        int[] height = new int[glyphs.length];
        for (int i = 0; i < height.length; i++) {
            height[i] = getFirstEmpty(i);
        }

        //The one with the most glyphs
        int maximumHeight = getIndicesOfExtreme(height, true).get(0);

        int row = maximumHeight == 3 ? 3 : maximumHeight + 1;

        //Place the glyph in the simulation
        addGlyphToColumn(newGlyph, column, Snake.ONE);

        glyphPlacementSystem.uiTarget(row, column);
        glyphPlacementSystem.drive.glyphLocate();
        
        while (!glyphPlacementSystem.drive.uTrack()) { }
    }

    private int getBestColumnIndex(GlyphColor[][] predictions) {
        int[][] greyBrowns = new int[3][2];
        int[] sums = new int[3];

        for (int i = 0; i < greyBrowns.length; i++) {
            greyBrowns[i] = convertPredictionToSums(predictions[i]);
            sums[i] = greyBrowns[i][0] + greyBrowns[i][1];

            //Determine which prediction is the most desirable based on which one
            // has the highest sum with a tiebreaker of the variance
            ArrayList<Integer> maximums = getIndicesOfExtreme(sums, true);
            if (maximums.size() == 1) {
                return maximums.get(0);
            } else {
                int[] variances = new int[3];
                //Get the variances of the differences between the two number of greys and browns
                for (int j = 0; j < greyBrowns.length; j++) {
                    variances[j] = Math.abs(greyBrowns[j][0] - greyBrowns[j][1]);
                }

                //Get the one(s) with the smallest variance
                ArrayList<Integer> minimums = getIndicesOfExtreme(variances, false);

                //The one with the least variance is returned
                if (minimums.size() == 1) {
                    return minimums.get(0);
                } else {
                    //Get the heights of the columns
                    int[] height = new int[minimums.size()];
                    for (int k = 0; k < minimums.size(); k++) {
                        height[k] = getFirstEmpty(k);
                    }

                    //The one with the fewest glyphs is returned
                    ArrayList<Integer> minimumHeights = getIndicesOfExtreme(height, false);
                    if (minimumHeights.size() == 1) {
                        return minimumHeights.get(0);
                    } else {
                        //If we get here, there's only the center tied with an outside one,
                        // so we prefer the other answer over the center
                        if (minimumHeights.get(0) == 1) {
                            return minimumHeights.get(1);
                        } else {
                            return minimumHeights.get(0);
                        }
                    }
                }
            }
        }
        return -1;
    }

    private ArrayList<Integer> getIndicesOfExtreme(int[] array, boolean maximum) {
        ArrayList<Integer> extremes = new ArrayList<>();

        int extreme = maximum ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        //Find largest value
        for (int num : array) {
            if ((num > extreme && maximum) || (num < extreme && !maximum)) {
                extreme = num;
            }
        }
        //Find all that are at largest value
        for (int i = 0; i < array.length; i++) {
            if (extreme == array[i]) {
                extremes.add(i);
            }
        }
        return extremes;
    }

    private int[] convertPredictionToSums(GlyphColor[] prediction) {
        //First index is the number of greys, second index is the number of browns
        int[] greyBrown = {0, 0};

        for (GlyphColor curr : prediction) {
            if (curr.equals(GlyphColor.GREY)) {
                greyBrown[0]++;
            }
            if (curr.equals(GlyphColor.BROWN)) {
                greyBrown[1]++;
            }
        }
        return greyBrown;
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
     * @return An array of three columns, representing what the next glyph would have to be would this one be placed
     */
    private GlyphColor[] getPrediction(GlyphColor newGlyph, int columnNum, Snake snakeTarget) {
        int[] empties = new int[3];
        for (int i = 0; i < empties.length; i++) {
            empties[i] = getFirstEmpty(i);
        }

        GlyphColor[] predictions = new GlyphColor[] {GlyphColor.NONE, GlyphColor.NONE, GlyphColor.NONE};

        //If there's no space for the new glyph or it doesn't match the target, then there are no future possibilities if we place here
        if (empties[columnNum] == -1 || !snakeTarget.getGlyphMap()[columnNum][empties[columnNum]].equals(newGlyph)) {
            return predictions;
        } else {
            //Assume the glyph gets placed
            empties[columnNum]++;
        }

        for (int i = 0; i < empties.length; i++) {
            //Get the glyph possibility at the empty space and puts it in the array
            GlyphColor prediction = snakeTarget.getGlyphMap()[columnNum][empties[i]];
            predictions[i] = prediction;
        }

        return predictions;
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
