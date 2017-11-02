package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Hazel on 9/22/2017.
 */

public class HalfMecanumDrive extends Drive{

    public HalfMecanumDrive() {
        super();
    }

    public HalfMecanumDrive(boolean verbose) {
        super(verbose);
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {

        super.setEncoders(useEncoders);

        double[] speedWheel = new double[4];
        double xMec = gamepad1.left_stick_x;
        double yMec = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double rMec = gamepad1.right_stick_x;

        double yTank = -gamepad2.left_stick_y;
        double rTank = gamepad2.left_stick_x;

        //Deadzone for joysticks
        xMec = super.deadZone(xMec);
        yMec = super.deadZone(yMec);
        rMec = super.deadZone(rMec);

        yTank = super.deadZone(yTank);
        rTank = super.deadZone(rTank);

        if (verbose) {
            telemetry.addData("xMec", xMec);
            telemetry.addData("yMec", yMec);
            telemetry.addData("rMec", rMec);

            telemetry.addData("yTank", yTank);
            telemetry.addData("rTank", rTank);
        }

        //Sets relative wheel speeds for the two mecanum wheels based on controller inputs
        speedWheel[0] = xMec + yMec + rMec;
        speedWheel[1] = -xMec + yMec - rMec;

        //These are the back two wheels, which are tank drive
        speedWheel[2] = yTank - rTank;
        speedWheel[3] = yTank + rTank;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel, speedFactor);
    }
}
