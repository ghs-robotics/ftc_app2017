package org.firstinspires.ftc.team4042;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Ryan Whiting on 10/17/2017.
 */

public class GlyphPlacementSystem
{
    private int x;
    private int y;
    private String baseOutput;
    private Telemetry out;

    public GlyphPlacementSystem(Telemetry out)
    {
        this.out = out;
        this.baseOutput = "[_______]\n[_______]\n[_______]\n[_______]";
        this.x = 0;
        this.y = 0;
    }

    public GlyphPlacementSystem(Telemetry out, int x, int y)
    {
        this(out);
        this.x = x;
        this.y = y;
    }

    //returns the current position of the glyph placement system
    public String getPositionAsString()
    {
        char[] output = baseOutput.toCharArray();

        int position = x + 3 * y;
        switch(position)
        {
            case(0): output[2] = 'X'; break;

            case(1): output[4] = 'X'; break;

            case(2): output[6] = 'X'; break;

            case(3): output[13] = 'X'; break;

            case(4): output[15] = 'X'; break;

            case(5): output[17] = 'X'; break;

            case(6): output[24] = 'X'; break;

            case(7): output[26] = 'X'; break;

            case(8): output[28] = 'X'; break;

            case(9): output[35] = 'X'; break;

            case(10): output[37] = 'X'; break;

            case(11): output[39] = 'X'; break;
        }

        return new String(output);
    }

    public int getPosition() {
        return x + 3 * y;
    }

    public void up() {
        if (y != 0) {
            y -= 1;
        }
    }

    public void down() {
        if (y != 3) {
            y += 1;
        }
    }

    public void left() {
        if (x != 0) {
            x -= 1;
        }
    }

    public void right() {
        if (x != 2) {
            x += 1;
        }
    }

    public void place() {
        //TODO: motor code here
    }
}
