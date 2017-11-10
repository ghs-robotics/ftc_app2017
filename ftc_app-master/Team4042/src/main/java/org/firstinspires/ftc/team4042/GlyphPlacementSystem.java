package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.util.Position;

/**
 * Created by meme on 11/2/2017.
 */

public class GlyphPlacementSystem {

    private Servo hand;
    private final int BASE_DISP = 900;
    private final int BLOCK_DISP = 700;
    private int targetX;
    private int targetY;
    private Position currentY;
    private String baseOutput;
    private Drive drive;

    private enum Position{HOME, RAISED, TOP, MID, BOT, ERROR}

    public GlyphPlacementSystem(HardwareMap map, Drive drive) {
        currentY = Position.HOME;
        this.drive = drive;
        this.baseOutput = "[ _ _ _ ]\n[ _ _ _ ]\n[ _ _ _ ]";
    }

    public String getTargetPositionAsString()
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
        }

        return "\n" + new String(output);
    }

    public void up() {
        if (targetY != 0) {
            targetY -= 1;
        }
    }

    public void down() {
        if (targetY != 2) {
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

    public void setTargetPosition() {
        drive.closeHand();
        drive.verticalDrivePos(BASE_DISP + (targetY + 1)*BLOCK_DISP);
    }

    public void setHomeTarget() {
        drive.openHand();
        drive.verticalDrivePos(10);
    }

    public void runToPosition() {
        drive.verticalDrive((drive.verticalDriveTargetPos() - drive.verticalDriveCurrPos())/ 100);

        int pos = drive.verticalDriveCurrPos();

        if (pos == 0) {
            currentY = Position.HOME;
        }
        else if (pos > BASE_DISP - 10 && pos < BASE_DISP + 10) {
            currentY = Position.RAISED;
        }
        else if (pos > (BASE_DISP + BLOCK_DISP) - 10 && pos < (BASE_DISP + BLOCK_DISP) + 10) {
            currentY = Position.TOP;
        }
        else if (pos > (BASE_DISP + 2*BLOCK_DISP) - 10 && pos < (BASE_DISP + 2*BLOCK_DISP) + 10) {
            currentY = Position.MID;
        }
        else if (pos > (BASE_DISP + 3*BLOCK_DISP) - 10 && pos < (BASE_DISP + 3*BLOCK_DISP) + 10) {
            currentY = Position.BOT;
        }
        else {
            currentY = Position.ERROR;
        }


    }
}
