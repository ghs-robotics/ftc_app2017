package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aA = false;

    private double adjustedSpeed;

    private boolean aLeftBumper = false;
    private boolean aRightBumper = false;

    private boolean bUp;
    private boolean bDown;
    private boolean bLeft;
    private boolean bRight;
    private boolean bB;

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive(true);

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
        drive.glyph = new GlyphPlacementSystem(hardwareMap, drive);
        //drive.glyph = new GlyphPlacementSystem(hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;
    }

    @Override
    public void start() {
        //Moves the servo to the up position
        drive.jewelUp();
    }
    
    @Override
    public void loop() {

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

        if(gamepad2.a) {
            drive.glyph.setTargetPosition();
        }
        else {
            drive.glyph.setHomeTarget();
        }


        //Glyph locate
        if (gamepad2.dpad_up && !bUp) { drive.glyph.up(); drive.glyph.setTargetPosition(); }
        bUp = gamepad2.dpad_up;
        if (gamepad2.dpad_down && !bDown) { drive.glyph.down(); drive.glyph.setTargetPosition(); }
        bDown = gamepad2.dpad_down;
        if (gamepad2.dpad_left && !bLeft) { drive.glyph.left(); drive.glyph.setTargetPosition(); }
        bLeft = gamepad2.dpad_left;
        if (gamepad2.dpad_right && !bRight) { drive.glyph.right(); drive.glyph.setTargetPosition(); }
        bRight = gamepad2.dpad_right;

        //Places glyph
        //if (gamepad2.a && !bA) {
        //    drive.glyph.runToPosition();
        //}
        //bA = gamepad2.a;
        //Adjust jewel arm
        drive.jewelAdjust(-gamepad2.right_stick_y);

        //Right trigger of the b controller runs the right intake forward
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

        //Left stick's y drives the u track
        //drive.verticalDrive(drive.deadZone(-gamepad2.left_stick_y));

        if (gamepad2.b && !bB) {
            if(drive.isHandOpen()) { drive.closeHand(); }
            else { drive.openHand(); }
        }

        drive.glyph.runToPosition();
        telemetryUpdate();
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.addData("Glyph", drive.glyph.getTargetPositionAsString());
        telemetry.addData("encoder", drive.verticalDriveCurrPos());
        telemetry.addData("hand is open", drive.isHandOpen());
        if (Drive.useGyro) {
            telemetry.addData("gyro", drive.gyro.updateHeading());
        }
        telemetry.update();
    }
}