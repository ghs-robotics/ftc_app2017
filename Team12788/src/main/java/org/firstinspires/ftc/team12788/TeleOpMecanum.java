package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive();

    //private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    private double adjustedSpeed;

    private DcMotor liftLeft;
    private DcMotor liftRight;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;

    private boolean invert;

    @Override
    public void init() {
        drive.initialize(telemetry, hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;

        invert = false;
    }
    
    @Override
    public void loop() {

        drive.drive(false, gamepad1,adjustedSpeed * MecanumDrive.FULL_SPEED, invert);
        liftLeft = hardwareMap.dcMotor.get("liftLeft");
        liftRight = hardwareMap.dcMotor.get("liftRight");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");

        if (gamepad2.dpad_up) {
            liftLeft.setPower(1);
            liftRight.setPower(-1);
        }
        else if (gamepad2.dpad_down) {
            liftLeft.setPower(-.5);
            liftRight.setPower(.5);
        }
        else {
            liftLeft.setPower(0);
            liftRight.setPower(0);
        }
        if (drive.deadZone(gamepad2.right_trigger) > 0) {
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
        }
        else if (drive.deadZone(gamepad2.left_trigger) > 0) {
            intakeLeft.setPower(-1);
            intakeRight.setPower(1);
        }
        else{
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
        }
        if (gamepad2.a) {
            grabLeft.setPosition(-1);
            grabRight.setPosition(1);
        }
        if (gamepad2.y) {
            grabRight.setPosition(-1);
            grabLeft.setPosition(1);
        }
        if (gamepad1.a) {
            adjustedSpeed = .5;
        }
        if (gamepad1.b) {
            adjustedSpeed = 1;
        }
        if (gamepad1.x) {
            adjustedSpeed = .25;
        }
        if (gamepad1.right_bumper) {
            invert = false;
        }
        if (gamepad1.left_bumper) {
            invert = true;
        }

    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.update();
    }
}