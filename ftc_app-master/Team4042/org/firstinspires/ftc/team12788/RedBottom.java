package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by Gautham on 12/16/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "RedBottom", group = "Linear Opmode")
public class RedBottom extends LinearOpMode {
    private MecanumDrive drive = new MecanumDrive();
    private VuMarkIdentifier mark = new VuMarkIdentifier();

    private DcMotor lift;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;
    public Servo jewel;

    ColorSensor color = new ColorSensor("color");
    AnalogSensor infrared = new AnalogSensor("ir");
    AnalogSensor whisker = new AnalogSensor("whisker");

    private RelicRecoveryVuMark vuMark;
    private ElapsedTime timer;
    private boolean a;

    boolean isRed = false;
    boolean isTop = false;


    //@Override
    //public void runOpMode() {
    //drive.initialize(telemetry, hardwareMap);
    //liftLeft = hardwareMap.dcMotor.get("liftLeft");
    //liftRight = hardwareMap.dcMotor.get("liftRight");
    //intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
    //intakeRight = hardwareMap.dcMotor.get("intakeRight");
    //drive.runWithoutEncoders();
    //grabLeft = hardwareMap.servo.get("grabLeft");
    //grabRight = hardwareMap.servo.get("grabRight");
    //mark.initialize(telemetry, hardwareMap);
    //waitForStart();


    public void driveLength(double length, boolean isRed) {
        infrared.getVoltageAvg();
        double diff = length - infrared.getVoltageAvg();
        diff = diff * -.25;
        if (isRed) {
            drive.driveXYR(.7, diff, 1, 0);
        } else {
            drive.driveXYR(.7, diff, -1, 0);
        }
    }

    public void reset() {
        drive.resetEncoders();
        sleep(500);
        drive.runWithEncoders();
    }

    public void JewelUp() {
        jewel.setPosition(0);
    }

    public void move(Direction direction, double speed, double targetTicks) {
        while (!drive.driveWithEncoders(direction, speed, targetTicks) && opModeIsActive()) {
        }
        reset();
    }

    public void rotate(Direction.Rotation rotation, double speed, double targetTicks) {
        while (!drive.rotateWithEncoders(rotation, speed, targetTicks) && opModeIsActive()) {
        }
        reset();
    }


    public void dropoff() {
        move(Direction.Backward, Autonomous.speedy, 8.5 * Autonomous.tile / 24);
        grabRight.setPosition(.9);
        grabLeft.setPosition(0);
        sleep(500);
        move(Direction.Forward, Autonomous.speedy, 8.5 * Autonomous.tile / 24);
    }

    public void jewelKnock(boolean isRed) {
        telemetry.addData("lol", "1");
        jewel.setPosition(.86);
        sleep(1000);
        if (isRed) {
            if (color.SenseRed()) {
                move(new Direction(-.25, -1), Autonomous.speedy - .5, 4 * Autonomous.tile / 24);
                sleep(200);
                jewel.setPosition(.1);
                move(Direction.Forward, Autonomous.speedy - .5, 7 * Autonomous.tile / 24);
            } else {
                move(Direction.Forward, Autonomous.speedy - .4, 7 * Autonomous.tile / 24);
                jewel.setPosition(.1);
            }
        } else {
            if (color.SenseBlue()) {
                move(Direction.Backward, Autonomous.speedy - .4, 2 * Autonomous.tile / 8);
                jewel.setPosition(.1);
            } else {
                move(new Direction(-.25, 1), Autonomous.speedy - .5, 6 * Autonomous.tile / 24);
                sleep(200);
                jewel.setPosition(.1);
                move(Direction.Backward, Autonomous.speedy - .5, 5 * Autonomous.tile / 24);
            }

        }

        sleep(500);
        telemetry.addData("lol2", "2");
    }
    public void JewelWhisker(boolean isRed, boolean isTop) {
        drive.initialize(telemetry, hardwareMap);
        mark.initialize(telemetry,hardwareMap);
        lift = hardwareMap.dcMotor.get("lift");
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        drive.runWithoutEncoders();
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        grabRight.setPosition(.4);
        grabLeft.setPosition(.4);
        sleep(500);
        lift.setTargetPosition(1300);
        lift.setPower(1);
        reset();
        sleep(500);
        vuMark = mark.getMark();
        if (isRed && !isTop) {
            jewelKnock(isRed);
            timer.reset();
            sleep(499);
            move(Direction.Forward, Autonomous.speedy - .2, 12 * Autonomous.tile / 24);
            jewel.setPosition(.7);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.2, 1, 0, 0);
            }
            drive.stopMotors();
            reset();
            move(Direction.Left, Autonomous.speedy - .2, 6 * Autonomous.tile / 24);
            timer.reset();
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.2, 0, 1, 0);
            }
            drive.stopMotors();
            reset();
            jewel.setPosition(.1);
            sleep(500);
            rotate(Direction.Rotation.Counterclockwise, Autonomous.speedy - .2, 1 * Autonomous.turn);
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                move(Direction.Right, Autonomous.speedy, 11 * Autonomous.tile / 24);
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                move(Direction.Right, Autonomous.speedy, 5 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            lift.setTargetPosition(0);
            lift.setPower(-.5);
            reset();
            sleep(1500);


        }
    }
    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        drive.initialize(telemetry, hardwareMap);
        lift = hardwareMap.dcMotor.get("lift");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        reset();

        jewel = hardwareMap.servo.get("jewel");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        color.initialize(hardwareMap);
        whisker.initialize(hardwareMap);

        //mark.initialize(telemetry, hardwareMap);

        boolean isRed = true;
        boolean isTop = false;
        /*while (opModeIsActive() && !isStarted()) {
            if (gamepad1.x) {
                isRed = false;
            }
            if (gamepad1.b) {
                isRed = true;
            }
            if (gamepad1.y) {
                isTop = true;
            }
            if (gamepad1.a) {
                isTop = false;
            }
        }*/
        waitForStart();
        JewelWhisker(isRed, isTop);

    }
}

