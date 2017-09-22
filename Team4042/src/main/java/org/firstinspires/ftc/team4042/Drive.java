package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Hazel on 9/22/2017.
 */

public class Drive {
    public static final double DEADZONE_SIZE = .01;

    /***instance variables**/
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    Telemetry telemetry;

    boolean verbose;

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

        verbose = false;
    }

    public Drive(HardwareMap hardwareMap, Telemetry tel, boolean verbose) {
        this(hardwareMap, tel);
        this.verbose = verbose;
    }

    /**
     * sets all the motors to run using the PID algorithms and encoders
     */
    public void runWithEncoders(){
        if (verbose) { telemetry.addData("Encoders", "true"); }

        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER); }
    }

    /**
     * sets all the motors to run NOT using the PID algorithms and encoders
     */
    public void runWithoutEncoders(){
        if (verbose) { telemetry.addData("Encoders", "false"); }

        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); }
    }

    /**
     * resents the encoder counts of all motors
     */
    public void resetEncoders() {
        if (verbose) { telemetry.addData("Encoders", "reset"); }

        if (motorLeftBack != null) { motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorLeftFront != null) { motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorRightBack != null) { motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
        if (motorRightFront != null) { motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
    }

    /**
     * Sets the motors to run with or without motors
     * @param useEncoders Whether or not to use encoders for the motors
     */
    public void setEncoders(boolean useEncoders) {
        if (useEncoders) {
            this.runWithEncoders();
        } else {
            this.runWithoutEncoders();
        }
    }

    /**
     * Returns x, unless it's within DEAD_ZONE of 0, then returns 0
     * @param x A value to test
     * @return X, adjusted for the deadzone
     */
    public double deadZone(double x) {
        if(Math.abs(x - DEADZONE_SIZE) <= 0) {return 0;}
        else { return x; }
    }

    /**
     * Sets motor power to four wheels from an array of the values for each of the four wheels.
     * The wheels should be clockwise from the top left:
     * 0 - leftFront
     * 1 - rightFront
     * 2 - rightBack
     * 3 - leftBack
     * @param speedWheel An array of the power to set to each motor
     */
    public void setMotorPower(double[] speedWheel) {
        if (motorLeftFront != null) { motorLeftFront.setPower(speedWheel[0]); }
        if (motorRightFront != null) { motorRightFront.setPower(-speedWheel[1]); } //The right motors are mounted "upside down", which is why we have to inverse this
        if (motorRightBack != null) { motorRightBack.setPower(-speedWheel[2]); }
        if (motorLeftBack != null) { motorLeftBack.setPower(speedWheel[3]); }

        if (verbose) {
            telemetry.addData("Left Front", speedWheel[0]);
            telemetry.addData("Right Front", -speedWheel[1]);
            telemetry.addData("Right Back", -speedWheel[2]);
            telemetry.addData("Left Back", speedWheel[3]);
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
