package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OmniDrive extends Drive {

    RevGyro gyro;

    //How much the robot is rotated when we start (as in, the wheels are in a diamond, not a square)
    public static final int OFFSET = 180;

    double oldGyro = OFFSET;

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param tel telemetry so Drive can send data to the phone
     */
    public OmniDrive(HardwareMap hardwareMap, Telemetry tel) {
        //Initialize motors and gyro
        super(hardwareMap, tel);
        gyro = new RevGyro(hardwareMap, tel);
    }

    public OmniDrive(HardwareMap hardwareMap, Telemetry tel, boolean verbose) {
        super(hardwareMap, tel, verbose);
        gyro = new RevGyro(hardwareMap, tel);
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {

        boolean useGyro = true;

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[4];

        double xComp = -gamepad1.left_stick_x;
        double yComp = -gamepad1.left_stick_y;
        double rot = gamepad1.right_stick_x;

        double heading;
        if (!useGyro) {
            heading = OFFSET;
        } else {
            heading = gyro.updateHeading();
            telemetry.addData("heading", heading);
        }

        telemetry.addData("x", xComp);
        telemetry.addData("y", yComp);
        telemetry.addData("r", rot);

        for (int n = 0; n <= 3; n++) {
            //This \/ rotates the control input to make it work on each motor
            // and assigns the initial wheel power ratios
            speedWheel[n] = xComp * Math.cos(Math.toRadians(heading)) +
                    yComp * Math.sin(Math.toRadians(heading)) + .7 * rot;
            heading = (heading + 90) % 360;

        }

        //In order to handle the problem if the values in speedWheel[] are greater than 1,
        //this scales them so the ratio between the values stays the same, but makes sure they're
        //less than 1. Then it multiplies it by speed to incorporate the speed at which
        //you want the robot to go
        double scaler = Math.abs(max(speedWheel[0], speedWheel[1], speedWheel[2], speedWheel[3]));
        //if the scaler is 0, it will cause a divide by 0 error
        if (scaler != 0) {
            for (int n = 0; n < 4; n++) {
                speedWheel[n] *= (Math.sqrt(xComp * xComp + yComp * yComp) +
                        Math.abs(rot / speedFactor) / scaler);
            }
        }

        //sets the wheel powers to the appropriate ratios
        motorRightFront.setPower(speedWheel[0]);
        motorLeftFront.setPower(speedWheel[1]);
        motorLeftBack.setPower(speedWheel[2]);
        motorRightBack.setPower(speedWheel[3]);
    }

    public double max(double a, double b, double c, double d) {

        a = Math.abs(a);
        b = Math.abs(b);
        c = Math.abs(c);
        d = Math.abs(d);

        double max = a;
        double[] vals = {b, c, d};

        for (int i = 0; i < 3; i++) {
            if (vals[i] > max) {
                max = vals[i];
            }
        }
        return max;
    }
}
