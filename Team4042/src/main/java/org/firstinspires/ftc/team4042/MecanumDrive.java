package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumDrive extends Drive {

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param tel telemetry so Drive can send data to the phone
     */
    public MecanumDrive(HardwareMap hardwareMap, Telemetry tel) {
        //Initialize motors and gyro
        super(hardwareMap, tel);
    }

    public MecanumDrive(HardwareMap hardwareMap, Telemetry tel, boolean verbose) {
        super(hardwareMap, tel, verbose);
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[4];
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double r = gamepad1.right_stick_x;

        //Deadzone for joysticks
        x = super.deadZone(x);
        y = super.deadZone(y);
        r = super.deadZone(r);

        if (verbose) {
            telemetry.addData("x", x);
            telemetry.addData("y", y);
            telemetry.addData("r", r);
        }

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] = x + y + r;
        speedWheel[1] = -x + y - r;
        speedWheel[2] = x + y - r;
        speedWheel[3] = -x + y + r;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel, speedFactor);
    }
}
