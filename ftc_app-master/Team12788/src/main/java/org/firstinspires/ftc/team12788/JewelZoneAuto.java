package org.firstinspires.ftc.team12788;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;
import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Jewl Auto", group = "Linear Opmode")
public class JewelZoneAuto extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    //private VuMarkIdentifier mark = new VuMarkIdentifier();

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
        move(Direction.Backward, Autonomous.speedy, 4 * Autonomous.tile / 24);
        grabRight.setPosition(-1);
        grabLeft.setPosition(.8);
        move(Direction.Forward, Autonomous.speedy, 4 * Autonomous.tile / 24);
    }

    public void jewelKnock(boolean isRed) {
        telemetry.addData("lol", "1");
        jewel.setPosition(.95);
        sleep(1000);
        if (isRed) {
            if (color.SenseRed()) {
                move(Direction.Forward, Autonomous.speedy - .4, Autonomous.tile / 8);
                jewel.setPosition(.63);
            } else {
                move(Direction.Backward, Autonomous.speedy - .5, 145);
                sleep(200);
                jewel.setPosition(.5);
            }
        } else {
            if (color.SenseBlue()) {
                move(Direction.Forward, Autonomous.speedy - .4, Autonomous.tile / 8);
                sleep(200);
                jewel.setPosition(.5);
            } else {
                move(Direction.Backward, Autonomous.speedy - .4, 2 * Autonomous.tile / 8);
                jewel.setPosition(.63);
            }

        }

        sleep(500);
        telemetry.addData("lol2", "2");
    }


    public void JewelWhisker(boolean isRed, boolean isTop) {
        drive.initialize(telemetry, hardwareMap);
        lift = hardwareMap.dcMotor.get("lift");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        drive.runWithoutEncoders();

        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");


        grabRight.setPosition(.7);
        grabLeft.setPosition(.3);
        reset();
        //vuMark = mark.getMark();
        vuMark = RelicRecoveryVuMark.CENTER;
        jewelKnock(isRed);
        if (isRed && !isTop) {

            timer.reset();
            jewel.setPosition(.55);
            sleep(499);
            move(Direction.Forward, Autonomous.speedy - .2, 12 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 1, 0, 0);
            }
            move(Direction.Left, Autonomous.speedy - .2, 1 * Autonomous.tile / 24);
            drive.stopMotors();
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 0, 1, 0);
            }
            drive.stopMotors();
            reset();
            jewel.setPosition(.1);
            sleep(500);
            rotate(Direction.Rotation.Counterclockwise, Autonomous.speedy - .2, Autonomous.turn);


            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                move(Direction.Right, Autonomous.speedy, 3 * Autonomous.tile / 6);
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                move(Direction.Right, Autonomous.speedy, 5 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }


        }
        if (!isRed && !isTop) {

            timer.reset();

            sleep(499);
            move(Direction.Backward, Autonomous.speedy - .2, 12 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 1, 0, 0);
            }
            drive.stopMotors();
            move(Direction.Left, Autonomous.speedy - .2, 1 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 0, -1, 0);
            }
            drive.stopMotors();
            reset();
            jewel.setPosition(.1);
            sleep(500);
            rotate(Direction.Rotation.Counterclockwise, Autonomous.speedy - .2, Autonomous.turn);
            jewel.setPosition(1);
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                move(Direction.Left, Autonomous.speedy, 3 * Autonomous.tile / 6);
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                move(Direction.Left, Autonomous.speedy, 5 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
        }

        if (isRed && isTop) {
            timer.reset();
            jewel.setPosition(.55);
            sleep(499);
            move(Direction.Backward, Autonomous.speedy - .2, 12 * Autonomous.tile / 24);

            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 0, 1, 0);
            }
            drive.stopMotors();
            move(Direction.Backward, Autonomous.speedy - .2, 1 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, -1, 0, 0);
            }
            drive.stopMotors();
            reset();
            jewel.setPosition(.1);
            sleep(500);
            rotate(Direction.Rotation.Clockwise, Autonomous.speedy - .2, Autonomous.turn);
            jewel.setPosition(1);
            rotate(Direction.Rotation.Clockwise, Autonomous.speedy - .2, Autonomous.turn);
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Right, Autonomous.speedy, 5 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                move(Direction.Right, Autonomous.speedy, 3 * Autonomous.tile / 6);
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
        }
        if (!isRed && isTop) {
            timer.reset();
            jewel.setPosition(.55);
            sleep(499);
            move(Direction.Backward, Autonomous.speedy - .2, 12 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 0, 1, 0);
            }
            drive.stopMotors();
            move(Direction.Backward, Autonomous.speedy - .2, 1 * Autonomous.tile / 24);
            while (whisker.getVoltage() < 2 && timer.seconds() < 5 && opModeIsActive()) {
                drive.driveXYR(.3, 1, 0, 0);
            }
            drive.stopMotors();
            reset();
            jewel.setPosition(.1);
            sleep(500);
            rotate(Direction.Rotation.Counterclockwise, Autonomous.speedy - .2, Autonomous.turn);
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Left, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                move(Direction.Left, Autonomous.speedy, 3 * Autonomous.tile / 6);
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                move(Direction.Left, Autonomous.speedy, 5 * Autonomous.tile / 6);
                dropoff();
                move(Direction.Right, Autonomous.speedy, 1 * Autonomous.tile / 3);
            }
        }
    }


    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        drive.initialize(telemetry, hardwareMap);
        lift = hardwareMap.dcMotor.get("lift");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        drive.runWithEncoders();

        jewel = hardwareMap.servo.get("jewel");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        color.initialize(hardwareMap);
        whisker.initialize(hardwareMap);

        //mark.initialize(telemetry, hardwareMap);

        boolean isRed = false;
        boolean isTop = false;
        while (opModeIsActive() && !isStarted()) {
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
        }
        waitForStart();
        JewelWhisker(isRed, isTop);

    }
}

