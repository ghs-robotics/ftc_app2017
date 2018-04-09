package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous ;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team4042.drive.MecanumDrive;

/**
 * Gets all the data from a gyro and prints it to telemetry
 */
@Autonomous(name="Rev_Gyro_test", group="testops")
@Disabled
public class RevGyroTest extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        drive.initializeGyro(telemetry, hardwareMap);

        waitForStart();

// Set up our telemetry dashboard
        while (opModeIsActive()) {
            drive.gyro.updateAngles();
            telemetry.addData("heading", drive.gyro.getHeading());
            telemetry.addData("pitch", drive.gyro.getPitch());
            telemetry.addData("roll", drive.gyro.getRoll());
            telemetry.update();
        }
    }

    /*private MecanumDrive drive = new MecanumDrive();

    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        drive.initializeGyro(telemetry, hardwareMap);

        //waitForStart();

        while (opModeIsActive()) {
            //telemetry.addData("gyro", drive.gyro);
            //telemetry.addData("useGyro", Drive.useGyro);
            drive.gyro.updateAngles();
            telemetry.addData("heading", drive.gyro.getHeading());
            telemetry.addData("pitch", drive.gyro.getPitch());
            telemetry.addData("roll", drive.gyro.getRoll());
            telemetry.update();

            //if (gamepad1.a) {
            //    drive.setUseGyro(!Drive.useGyro);
            //}
        }
    }*/
}