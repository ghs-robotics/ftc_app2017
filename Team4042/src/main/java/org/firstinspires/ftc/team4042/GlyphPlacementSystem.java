package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Ryan Whiting on 10/17/2017.
 */

public class GlyphPlacementSystem
{
    private int targetX;
    private int targetY;
    private int currentX;
    private Position currentY;
    private String baseOutput;
    private DigitalSensor homeLimit = new DigitalSensor("limit");
    private DcMotor verticalDrive;
    private final int BASE_DISP = 0;
    private final int BLOCK_DISP = 0;

    private enum Position{BACK, TOP, MID_TOP, MID_BOTTOM, BOTTOM}

    public GlyphPlacementSystem(HardwareMap map)
    {
        this(0, 0, map);
    }

    public GlyphPlacementSystem(int currentX, int currentY, HardwareMap map)
    {
        this.homeLimit.initialize(map);
        this.verticalDrive = map.dcMotor.get("vertical drive");
        this.baseOutput = "[ _ _ _ ]\n[ _ _ _ ]\n[ _ _ _ ]\n[ _ _ _ ]";
        this.targetX = currentX;
        this.targetY = currentY;
        this.currentX = currentX;
        this.currentY = Position.BACK;

        verticalDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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

            case(3): output[12] = 'X'; break;

            case(4): output[14] = 'X'; break;

            case(5): output[16] = 'X'; break;

            case(6): output[22] = 'X'; break;

            case(7): output[24] = 'X'; break;

            case(8): output[26] = 'X'; break;

            case(9): output[32] = 'X'; break;

            case(10): output[34] = 'X'; break;

            case(11): output[36] = 'X'; break;
        }

        return "\n" + new String(output);
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
        verticalDrive.setTargetPosition(BASE_DISP + BLOCK_DISP*targetY);

        /*
        Assuming motor forward power moves it right and up
        Move sideways motor (block distance * (targetX - currentX))
        Move vertical motor (block distance * (targetY - currentY))
         */

        goToHome();
    }

    public void runToPosition() {
        if (homeLimit.getState()) {
            verticalDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        if (verticalDrive.getCurrentPosition() == 0) {
            verticalDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }
    }

    public void goToHome() {
        while (!homeLimit.getState()) {
            verticalDrive.setPower(-1);
        }
        verticalDrive.setPower(0);
        verticalDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        currentY = Position.BACK;
    }
}
