package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Bren on 1/22/2018.
 */

@Autonomous(name = "Sensor: Color", group = "Sensor")
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
        telemetry.addData("Left Back", drive.motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front", drive.motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back", drive.motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front", drive.motorRightFront.getCurrentPosition());
        post();
        //test intake
        telemetry.addData("Testing: ", "intake");
        telemetry.addData("Control", "press a");
        drive.intakeLeft(1);
        drive.intakeRight(1);
        while(!gamepad1.a){

        }
    }

    public void post() {
        telemetry.addData("Control", "Press a");
        telemetry.update();
        while(!gamepad1.a);
        sleep(100);
    }
}
