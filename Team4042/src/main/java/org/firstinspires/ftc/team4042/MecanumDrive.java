package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumDrive {
    public static final double DEADZONE_SIZE = .01;

    /***instance variables***/
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    Telemetry telemetry;

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param tel telemetry so Drive can send data to the phone
     */
    public MecanumDrive(HardwareMap hardwareMap, Telemetry tel) {
        //Initialize motors and gyro
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
     * uses joystick inputs to set motor speeds for mechanim drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, double speedFactor) {

        if (useEncoders) {
            this.runWithEncoders();
        } else {
            this.runWithoutEncoders();
        }

        double[] speedWheel = new double[4];
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double r = gamepad1.right_stick_x;

        //Deadzone for joysticks
        if(Math.abs(x - DEADZONE_SIZE) <= 0){x = 0;}
        if(Math.abs(y - DEADZONE_SIZE) <= 0){y = 0;}
        if(Math.abs(r - DEADZONE_SIZE) <= 0){r = 0;}

        //Sets relative wheel speeds for mechanim drive based on controller inputs
        speedWheel[0] = x + y + r;
        speedWheel[1] = -x + y - r;
        speedWheel[2] = x + y - r;
        speedWheel[3] = -x + y + r;

        //Scales wheel speeds to fit motors
        for(int i = 0; i < 4; i++) {
            speedWheel[i] *= speedFactor;
            if(speedWheel[i] > 1){speedWheel[i] = 1;}
            if(speedWheel[i] < -1){speedWheel[i] = -1;}
        }
        //sets the wheel powers to the appropriate ratios
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
