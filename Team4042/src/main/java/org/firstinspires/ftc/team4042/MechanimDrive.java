package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MechanimDrive {
    //Initializes a factor for the speed of movement to a position
    public static double BASE_SPEED = .3;
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
     * @param telemetry telemetry so Drive can send data to the phone
     */
    public MechanimDrive(HardwareMap hardwareMap, Telemetry telemetry) {
        //Initialize motors and gyro
        motorLeftBack = hardwareMap.dcMotor.get("back left");
        motorLeftFront = hardwareMap.dcMotor.get("front left");
        motorRightBack = hardwareMap.dcMotor.get("back right");
        motorRightFront = hardwareMap.dcMotor.get("front right");

        this.telemetry = telemetry;

    }

    /**
     * sets all the motors to run using the PID algorithms and encoders
     */
    public void runWithEncoders(){
        motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * sets all the motors to run NOT using the PID algorithms and encoders
     */
    public void runWithoutEncoders(){
        motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * resents the encoder counts of all motors
     */
    public void resetEncoders() {
        motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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
        double y = gamepad1.left_stick_y;
        double r = gamepad1.right_stick_x;

        //Deadzone for joysticks
        if(x < DEADZONE_SIZE){x = 0;}
        if(y < DEADZONE_SIZE){y = 0;}
        if(r < DEADZONE_SIZE){r = 0;}

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
        motorRightFront.setPower(speedWheel[0]);
        motorLeftFront.setPower(speedWheel[1]);
        motorLeftBack.setPower(speedWheel[2]);
        motorRightBack.setPower(speedWheel[3]);
    }
}
