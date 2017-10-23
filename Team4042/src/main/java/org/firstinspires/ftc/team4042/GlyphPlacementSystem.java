package org.firstinspires.ftc.team4042;

/**
 * Created by Ryan Whiting on 10/17/2017.
 */

public class GlyphPlacementSystem
{
    private int targetX;
    private int targetY;
    private int currentX;
    private int currentY;
    private String baseOutput;

    public GlyphPlacementSystem()
    {
        this(0, 0);
    }

    public GlyphPlacementSystem(int currentX, int currentY)
    {
        this.baseOutput = "[_______]\n[_______]\n[_______]\n[_______]";
        this.targetX = currentX;
        this.targetY = currentY;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    //returns the current position of the glyph placement system
    public String getPositionAsString()
    {
        char[] output = baseOutput.toCharArray();

        int position = targetX + 3 * targetY;
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
        return targetX + 3 * targetY;
    }

    public void up() {
        if (targetY != 0) {
            targetY -= 1;
        }
    }

    public void down() {
        if (targetY != 3) {
            targetY += 1;
        }
    }

    public void left() {
        if (targetX != 0) {
            targetX -= 1;
        }
    }

    public void right() {
        if (targetX != 2) {
            targetX += 1;
        }
    }

    public void place() {
        //TODO: motor code here
        /*
        Assuming motor forward power moves it right and up
        Move sideways motor (block distance * (targetX - currentX))
        Move vertical motor (block distance * (targetY - currentY))
         */
    }
}
