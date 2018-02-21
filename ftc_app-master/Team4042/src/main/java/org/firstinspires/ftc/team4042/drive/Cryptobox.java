package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hazel on 2/13/2018.
 */

public class Cryptobox {
    public enum GlyphColor { GREY, BROWN, NONE, EITHER }

    private GlyphColor[][] glyphs = new GlyphColor[3][4];

    private File file = null;
    private Telemetry telemetry;

    private int uiX = 0;
    private int uiY = 0;

    private boolean rejectGlyph;

    /**
     * The first index is the column, the second index is the row
     * The rows are built from bottom to top, so index 0 is the
     * lowest position and index 3 the highest position
     */
    private final static GlyphColor[][] snake1 = {
            {GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY},
            {GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY},
            {GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN}};
    private final static GlyphColor[][] snake2 = {
            {GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN, GlyphColor.BROWN},
            {GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY, GlyphColor.BROWN},
            {GlyphColor.BROWN, GlyphColor.BROWN, GlyphColor.GREY, GlyphColor.GREY}};

    public enum Snake {
        ONE(snake1), TWO(snake2), NONE(new GlyphColor[0][0]);
        private GlyphColor[][] glyphMap;
        private Snake(GlyphColor[][] glyphMap) {
            this.glyphMap = glyphMap;
        }

        public GlyphColor[][] getGlyphMap() {
            return glyphMap;
        }
    }

    private GlyphPlacementSystem glyphPlacementSystem;

    private int numGlyphsPlaced;

    private Snake snakeTarget;

    public Cryptobox(Telemetry telemetry, GlyphPlacementSystem glyphPlacementSystem) {
        this (telemetry);
        this.glyphPlacementSystem = glyphPlacementSystem;
    }

    public Cryptobox(Telemetry telemetry) {
        this.telemetry = telemetry;
        this.file = new File("./storage/emulated/0/bluetooth/cryptobox.txt");
        this.numGlyphsPlaced = 0;
    }

    public void clear() {
        for (int i = 0; i < glyphs.length; i++) {
            for (int j = 0; j < glyphs[0].length; j++) {
                glyphs[i][j] = GlyphColor.NONE;
            }
        }
    }

    /**
     * Reads the file and stores data into cryptobox array
     */
    public void loadFile() {
        if (file == null) { return; } //Can't load a null file

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            for (int r = 0; r < 4 && (line = bufferedReader.readLine()) != null; r++) {
                //Reads the lines from the file in order
                //store each one onto array
                if (line.length() > 0) {
                    String[] row = line.split(" ");

                    for (int c = 0; c < row.length; c++) {
                        String pos = row[c];
                        glyphs[r][c] = pos.equalsIgnoreCase("G") ? GlyphColor.GREY :
                                pos.equalsIgnoreCase("B") ? GlyphColor.BROWN : GlyphColor.NONE;
                    }

                    telemetry.update();
                }
            }
            fileReader.close();
        } catch (Exception e) {
            telemetry.addData("Err load file:", e);
        }
    }

    /**
     * Writes current cryptobox array to the file
     */
    public void writeFile() {
        if (file == null) { return; } //Can't load a null file

        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter print_stream = new PrintWriter(fileWriter);
            //write array to file for each row and col
            print_stream.print(this.toString());
        } catch (Exception e) {
            telemetry.addData("Err load file:", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder("\n");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 3; c++) {
                String val = this.glyphs[c][r].equals(GlyphColor.BROWN) ? "B" : this.glyphs[c][r].equals(GlyphColor.GREY) ? "G" : "N";
                toString.append(val);
            }
            toString.append("\n");
        }
        return toString.toString();
    }

    public void uiUp() {
        if (uiY != 0) {
            uiY--;
        }
    }

    public void uiDown() {
        if (uiY != 3) {
            uiY++;
        }
    }

    public void uiLeft() {
        if (uiX != 2) {
            uiX++;
        }
    }

    public void uiRight() {
        if (uiX != 0) {
            uiX--;
        }
    }

    /**
     * Sets the glyph color at the ui's target
     * @param newGlyph The glyph color to set to
     */
    public void setGlyphAtUi(GlyphColor newGlyph) {
        glyphs[uiX][3 - uiY] = newGlyph;
        writeFile();
    }

    /**
     * Returns a more user-readable ui
     * @param cursor Whether or not to display a cursor
     * @return A readable ui
     */
    public String uiToString(boolean cursor) {
        StringBuilder toString = new StringBuilder("\n");
        for (int r = 3; r >= 0; r--) {
            for (int c = 0; c < 3; c++) {
                String val;
                if (cursor && c == uiX && r == (3 - uiY)) {
                    val = "X";
                } else {
                    val = this.glyphs[c][r].equals(GlyphColor.BROWN) ? "B" : this.glyphs[c][r].equals(GlyphColor.GREY) ? "G" : "N";
                }
                toString.append(val).append(" ");
            }
            toString.append("\n");
        }
        return toString.toString();
    }

    public int getNumGlyphsPlaced() {
        return numGlyphsPlaced;
    }

    public Snake getSnakeTarget() {
        return snakeTarget;
    }

    public void toggleSnakeTarget() {
        if (snakeTarget.equals(Snake.ONE)) {
            snakeTarget = Snake.TWO;
        } else if (snakeTarget.equals(Snake.TWO)) {
            snakeTarget = Snake.ONE;
        }
    }

    public void setSnakeTarget(Snake snakeTarget) {
        this.snakeTarget = snakeTarget;
    }

    public void incrementGlyphsPlaced() {
        numGlyphsPlaced++;
    }

    /**
     * Determines the cipher based off of the first glyph and its column target
     */
    public void cipherFirstGlyph(GlyphColor newGlyph, int column) {
        switch (column) {
            case 0:
                snakeTarget = newGlyph.equals(GlyphColor.BROWN) ? Snake.ONE : Snake.TWO;
                break;
            case 1:
                snakeTarget = newGlyph.equals(GlyphColor.GREY) ? Snake.ONE : Snake.TWO;
                break;
            case 2:
                snakeTarget = newGlyph.equals(GlyphColor.GREY) ? Snake.ONE : Snake.TWO;
                break;
        }

    }

    /**
     * Places a glyph and returns the next target
     * @param newGlyph The glyph currently held to place
     * @return The next color glyph to get
     */
    public int[] placeGlyph(GlyphColor newGlyph) {

        if (numGlyphsPlaced == 12) {
            return new int[]{0, 0};
        }

        int column;

        if (numGlyphsPlaced == 0) {
            return placeFirstGlyph(newGlyph);
        } else {
            if (snakeTarget.equals(Snake.NONE)) {
                return noCipherMatch(newGlyph);
            } else {
                int[][] greyBrowns = getPredictionSums(newGlyph);

                if (glyphMatchesCipher(greyBrowns)) {
                    column = getBestColumnIndex(greyBrowns);

                    //Place at the height of one over the lowest
                    int maximumHeight = getRowTarget();

                    if (driveGlyphPlacer(newGlyph, maximumHeight, column)) {
                        int grey = greyBrowns[column][0];
                        int brown = greyBrowns[column][1];

                        if (numGlyphsPlaced == 12) {
                            return new int[]{0, 0};
                        }

                        return new int[]{grey, brown};
                    } else { //Ran out of space in that column
                        return new int[]{0, 0};
                    }
                } else { //The glyph doesn't match the cipher
                    return noCipherMatch(newGlyph);
                }
            }
        }
    }

    private int[] noCipherMatch(GlyphColor newGlyph) {
        snakeTarget = Snake.NONE;
        if (rejectGlyph) {
            return new int[] {-1, -1};
        } else {
            int column = getShortestColumn(Arrays.asList(0, 1, 2));
            driveGlyphPlacerNoCipher(newGlyph, getRowTarget(), column);
            return new int[] {1, 1};
        }
    }

    private int getRowTarget() {
        int[] height = new int[glyphs.length];
        int maximumHeight = Integer.MIN_VALUE;
        for (int i = 0; i < height.length; i++) {
            int curr = getFirstEmpty(i);
            height[i] = curr;
            if (curr > maximumHeight) {
                maximumHeight = curr;
            }
        }

        //The one with the most glyphs
        telemetry.log().add("height: [" + height[0] + ", " + height[1] + ", " + height[2] + "]");
        telemetry.log().add("maximum height: " + maximumHeight);
        return maximumHeight;
    }

    private boolean glyphMatchesCipher(int[][] greyBrowns) {
        int[] cipherFailure = new int[] {0, 0};

        for (int[] greyBrown : greyBrowns) {
            if (!Arrays.equals(greyBrown, cipherFailure)) {
                return true;
            }
        }
        return false;
    }

    private int[][] getPredictionSums(GlyphColor newGlyph) {
        GlyphColor[][] predictions = new GlyphColor[3][3];
        for (int i = 0; i < glyphs.length; i++) {
            //Contains the predictions for if we put the glyph in that column
            GlyphColor[] prediction = getPrediction(newGlyph, i);
            telemetry.log().add("Prediction " + i + ": " + prediction[0] + " " + prediction[1] + " " + prediction[2]);
            predictions[i] = prediction;
        }

        int[][] greyBrowns = new int[3][2];
        for (int i = 0; i < greyBrowns.length; i++) {
            greyBrowns[i] = convertPredictionToSums(predictions[i]);
        }
        return greyBrowns;
    }

    private int[] placeFirstGlyph(GlyphColor newGlyph) {
        //Set up which snake we want to target, then get the other color glyph next
        cipherFirstGlyph(newGlyph, 1);

        //Always place the first glyph in the center column
        driveGlyphPlacer(newGlyph, 0, 1);

        return newGlyph.equals(GlyphColor.GREY) ? new int[] { 1, 2 } : new int[] { 2, 1 };
    }

    public void driveGlyphPlacerNoCipher(GlyphColor newGlyph, int row, int column) {
        addGlyphToColumnNoCipher(newGlyph, column);
        numGlyphsPlaced++;

        if (glyphPlacementSystem != null) {
            telemetry.log().add("target: " + column + ", " + (3 - row));
            glyphPlacementSystem.uiTarget(column, Range.clip(3 - row, 0, 2)); //We subtract from 3 because the glyph placer reads 0 -> 3 and this class reads 3 -> 0
            glyphPlacementSystem.drive.glyphLocate();
        }

        writeFile();
    }

    public boolean driveGlyphPlacer(GlyphColor newGlyph, int row, int column) {
        if (addGlyphToColumn(newGlyph, column)) {
            numGlyphsPlaced++;

            if (glyphPlacementSystem != null) {
                telemetry.log().add("target: " + column + ", " + (3 - row));
                glyphPlacementSystem.uiTarget(column, Range.clip(3 - row, 0, 2)); //We subtract from 3 because the glyph placer reads 0 -> 3 and this class reads 3 -> 0
                glyphPlacementSystem.drive.glyphLocate();
            }

            writeFile();

            return true;
        }
        return false;
    }

    private int getBestColumnIndex(int[][] greyBrowns) {
        //Determine which prediction is the most desirable based on which one
        // has the highest sum with a tiebreaker of the variance
        ArrayList<Integer> largestSums = getLargestSums(greyBrowns);

        if (largestSums.size() == 1) {
            return largestSums.get(0);
        } else {
            //The one with the least variance is returned
            ArrayList<Integer> smallestVariance = getSmallestVariance(largestSums, greyBrowns);
            if (smallestVariance.size() == 1) {
                return smallestVariance.get(0);
            } else {
                //The one with the tallest glyph is returned
                return getShortestColumn(smallestVariance);
            }
        }
    }

    private ArrayList<Integer> getLargestSums(int[][] greyBrowns) {
        int[] sums = new int[3];

        for (int i = 0; i < greyBrowns.length; i++) {
            sums[i] = greyBrowns[i][0] + greyBrowns[i][1];
            telemetry.log().add("Sum " + i + ": " + sums[i]);
        }

        return getIndicesOfExtreme(sums, true);
    }

    private ArrayList<Integer> getSmallestVariance(ArrayList<Integer> largestSums, int[][] greyBrowns) {
        int[] variances = new int[3];
        //Get the variances of the differences between the two number of greys and browns
        for (int j = 0; j < greyBrowns.length; j++) {
            if (largestSums.contains(j)) {
                variances[j] = Math.abs(greyBrowns[j][0] - greyBrowns[j][1]);
            } else {
                variances[j] = Integer.MAX_VALUE;
            }
        }

        //Get the one(s) with the smallest variance
        return getIndicesOfExtreme(variances, false);
    }

    private int getShortestColumn(List<Integer> minimums) {
        //Get the heights of the columns
        int[] height = new int[3];
        for (int k = 0; k < height.length; k++) {
            if (minimums.contains(k)) {
                height[k] = getFirstEmpty(k);
            } else {
                height[k] = Integer.MAX_VALUE;
            }
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

    private ArrayList<Integer> getIndicesOfExtreme(int[] array, boolean maximum) {
        ArrayList<Integer> extremes = new ArrayList<>();

        int extreme = maximum ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        //Find extreme value
        for (int num : array) {
            if ((num > extreme && maximum) || (num < extreme && !maximum)) {
                if (!(!maximum && num == -1)) { //Don't count -1's in minimum calculations
                    extreme = num;
                }
            }
        }
        telemetry.log().add("Extreme : " + extreme);
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
     * Places a glyph in the column indicated, at the lowest empty space,
     * if that space matches the snake target
     * @param newGlyph The new glyph color to add
     * @param columnNum The column to place in
     * @return If there was space in the column to place and
     * that space matches the indicated target
     */
    private boolean addGlyphToColumn(GlyphColor newGlyph, int columnNum) {

        int emptySpace = getFirstEmpty(columnNum);
        if (emptySpace != -1) {
            if (snakeTarget.getGlyphMap()[columnNum][emptySpace].equals(newGlyph)) {
                //If it matches the target, place that glyph there
                glyphs[columnNum][emptySpace] = newGlyph;
                return true;
            } else {
                //If it doesn't match the target, then you can't place there so return false
                return false;
            }
        }
        return false;
    }

    private void addGlyphToColumnNoCipher(GlyphColor newGlyph, int columnNum) {
        int emptySpace = getFirstEmpty(columnNum);
        if (emptySpace != -1) {
            glyphs[columnNum][emptySpace] = newGlyph;
        }
    }

    /**
     * Simulates placing a glyph in the target location and returns the grey/brown possibilities for it
     * @param newGlyph The new glyph color to add
     * @param columnNum The column to place in
     * @return An array of three columns, representing what the next glyph would have to be would this one be placed
     */
    private GlyphColor[] getPrediction(GlyphColor newGlyph, int columnNum) {
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
            //if it is 0, you cannot place above this position, and should not count it in predictions
            if (!(empties[i] == 0 && i == columnNum) && empties[i] != -1 && empties[i] != 4) {
                GlyphColor prediction = snakeTarget.getGlyphMap()[i][empties[i]];
                predictions[i] = prediction;
            }
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
            if (column[i].equals(GlyphColor.NONE)) {
                return i;
            }
        }
        return -1;
    }
}
