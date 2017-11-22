package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team4042.drive.Drive;
import org.firstinspires.ftc.team4042.drive.GlyphPlacementSystem;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;

@TeleOp(name = "Mecanum", group="drive")
public class TeleOpMecanum extends OpMode {

    private boolean aA = false;
    private boolean aY = false;

    private double adjustedSpeed;

    private boolean aLeftBumper = false;
    private boolean aRightBumper = false;

    private boolean bUp;
    private boolean bDown;
    private boolean bLeft;
    private boolean bRight;
    private boolean bA;

    private GlyphPlacementSystem.Position targetY;
    private GlyphPlacementSystem.HorizPos targetX;
    private boolean uTrackAtBottom = true;
    private GlyphPlacementSystem.Stage stage;

    //Declare OpMode members.
    private Drive drive = new MecanumDrive(true);

    /**
    GAMEPAD 1:
      Joystick 1 X & Y - movement
      Joystick 2 X - rotation
      Bumpers - speed modes
      Triggers -
      Dpad -
      A - toggle verbose
      B -
      X -
      Y -

    GAMEPAD 2:
      Joystick 1 Y - adjust u-track
      Joystick 2 Y - servo arm
      Bumpers - run intakes backwards
      Triggers - run intakes forwards
      Dpad - placer
      A - places glyph
      B - opens/closes glyph hand
      X - u-track reset
      Y - glyph override
     */

    @Override
    public void init() {
        drive.initialize(telemetry, hardwareMap);
        drive.runWithEncoders();
        //drive.glyph = new GlyphPlacementSystem(hardwareMap, drive);
        //drive.glyph = new GlyphPlacementSystem(hardwareMap);
        telemetry.update();

        targetY = GlyphPlacementSystem.Position.TOP;
        stage = GlyphPlacementSystem.Stage.HOME;
        drive.glyph.setHomeTarget();

        adjustedSpeed = MecanumDrive.FULL_SPEED;
    }

    @Override
    public void start() {
        //Moves the servo to the up position
        drive.jewelUp();
    }
    
    @Override
    public void loop() {

        //1 Y - toggle extendo
        if (gamepad1.y && !aY) {
            Drive.isExtendo = !Drive.isExtendo;
        }
        aY = gamepad1.y;

        //1 A - toggle verbose
        if (gamepad1.a && !aA) {
            drive.toggleVerbose();
        }
        aA = gamepad1.a;

        //Drives the robot
        drive.drive(false, gamepad1, gamepad2, adjustedSpeed * MecanumDrive.FULL_SPEED);

        if (Drive.useGyro) {
            drive.useGyro();
        }

        speedModes();

        //If you're at the bottom, haven't been pushing a, and now are pushing a
        if (uTrackAtBottom && !bA && gamepad2.a) {
            uTrack();
        }
        //If you're not at the bottom and are pushing a
        if (!uTrackAtBottom && gamepad2.a) {
            uTrack();
        }
        bA = gamepad2.a;

        //Glyph locate
        glyphLocate();

        //Right trigger of the b controller runs the right intake forward
        intakes();

        drive.glyph.runToPosition();
        telemetryUpdate();
    }

    private void speedModes() {
        //If you push the left bumper, dials the speed down
        if (gamepad1.left_bumper && !aLeftBumper && (adjustedSpeed - 0.25) >= 0) {
            adjustedSpeed -= 0.25;
        }
        aLeftBumper = gamepad1.left_bumper;
        //Right bumper - dial speed up
        if (gamepad1.right_bumper && !aRightBumper && (adjustedSpeed + 0.25) <= MecanumDrive.FULL_SPEED)
        {
            adjustedSpeed += 0.25;
        }
        aRightBumper = gamepad1.right_bumper;
    }

    private void uTrack() {
        switch (stage) {
            case HOME: {
                //Close the hand
                drive.closeHand();
                telemetry.addData("closed hand", "closed hand");
                stage = GlyphPlacementSystem.Stage.PLACE1;
                uTrackAtBottom = false;
                break;
            }
            case PLACE1: {
                //Raise the u-track
                drive.glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISED);
                if(drive.glyph.currentY.equals(GlyphPlacementSystem.Position.RAISED)) {
                    stage = GlyphPlacementSystem.Stage.PAUSE1;
                }
                break;
            }
            case PAUSE1: {
                //Move to target X location
                drive.glyph.moveXAxis(targetX);
                if(drive.glyph.xTargetReached()) {
                    stage = GlyphPlacementSystem.Stage.PLACE2;
                }
                break;
            }
            case PLACE2:{
                //Move to target Y location
                drive.glyph.setTargetPosition(targetY);
                if(drive.glyph.currentY.equals(targetY)) {
                    stage = GlyphPlacementSystem.Stage.RETURN1;
                }
                break;
            }
            case RETURN1: {
                //Open the hand; raise the u-track
                drive.openHand();
                drive.glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISED);
                if(drive.glyph.currentY.equals(GlyphPlacementSystem.Position.RAISED)) {
                    stage = GlyphPlacementSystem.Stage.PAUSE2;
                }
                break;
            }
            case PAUSE2: {
                //Move back to center x location (so the hand fits back in the robot)
                drive.glyph.moveXAxis(GlyphPlacementSystem.HorizPos.CENTER);
                if(drive.glyph.xTargetReached()) {
                    stage = GlyphPlacementSystem.Stage.RETURN2;
                }
                break;
            }
            case RETURN2: {
                //Move back to the bottom and get ready to do it again
                drive.glyph.setHomeTarget();
                stage = GlyphPlacementSystem.Stage.HOME;
                uTrackAtBottom = true;
                break;
            }
        }
    }

    private void glyphLocate() {
        if (gamepad2.dpad_up && !bUp) { targetY = GlyphPlacementSystem.Position.values()[drive.glyph.up() + 2]; }
        bUp = gamepad2.dpad_up;
        if (gamepad2.dpad_down && !bDown) { targetY = GlyphPlacementSystem.Position.values()[drive.glyph.down() + 2]; }
        bDown = gamepad2.dpad_down;
        //TODO: add horizontal targeting code once that exists
        if (gamepad2.dpad_left && !bLeft) { drive.glyph.left(); }
        bLeft = gamepad2.dpad_left;
        if (gamepad2.dpad_right && !bRight) { drive.glyph.right(); }
        bRight = gamepad2.dpad_right;
    }

    private void intakes() {
        double bRightTrigger = drive.deadZone(gamepad2.right_trigger);
        if (bRightTrigger > 0) {
            drive.intakeRight(bRightTrigger);
        }
        //Right bumper of the b controller runs the right intake backwards
        else if (gamepad2.right_bumper) {
            drive.intakeRight(-1);
        }
        else {
            drive.intakeRight(0);
        }

        //Left trigger of the b controller runs the left intake forward
        double bLeftTrigger = drive.deadZone(gamepad2.left_trigger);
        if (bLeftTrigger > 0) {
            drive.intakeLeft(bLeftTrigger);
        }
        //Left bumper of the b controller runs the left intake backwards
        else if (gamepad2.left_bumper) {
            drive.intakeLeft(-1);
        }
        else {
            drive.intakeLeft(0);
        }
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.addData("Glyph", drive.glyph.getTargetPositionAsString());
        telemetry.addData("encoder currentY pos", drive.verticalDriveCurrPos());
        telemetry.addData("hand is open", drive.isHandOpen());
        telemetry.addData("targetY", targetY.toString());
        telemetry.addData("Current pos", drive.glyph.currentY.toString());
        telemetry.addData("encoder targetY pos", drive.verticalDriveTargetPos());
        telemetry.addData("stage", stage);
        telemetry.addData("gamepad2.a", gamepad2.a);
        if (Drive.useGyro) {
            telemetry.addData("gyro", drive.gyro.updateHeading());
        }
        telemetry.update();
    }
}