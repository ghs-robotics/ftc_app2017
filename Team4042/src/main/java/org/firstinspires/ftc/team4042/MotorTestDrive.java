package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "DylanTest", group = "Iterative Opmode")
public class MotorTestDrive extends Drive{

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param tel telemetry so Drive can send data to the phone
     */
    public MotorTestDrive(HardwareMap hardwareMap, Telemetry tel) {
        super (hardwareMap, tel);
    }

    /**
     * uses joystick inputs to set motor speeds for mechanim drive
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

        //Sets relative wheel speeds for mechanim drive based on controller inputs
        speedWheel[0] =  x;
        speedWheel[1] =  y;
        speedWheel[2] =  gamepad1.right_stick_y;
        speedWheel[3] =  r;

        super.setMotorPower(speedWheel);
    }
}
