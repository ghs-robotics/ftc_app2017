package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by Ryan on 11/2/2017.
 */

public class GlyphPlacementSystem {

    public final double HORIZONTAL_TRANSLATION_TIME = 2;
    public final int PLACEMENT_ERROR_MARGIN = 100;
    private final int SPEED_FACTOR_CONSTANT = 100;
    private int targetX;
    private int targetY;
    public Position currentY;
    public HorizPos currentX;
    private String baseOutput;
    private Drive drive;

    public ElapsedTime horizontalTimer = new ElapsedTime();

    public enum Position {
        //HOME(0), RAISED(1200), TOP(1600), MID(2000), BOT(2500), TRANSITION(-1);
        HOME(10), RAISED(1200), TOP(1600), MID(1900), BOT(2200), TRANSITION(-1);

        private final Integer encoderVal;
        Position(Integer encoderVal) { this.encoderVal = encoderVal; }
        public Integer getEncoderVal() { return encoderVal; }
        @Override
        public String toString() { return this.name() + this.getEncoderVal(); }
    }

    public enum Stage {
        HOME, PAUSE1, PAUSE2, PLACE1, PLACE2, RETURN1, RETURN2, GRAB, RELEASE, RESET
    }

    public enum HorizPos {
        LEFT(-.82), CENTER(0.0), RIGHT(.82);

        private final Double power;
        HorizPos(Double power) { this.power = power; }
        public Double getPower() { return power; }
        @Override
        public String toString() { return this.name() + this.getPower(); }
    }

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

    public void setTarget(RelicRecoveryVuMark x, int y) {
        drive.targetY = GlyphPlacementSystem.Position.TOP;
        switch (x) {
            case LEFT:
                targetX = 0;
                drive.targetX = HorizPos.LEFT;
                break;
            case CENTER:
                targetX = 1;
                drive.targetX = HorizPos.CENTER;
                break;
            case RIGHT:
                targetX = 2;
                drive.targetX = HorizPos.RIGHT;
                break;
            default:
                //when in doubt, place in the middle
                targetX = 1;
                drive.targetX = HorizPos.CENTER;
                break;
        }
        targetY = y;
    }

    public int up() {
        if (targetY != 0) {
            targetY -= 1;
        }
        return targetY;
    }

    public int down() {
        if (targetY != 2) {
            targetY += 1;
        }
        return targetY;
    }

    public int left() {
        if (targetX != 0) {
            targetX -= 1;
        }
        return targetX;
    }

    public int right() {
        if (targetX != 2) {
            targetX += 1;
        }
        return targetX;
    }

    public void setTargetPosition(Position position) {
        drive.setVerticalDrivePos(position.getEncoderVal());
    }

    public void setHomeTarget() {
        drive.setVerticalDrivePos(Position.HOME.getEncoderVal());
    }

    public void setXPower(HorizPos targetPos) {
        //if target = left(-1) and current = right(1)
        //we want to move left (-1)
        //so target - current
        if (targetX != 1) {
            double power = targetPos.getPower() - currentX.getPower();
            power = Range.clip(power, -1, 1);

            drive.setHorizontalDrive(power);
            horizontalTimer.reset();
        }
    }

    public void adjustBack() {
        drive.setHorizontalDrive(-.2);
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while (timer.seconds() < .2) {  }
        drive.setHorizontalDrive(0);
    }

    public boolean xTargetReached(HorizPos targetPos) {
        //If you're going left or right, then use the timer to see if you should stop
        drive.log.add("target x " + targetX + " targetPos " + targetPos);
        if (((targetPos.equals(HorizPos.LEFT) || targetPos.equals(HorizPos.RIGHT)) && (horizontalTimer.seconds() >= HORIZONTAL_TRANSLATION_TIME)) ||
                //If you're going to the center and you hit the limit switch, stop
                (targetX != 1 && targetPos.equals(HorizPos.CENTER) && drive.getCenterState()) ||
                (targetX == 1 && targetPos.equals(HorizPos.CENTER))) {
            drive.setHorizontalDrive(0);
            currentX = targetPos;
            return true;
        }
        return false;
    }

    public void runToPosition() {
        drive.setVerticalDrive((drive.verticalDriveTargetPos() - drive.verticalDriveCurrPos())/ SPEED_FACTOR_CONSTANT);

        int pos = drive.verticalDriveCurrPos();

        if (pos < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.HOME;
        }
        else if (Math.abs(pos - Position.RAISED.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.RAISED;
        }
        else if (Math.abs(pos - Position.TOP.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.TOP;
        }
        else if (Math.abs(pos - Position.MID.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.MID;
        }
        else if (Math.abs(pos - Position.BOT.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.BOT;
        }
        else {
            currentY = Position.TRANSITION;
        }


    }
}
