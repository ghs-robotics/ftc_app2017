package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Hazel on 11/6/2017.
 */

public class ExtendoDrive extends Drive {

    @Override
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {
        super.setEncoders(useEncoders);

        double left = -gamepad1.left_stick_y;
        double right = -gamepad1.right_stick_y;

        driveLR(speedFactor, left, right);
    }

    private void driveLR(double speedFactor, double l, double r) {

        double[] speedWheel = new double[4];

        //Deadzone for joysticks
        l = super.deadZone(l);
        r = super.deadZone(r);

        if (verbose) {
            telemetry.addData("left", l);
            telemetry.addData("right", r);
        }

        speedWheel[0] = l;
        speedWheel[1] = r;
        //We don't move the back motors, for obvious reasons
        speedWheel[2] = 0;
        speedWheel[3] = 0;

        super.setMotorPower(speedWheel, speedFactor);
    }

}
