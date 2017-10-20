package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    private double adjustedSpeed;

    //Declare OpMode members.
    private MecanumDrive drive;

    //private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    @Override
    public void init() {
        /*try {
            sensor = hardwareMap.get(UltrasonicI2cRangeSensor.class, "MB1242-0");
        }catch (Exception x){
            telemetry.addLine("it broke");
        }
        sensor.startRanging();
        */
        drive = new MecanumDrive(hardwareMap, telemetry, true);
        telemetry.update();

        adjustedSpeed = drive.FULL_SPEED;
    }
    
    @Override
    public void loop() {

        //rangeData = sensor.getRange();
        //telemetry.addData("range", rangeData.get(2));

        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, adjustedSpeed);
        if (Drive.useGyro) {
            drive.useGyro();
        }
        telemetry.update();

        if (gamepad1.left_bumper && adjustedSpeed >= drive.FULL_SPEED)
        {
            adjustedSpeed *= 0.5;
        }

        if (gamepad1.right_bumper && adjustedSpeed <= drive.FULL_SPEED)
        {
            adjustedSpeed *= 2;
        }
    }

    /* CODE FROM HERE DOWN IS AN ATTEMPT TO IMPLEMENT DYLAN'S DRIVE ALGORITHM
    MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive.drive(false, gamepad1, 1);
    }*/

}