package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.util.Position;

/**
 * Created by meme on 11/2/2017.
 */

public class AlternateGlyphPlacementSystem {

    private DcMotor verticalDrive;
    private Servo hand;
    private final int BASE_DISP = 0;
    private final int BLOCK_DISP = 1500;
    private Position currentY;

    private enum Position{HOME, RAISED, TOP, MID, BOT, ERROR}

    public AlternateGlyphPlacementSystem(HardwareMap map) {
        verticalDrive = map.dcMotor.get("vertical drive");
        currentY = Position.HOME;
    }

    public void setTargetPosition(int block) {
        verticalDrive.setTargetPosition(BASE_DISP + block*(BLOCK_DISP + 1));
    }

    public void runToPosition() {
        verticalDrive.setPower((verticalDrive.getTargetPosition() - verticalDrive.getCurrentPosition())/ 500);

        int pos = verticalDrive.getCurrentPosition();

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
