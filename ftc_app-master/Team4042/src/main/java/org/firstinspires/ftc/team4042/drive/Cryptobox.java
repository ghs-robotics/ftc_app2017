package org.firstinspires.ftc.team4042.drive;

import android.content.SharedPreferences;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team4042.autos.AutoInstruction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by Hazel on 2/13/2018.
 */

public class Cryptobox {
    private enum GlyphColor { GREY, BROWN, NONE, EITHER }

    private GlyphColor[][] glyphs = new GlyphColor[3][4];

    private File file = null;
    private Telemetry telemetry;

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
        }catch (Exception e) {
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
            for (int r = 0; r < 4; r++) {
                String[] val = new String[3];
                for (int c = 0; c < 3; c++) {
                    val[c] = this.glyphs[r][0].equals(GlyphColor.BROWN) ? "B" : this.glyphs[r][0].equals(GlyphColor.BROWN) ? "G" : "N";
                }
                print_stream.println(val[0] + " " + val[1] + " " + val[2]);
            }
        }catch (Exception e) {
            telemetry.addData("Err load file:", e);
        }
    }

    public Cryptobox(Telemetry telemetry) {
        for (int i = 0; i < glyphs.length; i++) {
            for (int j = 0; j < glyphs[0].length; j++) {
                glyphs[i][j] = GlyphColor.NONE;
            }
        }
        this.telemetry = telemetry;
        this.file = new File("./storage/emulated/0/bluetooth/cryptobox.txt");
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
        }
        return false;
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
