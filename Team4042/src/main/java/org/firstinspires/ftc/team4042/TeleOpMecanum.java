package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    private double adjustedSpeed;

    private boolean aLeftBumper = false;
    private boolean aRightBumper = false;

    private boolean bUp = false;
    private boolean bDown = false;
    private boolean bLeft = false;
    private boolean bRight = false;

    private boolean bA = false;

    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive(true);

    //private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    private GlyphPlacementSystem glyph;

    /**
    GAMEPAD 1:
      Joystick 1 - movement
      Joystick 2 - rotation
      Bumpers - speed modes
      Triggers -
      Dpad -
      A - toggle verbose
      B -
      X -
      Y -

    GAMEPAD 2:
      Joystick 1 -
      Joystick 2 -
      Bumpers - run intakes backwards
      Triggers - run intakes forwards
      Dpad - placer
      A - place glyph
      B -
      X -
      Y -
     */

    @Override
    public void init() {
        /*try {
            sensor = hardwareMap.get(I2cRangeSensor.class, "MB1242-0");
        }catch (Exception x){
            telemetry.addLine("it broke");
        }
        sensor.startRanging();
        */
        drive.initialize(telemetry, hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;
        glyph = new GlyphPlacementSystem(hardwareMap);

        intakeLeft = hardwareMap.dcMotor.get("intake left");
        intakeRight = hardwareMap.dcMotor.get("intake right");
    }
    
    @Override
    public void loop() {

        //rangeData = sensor.getRange();
        //telemetry.addData("range", rangeData.get(2));

        //1 A - toggle verbose
        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;

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

        //Glyph locate
        if (gamepad2.dpad_up && !bUp) { glyph.up(); }
        bUp = gamepad2.dpad_up;
        if (gamepad2.dpad_down && !bDown) { glyph.down(); }
        bDown = gamepad2.dpad_down;
        if (gamepad2.dpad_left && !bLeft) { glyph.left(); }
        bLeft = gamepad2.dpad_left;
        if (gamepad2.dpad_right && !bRight) { glyph.right(); }
        bRight = gamepad2.dpad_right;

        //Places glyph
        if (gamepad2.a) { glyph.place(); }
        bA = gamepad2.a;

        //The left intake is mounted "backwards"
        intakeLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        //Right trigger of the b controller runs the right intake forward
        double bRightTrigger = drive.deadZone(gamepad2.right_trigger);
        if (bRightTrigger > 0) {
            intakeRight.setPower(bRightTrigger);
        }
        //Right bumper of the b controller runs the right intake backwards
        else if (gamepad2.right_bumper) {
            intakeRight.setPower(-1);
        }
        else {
            intakeRight.setPower(0);
        }

        //Left trigger of the b controller runs the left intake forward
        double bLeftTrigger = drive.deadZone(gamepad2.left_trigger);
        if (bLeftTrigger > 0) {
            intakeLeft.setPower(bLeftTrigger);
        }
        //Left bumper of the b controller runs the left intake backwards
        else if (gamepad2.left_bumper) {
            intakeLeft.setPower(-1);
        }
        else {
            intakeLeft.setPower(0);
        }

        telemetryUpdate();
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.addData("Glyph", glyph.getPositionAsString());
        telemetry.update();
    }
}