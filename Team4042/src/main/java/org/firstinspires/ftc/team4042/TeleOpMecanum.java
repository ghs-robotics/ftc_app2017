package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceImpl;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    //True if the back wheels are mecanum, false if they're tank
    private final boolean useBackMecanum = true;

    //Use gyro - true/false
    private final static boolean useGyro = true;

    //Reverses power input to back left motor
    public static final boolean team12788 = false;

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
        drive = new MecanumDrive(hardwareMap, telemetry, true, useGyro);
        telemetry.update();
    }
    
    @Override
    public void loop() {

        //rangeData = sensor.getRange();
        //telemetry.addData("range", rangeData.get(2));

        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, Drive.FULL_SPEED);
        if (useGyro) {
            drive.useGyro();
        }
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