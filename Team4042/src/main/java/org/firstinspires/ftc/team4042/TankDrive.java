package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Gautham on 9/26/2017.
 */

public class TankDrive extends Drive{

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param tel telemetry so Drive can send data to the phone
     */
    public TankDrive(HardwareMap hardwareMap, Telemetry tel) {
        //Initialize motors and gyro
        super(hardwareMap, tel);
    }

    public TankDrive(HardwareMap hardwareMap, Telemetry tel, boolean verbose, boolean useGyro) {
        super(hardwareMap, tel, verbose, useGyro);
    }

    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[2];
        double l = gamepad1.left_stick_y;
        double r = gamepad1.right_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1


        //Deadzone for joysticks
        l = super.deadZone(l);
        r = super.deadZone(r);


        if (verbose) {
            telemetry.addData("l", l);
            telemetry.addData("r", r);
            ;
        }

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] = l;
        speedWheel[1] = r;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel, speedFactor);
    }
}
