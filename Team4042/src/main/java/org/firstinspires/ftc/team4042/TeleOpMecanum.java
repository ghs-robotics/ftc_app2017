package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceImpl;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    //True if the back wheels are mecanum, false if they're tank
    private final boolean useBackMecanum = true;

    //Declare OpMode members.
    private MecanumDrive drive;

    private UltrasonicI2cRangeSensor sensor;
    private ArrayList<Integer> rangeData;

    @Override
    public void init() {
        sensor = hardwareMap.get(UltrasonicI2cRangeSensor.class, "MB1242-0");
        sensor.startRanging();

        drive = new MecanumDrive(hardwareMap, telemetry, true);
    }
    
    @Override
    public void loop() {

        rangeData = sensor.getRange();
        telemetry.addData("range", rangeData.get(2));

        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, Drive.FULL_SPEED);
        drive.useGyro();
        telemetry.update();

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