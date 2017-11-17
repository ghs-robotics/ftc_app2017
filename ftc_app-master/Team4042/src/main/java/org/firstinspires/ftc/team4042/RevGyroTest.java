package org.firstinspires.ftc.team4042;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous ;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;

/**
 * Gets all the data from a gyro and prints it to telemetry
 */
@Autonomous(name="Rev_Gyro_test", group="K9bot")
public class RevGyroTest extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();

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