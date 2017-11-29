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

    private DcMotor lift;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;
    private DcMotor relic;

    private boolean overide;

    private int liftPos;

    public Servo grabLeft;
    public Servo grabRight;

    private boolean invert;

    @Override
    public void init() {
        drive.initialize(telemetry, hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;

        invert = false;
        overide = false;
    }

    @Override
    public void loop() {
        lift = hardwareMap.dcMotor.get("lift");
        relic = hardwareMap.dcMotor.get("relic");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        if (gamepad2.dpad_up) {
            liftPos = 6000;
        } else if (gamepad2.dpad_down) {
            liftPos = 0;
        } else if (gamepad2.dpad_left || gamepad2.dpad_right) {
            liftPos = 3000;
        }

        if (0 < Math.abs(lift.getCurrentPosition() - liftPos) - 100) {
            if (liftPos < lift.getCurrentPosition()) {
                lift.setPower(-.5);
            } else if (liftPos > lift.getCurrentPosition()) {
                lift.setPower(1);
            }
        }
        else {
            lift .setPower(0);
        }

        while (gamepad2.start) {
            relic.setPower(1);
        }
        while (gamepad2.back) {
            relic.setPower(-1);
        }

        if (drive.deadZone(gamepad2.right_trigger) > 0) {
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
        } else if (drive.deadZone(gamepad2.left_trigger) > 0) {
            intakeLeft.setPower(-1);
            intakeRight.setPower(1);
        } else {
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
        }
        if (gamepad2.b) {
            grabLeft.setPosition(-1);
            grabRight.setPosition(1);
        }
        if (gamepad2.y) {
            grabRight.setPosition(.57);
            grabLeft.setPosition(.3);
        }
        if (gamepad2.x) {
            grabRight.setPosition(-1);
            grabLeft.setPosition(.8);
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
        if (gamepad2.a || overide) {
            overide = true;
            if (!drive.driveWithEncoders(Direction.Forward, .5, .2 * Autonomous.tile)) {

            } else {
                grabLeft.setPosition(-1);
                grabRight.setPosition(1);
                overide = false;
            }
        } else {
            drive.drive(false, gamepad1, adjustedSpeed * MecanumDrive.FULL_SPEED, invert);
        }
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.update();
    }
}