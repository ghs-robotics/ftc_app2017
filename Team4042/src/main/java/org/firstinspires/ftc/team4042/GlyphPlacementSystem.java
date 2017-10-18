package org.firstinspires.ftc.team4042;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Ryan Whiting on 10/17/2017.
 */

public class GlyphPlacementSystem
{
    private int position;
    private String baseOutput;
    private Telemetry out;

    public GlyphPlacementSystem(Telemetry out)
    {
        this.out = out;
        this.baseOutput = "[_______]\n[_______]\n[_______]\n[_______]";
    }

    public GlyphPlacementSystem(int x, int y)
    {
        position = x + 3 * y;
    }

    //returns the current position of the glyph placement system
    public int getIndex()
    {
        char[] output = baseOutput.toCharArray();

        switch(position)
        {
            case(0): output[2] = 'X';

            case(1): output[4] = 'X';

            case(2): output[6] = 'X';

            case(3): output[13] = 'X';

            case(4): output[15] = 'X';

            case(5): output[17] = 'X';

            case(6): output[24] = 'X';

            case(7): output[26] = 'X';

            case(8): output[28] = 'X';

            case(9): output[35] = 'X';

            case(10): output[37] = 'X';

            case(11): output[39] = 'X';
        }

        out.addLine(output.toString());

        return position;
    }
}
