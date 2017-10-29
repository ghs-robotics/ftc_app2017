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
        hardwareMap.dcMotor.get("cluck");
        hardwareMap.dcMotor.get("chirp");
        hardwareMap.dcMotor.get("baa");
        hardwareMap.servo.get("bork");

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



    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.update();
    }
}