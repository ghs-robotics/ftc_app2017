package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Bren on 1/22/2018.
 */

@Autonomous(name = "Sensor: Color", group = "Sensor")
@Disabled
public class TestRobot extends LinearOpMode{

    private Drive drive = new MecanumDrive();

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        drive.runWithEncoders();

        drive.initializeGyro(telemetry, hardwareMap);

        telemetry.update();
        waitForStart();
        post();
        //test motors
        telemetry.addData("Testing: ", "motors");
        drive.runWithEncoders();
        drive.motorLeftBack.setPower(.5);
        drive.motorLeftFront.setPower(.5);
        drive.motorRightBack.setPower(.5);
        drive.motorRightFront.setPower(.5);
        post();
        drive.motorLeftBack.setPower(0);
        drive.motorLeftFront.setPower(0);
        drive.motorRightBack.setPower(0);
        drive.motorRightFront.setPower(0);
        telemetry.addData("Left Back", drive.motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front", drive.motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back", drive.motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front", drive.motorRightFront.getCurrentPosition());
        post();
        //test intake
        telemetry.addData("Testing: ", "intake");
        drive.intakeLeft(1);
        drive.intakeRight(1);
        post();
        drive.intakeRight(0);
        drive.intakeLeft(0);
        //test verticle utrack
        telemetry.addData("Testing: ", "Vertical U-track");
        telemetry.update();
        drive.setVerticalDrive(1);
        sleep(500);
        drive.setVerticalDrive(0);
        telemetry.addData("Utrack position: ", drive.verticalDriveCurrPos());
        post();
        drive.setVerticalDrive(-1);
        sleep(470);
        drive.setVerticalDrive(0);
        post();
        //test horizontal utrack
        telemetry.addData("Testing: ", "horizontal U-track");
        telemetry.addData("Control", "press a to skip");
        telemetry.update();
        drive.setHorizontalDrive(.82);
        sleep(500);
        drive.setHorizontalDrive(0);
        sleep(200);
        drive.setHorizontalDrive(-.82);
        while(!drive.getCenterState() && ! gamepad1.a);
        drive.setHorizontalDrive(0);
        sleep(200);
        //test internal intake
        telemetry.addData("Testing: ", "internal intake");
        drive.internalIntakeLeft(.82);
        drive.internalIntakeRight(.82);
        post();
        drive.internalIntakeLeft(0);
        drive.internalIntakeRight(0);
        //test hand
        telemetry.addData("Testing: ", "hand");
        telemetry.addData("Extra Controls", "press b to toggle");

    }

    public void testDrive() {
        
    }

    public void post() {
        telemetry.addData("Control", "Press a");
        telemetry.update();
        while(!gamepad1.a);
        sleep(100);
    }
}
