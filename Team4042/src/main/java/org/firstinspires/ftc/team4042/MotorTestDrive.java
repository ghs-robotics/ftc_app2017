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
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, double speedFactor) {

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[4];
        double xLeft = gamepad1.left_stick_x;
        double yLeft = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double xRight = gamepad1.right_stick_x;
        double yRight = -gamepad1.right_stick_y;

        //Deadzone for joysticks
        xLeft = super.deadZone(xLeft);
        yLeft = super.deadZone(yLeft);
        xRight = super.deadZone(xRight);

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] =  xLeft;
        speedWheel[1] =  yLeft;
        speedWheel[2] =  xRight;
        speedWheel[3] =  yRight;

        super.setMotorPower(speedWheel);
    }
}
