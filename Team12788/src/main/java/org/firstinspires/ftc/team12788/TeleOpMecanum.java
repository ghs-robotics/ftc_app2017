package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive(true);

    //private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    private double adjustedSpeed;

    private DcMotor Drive1;
    private DcMotor Drive3;
    private DcMotor Drive2;
    private DcMotor Drive4;
    private DcMotor liftLeft;
    private DcMotor liftRight;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;

    @Override
    public void init() {
        drive.initialize(telemetry, hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;
    }
    
    @Override
    public void loop() {

        drive.drive(false, gamepad1, gamepad2, adjustedSpeed * MecanumDrive.FULL_SPEED);
        liftLeft = hardwareMap.dcMotor.get("moo");
        liftRight = hardwareMap.dcMotor.get("cluck");
        intakeRight = hardwareMap.dcMotor.get("chirp");
        intakeLeft = hardwareMap.dcMotor.get("baa");
        grabLeft = hardwareMap.servo.get("bork");
        grabRight = hardwareMap.servo.get("meow");
        Drive1 = hardwareMap.dcMotor.get("croak");
        Drive3 = hardwareMap.dcMotor.get("hiss");
        Drive2 = hardwareMap.dcMotor.get("rawr");
        Drive4 = hardwareMap.dcMotor.get("hoot");


        if (Drive.useGyro) {
            drive.useGyro();
        }
        if (gamepad2.dpad_up) {
            liftLeft.setPower(1);
            liftRight.setPower(-1);
        }
        if (gamepad2.dpad_down) {
            liftLeft.setPower(-1);
            liftRight.setPower(1);
        }
        if (drive.deadZone(gamepad2.right_trigger) > 0) {
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
        }
        if (drive.deadZone(gamepad2.left_trigger) > 0) {
            intakeLeft.setPower(-1);
            intakeRight.setPower(1);
        }
        if (gamepad2.a) {
            grabLeft.setPosition(-1);
            grabRight.setPosition(1);
        }
        if (gamepad2.y) {
            grabRight.setPosition(-1);
            grabLeft.setPosition(1);
        }
        if (((gamepad1.left_stick_x>0 || gamepad1.right_stick_y>0) || gamepad1.right_stick_x>1) && gamepad1.a) {
            Drive1.setPower();
        }



    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.update();
    }
}