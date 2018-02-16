package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.team4042.autos.C;

/**
 * Created by Ryan on 11/2/2017.
 */

public class GlyphPlacementSystem {

    public final double HORIZONTAL_TRANSLATION_TIME = C.get().getDouble("TransTime");
    public final int PLACEMENT_ERROR_MARGIN = C.get().getInt("PlaceError");
    private final double PROPORTIONAL_CONSTANT = C.get().getDouble("Prop");
    private final double DERIV_CONSTANT = C.get().getDouble("Deriv");
    public int uiTargetX;
    public int uiTargetY;
    public Position currentY;
    public HorizPos currentX;
    private String baseOutput;
    private Drive drive;

    public ElapsedTime horizontalTimer = new ElapsedTime();

    //indicates the position of the lift in motor encoders
    public enum Position {
        //HOME(0), RAISED(1200), TOP(1600), MID(2000), BOT(2500), TRANSITION(-1);
        HOME(0), RAISEDBACK(1325), RAISED(1375), TOP(1525), MID(1875), BOT(2050), TRANSITION(-1);
        //HOME(10), RAISEDBACK(1350), RAISED(1401), TOP(1600), MID(1900), BOT(2200), TRANSITION(-1);

        private final Integer encoderVal;
        Position(Integer encoderVal) { this.encoderVal = encoderVal; }
        public Integer getEncoderVal() { return encoderVal; }
        @Override
        public String toString() { return this.name() + this.getEncoderVal(); }
    }

    //indicates the action of placing the robot is currently performing
    public enum Stage {
        HOME, PAUSE1, PAUSE2, PLACE1, PLACE2, RETURN1, RETURN2, GRAB, RELEASE, RESET
    }

    public enum HorizPos {
        LEFT(-.75), CENTER(0.0), RIGHT(.75);

        private final Double power;
        HorizPos(Double power) { this.power = power; }
        public Double getPower() { return power; }
        @Override
        public String toString() { return this.name() + this.getPower(); }
    }

    public GlyphPlacementSystem(HardwareMap map, Drive drive) {
        currentY = Position.HOME;
        this.drive = drive;
        //blank for UI output
        this.baseOutput = "[ _ _ _ ]\n[ _ _ _ ]\n[ _ _ _ ]";
}

    //converts the currently targeted position for the glyph placer into a string for UI output
    public String getTargetPositionAsString()
    {
        char[] output = baseOutput.toCharArray();
        int position = uiTargetX + 3 * uiTargetY;
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

    //sets the horizontal position for placing glyphs in auto
    public void setTarget(RelicRecoveryVuMark x, int y) {
        drive.targetY = GlyphPlacementSystem.Position.TOP;
        switch (x) {
            case LEFT:
                //uiTargetX = 0;
                drive.targetX = HorizPos.LEFT;
                break;
            case CENTER:
                //uiTargetX = 1;
                drive.targetX = HorizPos.CENTER;
                break;
            case RIGHT:
                //uiTargetX = 2;
                drive.targetX = HorizPos.RIGHT;
                break;
            default:
                //when in doubt, place in the middle
                //uiTargetX = 1;
                drive.targetX = HorizPos.CENTER;
                break;
        }
        //uiTargetY = y;
    }

    //sets the UI to display a desired target location
    public void uiTarget(int uiTargetX, int uiTargetY) {
        this.uiTargetX = uiTargetX;
        this.uiTargetY = uiTargetY;
    }

    //indexes the UI target location up one unit
    public int uiUp() {
        if (uiTargetY != 0) {
            uiTargetY -= 1;
        }
        return uiTargetY;
    }

    //indexes the UI target location down one unit
    public int uiDown() {
        if (uiTargetY != 2) {
            uiTargetY += 1;
        }
        return uiTargetY;
    }

    //indexes the UI target location left one unit
    public int uiLeft() {
        if (uiTargetX != 0) {
            uiTargetX -= 1;
        }
        return uiTargetX;
    }

    //indexes the UI target location right one unit
    public int uiRight() {
        if (uiTargetX != 2) {
            uiTargetX += 1;
        }
        return uiTargetX;
    }

    //sets the encoder target of the drive motor to the specified position
    public void setTargetPosition(Position position) {
        drive.setVerticalDrivePos(position.getEncoderVal());
    }

    //sets the encoder target of the drive motor to the fully returned encoder position
    public void setHomeTarget() {
        drive.setVerticalDrivePos(Position.HOME.getEncoderVal());
    }

    //runs the horizontal drive servo based on the power multipliers in HorizPos
    public void setXPower(HorizPos targetPos) {
        //if target = left(-1) and current = right(1)
        //we want to move left (-1)
        //so target - current
        if (!drive.targetX.equals(HorizPos.CENTER)) {
            double power = targetPos.getPower() - currentX.getPower();
            power = Range.clip(power, -1, 1);
            if (targetPos.equals(HorizPos.CENTER)) {
                power *= C.get().getDouble("returnSpeed");
            }
            drive.setHorizontalDrive(power);
            horizontalTimer.reset();
        }
    }

    //handles typewriter motion
    public void adjustBack(double mulch) {
        drive.setHorizontalDrive(mulch * drive.getHorizontalDrive() * -.4);
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        drive.log.add("adjusting back at power " + drive.getHorizontalDrive());
        while (timer.seconds() < .15) {  }
        drive.setHorizontalDrive(0);
    }

    public boolean xTargetReached(HorizPos targetPos) {
        //If you're going left or right, then use the timer to see if you should stop
        //drive.log.add("target x " + drive.targetX + " targetPos " + targetPos);
        if (((targetPos.equals(HorizPos.LEFT) || targetPos.equals(HorizPos.RIGHT)) && (horizontalTimer.seconds() >= HORIZONTAL_TRANSLATION_TIME)) ||
                //If you're going to the center and you hit the limit switch, stop
                (!drive.targetX.equals(HorizPos.CENTER) && targetPos.equals(HorizPos.CENTER) && drive.getCenterState()) ||
                (drive.targetX.equals(HorizPos.CENTER) && targetPos.equals(HorizPos.CENTER))) {
            if (drive.getHorizontalDrive() > 0) {
                adjustBack(1);
            }
            drive.setHorizontalDrive(0);
            currentX = targetPos;
            return true;
            //If we want to go to the center but miss the switch, it will reverse th u-track
        } if(!drive.targetX.equals(HorizPos.CENTER) && targetPos.equals(HorizPos.CENTER) && (horizontalTimer.seconds() >= (/*C.get().getDouble("missedCenter") + */HORIZONTAL_TRANSLATION_TIME))) {
            this.horizontalTimer.reset();
            if (this.currentX.equals(HorizPos.LEFT)){
                this.currentX = HorizPos.RIGHT;
                drive.targetX = HorizPos.RIGHT;
            }
            if (this.currentX.equals(HorizPos.RIGHT)){
                this.currentX = HorizPos.LEFT;
                drive.targetX = HorizPos.LEFT;
            }
            drive.setHorizontalDrive(-.5 * drive.getHorizontalDrive());
            //this.setXPower(HorizPos.CENTER);
        }
        return false;
    }

    public void runToPosition() {
        double power = ((double)drive.verticalDriveTargetPos() - (double)drive.verticalDriveCurrPos())/ PROPORTIONAL_CONSTANT + drive.uTrackRate * DERIV_CONSTANT;
        power = Math.abs(power) < 0.2 ? 0 : power;
        drive.setVerticalDrive(power);

        int pos = drive.verticalDriveCurrPos();

        if (pos < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.HOME;
        }
        else if (Math.abs(pos - Position.RAISED.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.RAISED;
        }
        else if (Math.abs(pos - Position.RAISEDBACK.getEncoderVal()) < PLACEMENT_ERROR_MARGIN) {
            currentY = Position.RAISEDBACK;
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
