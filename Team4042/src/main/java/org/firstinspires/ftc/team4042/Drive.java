package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Hazel on 9/22/2017.
 */

public class Drive {
    public static final double DEADZONE_SIZE = .01;

    /***instance variables***/
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    Telemetry telemetry;

    public Drive(HardwareMap hardwareMap, Telemetry tel) {
        this.telemetry = tel;

        try {
            motorLeftFront = hardwareMap.dcMotor.get("front left");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Front Left", "Could not find.");
        }

        try {
            motorRightFront = hardwareMap.dcMotor.get("front right");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Front Right", "Could not find.");
        }

        try {
            motorRightBack = hardwareMap.dcMotor.get("back right");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Back Right", "Could not find.");
        }

        try {
            motorLeftBack = hardwareMap.dcMotor.get("back left");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Back Left", "Could not find.");
        }
    }

    /**
     * sets all the motors to run using the PID algorithms and encoders
     */
    public void runWithEncoders(){
        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
    }

    /**
     * sets all the motors to run NOT using the PID algorithms and encoders
     */
    public void runWithoutEncoders(){
        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
    }

    /**
     * resents the encoder counts of all motors
     */
    public void resetEncoders() {
        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
    }

    /**
     * Sets the motors to run with or without motors
     * @param useEncoders
     */
    public void setEncoders(boolean useEncoders) {
        if (useEncoders) {
            this.runWithEncoders();
        } else {
            this.runWithoutEncoders();
        }
    }

    public double deadZone(double x) {
        if(Math.abs(x - DEADZONE_SIZE) <= 0) {return 0;}
        else { return x; }
    }

    public void setMotorPower(double[] speedWheel) {
        if (motorLeftFront != null) { motorLeftFront.setPower(speedWheel[0]); }
        if (motorRightFront != null) { motorRightFront.setPower(-speedWheel[1]); } //The right motors are mounted "upside down", which is why we have to inverse this
        if (motorRightBack != null) { motorRightBack.setPower(-speedWheel[2]); }
        if (motorLeftBack != null) { motorLeftBack.setPower(speedWheel[3]); }

        telemetry.addData("Left Front", speedWheel[0]);
        telemetry.addData("Right Front", -speedWheel[1]);
        telemetry.addData("Right Back", -speedWheel[2]);
        telemetry.addData("Left Back", speedWheel[3]);
    }
}
