package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Bren on 1/22/2018.
 */

@Autonomous(name = "TestRobot", group = "Drive")
public class TestRobot extends LinearOpMode{

    private Drive drive = new MecanumDrive();

    private ArrayList<String> tests = new ArrayList<>();
    private boolean lastA = false;

    @Override
    public void runOpMode() {
        tests.add("Drive Motors");
        tests.add("Drive Encoders");
        tests.add("External and Internal Intakes");
        tests.add("Jewel Probe");
        tests.add("Vertical U-track");
        tests.add("U-track Encoder");
        tests.add("UHT");
        tests.add("U-track Hand");
        tests.add("Reset U-track");
        tests.add("Brakes Down; Catches Up");
        tests.add("Open Winch");
        tests.add("Sensors");
        tests.add("There are no more tests to run.");

        drive.initialize(telemetry, hardwareMap);
        drive.runWithEncoders();

        drive.initializeGyro(telemetry, hardwareMap);

        telemetry.update();
        waitForStart();
        post();
        //Drive motors
        drive.runWithEncoders();
        drive.motorLeftBack.setPower(.5);
        drive.motorLeftFront.setPower(.5);
        drive.motorRightBack.setPower(.5);
        drive.motorRightFront.setPower(.5);
        post();

        //Drive encoders
        drive.motorLeftBack.setPower(0);
        drive.motorLeftFront.setPower(0);
        drive.motorRightBack.setPower(0);
        drive.motorRightFront.setPower(0);
        telemetry.addData("Left Back Encoder", drive.motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front Encoder", drive.motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back Encoder", drive.motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front Encoder", drive.motorRightFront.getCurrentPosition());
        post();

        //External and internal intakes
        drive.intakeLeft(1);
        drive.internalIntakeLeft(1);
        drive.intakeRight(1);
        drive.internalIntakeRight(1);
        post();

        //Jewel Probe
        drive.intakeRight(0);
        drive.intakeLeft(0);
        drive.internalIntakeRight(0);
        drive.internalIntakeLeft(0);
        drive.jewelOut();
        post();

        //Vertical u-track
        drive.jewelUp();
        drive.setVerticalDrive(1);
        post();

        //U-track Encoder
        drive.setVerticalDrive(0);
        telemetry.addData("U-track Encoder", drive.verticalDriveCurrPos());
        post();

        //UHT
        drive.setHorizontalDrive(.82);
        post();

        //U-track Hand
        drive.openHand();
        post();

        //Reset U-track
        drive.closeHand();
        drive.setHorizontalDrive(-.82);
        while(!drive.getCenterState() && opModeIsActive());
        drive.setHorizontalDrive(0);
        drive.setVerticalDrive(-1);
        while (!drive.getBottomState() && opModeIsActive());
        drive.setVerticalDrive(0);
        post();

        //Brakes Down; Catches Up
        drive.lowerBrakes();
        drive.unlockCatches();
        post();

        //Open Winch
        drive.lockCatches();
        drive.raiseBrakes();
        drive.openWinch();
        post();

        //Sensors
        drive.stowWinch();
        drive.readSensorsSetUp();
        for (AnalogSensor ir : drive.shortIr) { telemetry.addData(ir.getName(), ir.getCmAvg()); }
        for (AnalogSensor longIr : drive.longIr) { telemetry.addData(longIr.getName(), longIr.getCmAvg()); }
        for (AnalogSensor sonar : drive.sonar) { telemetry.addData(sonar.getName(), sonar.getCmAvg()); }
        post();
    }

    public void post() {
        String nextTest = tests.remove(0);
        telemetry.addData("Next Test", nextTest);
        telemetry.addData("Control", "Press a to continue");
        telemetry.update();
        lastA = gamepad1.a;
        while(opModeIsActive()){
            if (gamepad1.a && !lastA) {
                break;
            }
            lastA = gamepad1.a;
        }
        lastA = gamepad1.a;
        telemetry.addData("Testing", nextTest);
        telemetry.update();
    }
}
