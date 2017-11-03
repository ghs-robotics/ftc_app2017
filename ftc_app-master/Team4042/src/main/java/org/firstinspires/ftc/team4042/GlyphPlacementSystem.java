package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    public DigitalSensor homeLimit = new DigitalSensor("limit");
    public DcMotor verticalDrive;
    private Servo grabbyBoi;
    private CRServo slidyBoi;
    private final int BASE_DISP = 900;
    private final int BLOCK_DISP = 700;
    private final double FORWARD_SPEED = 0.5;
    private final double REVERSE_SPEED = 1;
    private boolean isPlacing;
    public boolean override;

    private enum Position{HOME, RAISED, TOP, MID, BOT, ERROR}

    public GlyphPlacementSystem(HardwareMap map)
    {
        this(0, 0, map, false);
    }

    public GlyphPlacementSystem(int currentX, int currentY, HardwareMap map, boolean override)
    {
        this.homeLimit.initialize(map);
        this.verticalDrive = map.dcMotor.get("vertical drive");
        this.grabbyBoi = map.servo.get("hand");
        this.slidyBoi = map.crservo.get("slidy boi");

        this.baseOutput = "[ _ _ _ ]\n[ _ _ _ ]\n[ _ _ _ ]";
        this.targetX = currentX;
        this.targetY = currentY;
        this.currentX = currentX;
        this.currentY = Position.HOME;
        this.override = override;
        this.isPlacing = false;

        verticalDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //returns the current position of the glyph placement system
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

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() { return  currentY.ordinal(); }

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

    ////this is not done////
    public void place() {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        isPlacing = true;
        //Current servo positions are placeholder until we have actual numbers
        closeHand();

        verticalDrive.setTargetPosition(BASE_DISP);

        if( currentY.equals(Position.RAISED) && timer.milliseconds() >= 500) {
            timer.reset();

            //TODO: horizontal index code here
            verticalDrive.setTargetPosition(BLOCK_DISP * (targetY + 1));

            if( (currentY.ordinal() - 2 == targetY || override) && timer.milliseconds() >= 1000) {
                openHand();
                goToHome();
                isPlacing = false;
            }
        }
        else if (timer.milliseconds() > 10000) {
            goToHome();
            isPlacing = false;
        }

        /*
        Assuming motor forward power moves it right and up
        Move sideways motor (block distance * (targetX - currentX))
        Move vertical motor (block distance * (targetY - currentY))
         */


    }

    public void openHand() {
        grabbyBoi.setPosition(.57);
    }

    public void closeHand() {
        grabbyBoi.setPosition(1);
    }

    //TODO: this method should run in the main loop
    public void runToPosition() {
        int pos = verticalDrive.getCurrentPosition();

        if (pos < verticalDrive.getTargetPosition()) {
            verticalDrive.setPower(FORWARD_SPEED);
        }
        else {
            verticalDrive.setPower(0);
        }

        if (homeLimit.getState()) {
            verticalDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            verticalDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        //these statements determine the position of the vertical drive with a 20 tick margin of error
        //the ERROR position does not necessarily indicate an error, but rather that the drive is in
        //a nonstandard position
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

    public void setHorizontalIndex() {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

    }

    public boolean getIsPlacing() { return isPlacing; }

    public void switchOverride() { override = !override; }

    //go home robot, ur drunk
    public void goToHome() {
        if (!homeLimit.getState()) {
            verticalDrive.setTargetPosition(0);
            verticalDrive.setPower(REVERSE_SPEED);
        }
    }
}
