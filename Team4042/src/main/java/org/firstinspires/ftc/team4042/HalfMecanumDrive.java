package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Hazel on 9/22/2017.
 */

public class HalfMecanumDrive extends Drive{

    public HalfMecanumDrive(HardwareMap hardwareMap, Telemetry tel) {
        super(hardwareMap, tel);
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, double speedFactor) {

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[4];
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double r = gamepad1.right_stick_x;

        //Deadzone for joysticks
        x = super.deadZone(x);
        y = super.deadZone(y);
        r = super.deadZone(r);

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] = 0;
        speedWheel[1] = 0;
        speedWheel[2] = 0;
        speedWheel[3] = 0;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel);
    }
}
