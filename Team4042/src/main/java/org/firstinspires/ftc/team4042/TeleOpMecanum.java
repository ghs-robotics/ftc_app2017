package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    private double adjustedSpeed;

    private boolean aLeftBumper = false;
    private boolean aRightBumper = false;

    private boolean aUp = false;
    private boolean aDown = false;
    private boolean aLeft = false;
    private boolean aRight = false;

    private boolean aA = false;

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, true);

    //private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    private GlyphPlacementSystem glyph;

    @Override
    public void init() {
        /*try {
            sensor = hardwareMap.get(I2cRangeSensor.class, "MB1242-0");
        }catch (Exception x){
            telemetry.addLine("it broke");
        }
        sensor.startRanging();
        */
        drive.initialize(hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;
        glyph = new GlyphPlacementSystem(telemetry);
    }
    
    @Override
    public void loop() {

        //rangeData = sensor.getRange();
        //telemetry.addData("range", rangeData.get(2));

        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, adjustedSpeed * MecanumDrive.FULL_SPEED);

        if (Drive.useGyro) {
            drive.useGyro();
        }
        telemetry.update();

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

        //Glyph placement
        if (gamepad1.dpad_up && !aUp) { glyph.up(); }
        aUp = gamepad1.dpad_up;
        if (gamepad1.dpad_down && !aDown) { glyph.down(); }
        aDown = gamepad1.dpad_down;
        if (gamepad1.dpad_left && !aLeft) { glyph.left(); }
        aLeft = gamepad1.dpad_left;
        if (gamepad1.dpad_right && !aRight) { glyph.right(); }
        aRight = gamepad1.dpad_right;

        if (gamepad1.a) { glyph.place(); }
        aA = gamepad1.a;

        double rightTrigger = drive.deadZone(gamepad1.right_trigger);
        if (rightTrigger > 0) {
            //TODO: RUN INTAKE FORWARD
        }
        double leftTrigger = drive.deadZone(gamepad1.left_trigger);
        if (leftTrigger > 0) {
            //TODO: RUN INTAKE BACKWARD
        }

        telemetryUpdate();
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.addData("Glyph", glyph.getPositionAsString());
    }

    /* CODE FROM HERE DOWN IS AN ATTEMPT TO IMPLEMENT DYLAN'S DRIVE ALGORITHM
    MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive.drive(false, gamepad1, 1);
    }*/

}