package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name = "Motor Test", group = "Iterative Opmode")
public class MotorTestDrive extends Drive{

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param tel telemetry so Drive can send data to the phone
     */
    public MotorTestDrive() {
        super();
    }

    public MotorTestDrive(boolean verbose) {
        super(verbose);
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {

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

        if (verbose) {
            telemetry.addData("xLeft", xLeft);
            telemetry.addData("yLeft", yLeft);
            telemetry.addData("xRight", xRight);
            telemetry.addData("yRight", yRight);
        }

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] =  xLeft;
        speedWheel[1] =  yLeft;
        speedWheel[2] =  xRight;
        speedWheel[3] =  yRight;

        super.setMotorPower(speedWheel, speedFactor);
    }
}
