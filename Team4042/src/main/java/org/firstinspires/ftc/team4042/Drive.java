package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Hazel on 9/22/2017.
 */

public abstract class Drive {
    //Initializes a factor for the speed of movement to a position when driving with encoders
    public static final double BASE_SPEED = .3;
    //The deadzone size for the joystick inputs
    public static final double DEADZONE_SIZE = .01;
    //The largest speed factor possible
    public static final double FULL_SPEED = 1;
    //The power to put to the motors to stop them
    public static final double STOP_SPEED = 0;

    //Use gyro - true/false
    public static boolean useGyro = true;
    //Reverses power input to back left motor
    public static final boolean team12788 = false;

    //Set to false to just get outputs as telemetry
    public static boolean useMotors = true;

    //adjusted power for power levels

    /***instance variables**/
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    Telemetry telemetry;

    RevGyro gyro;

    AnalogSensor ir;

    boolean verbose;

    //Require drive() in subclasses
    public abstract void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor);

    public Drive(HardwareMap hardwareMap, Telemetry tel) {
        this.telemetry = tel;

        if (useGyro) {
            gyro = new RevGyro(hardwareMap, tel);
        }
        telemetry.addData("useGyro", useGyro);

        ir = new AnalogSensor(hardwareMap);

        verbose = false;
    }

    public Drive(HardwareMap hardwareMap, Telemetry tel, boolean verbose) {
        this(hardwareMap, tel);
        this.verbose = verbose;
    }

    public void initialize(HardwareMap hardwareMap) {
        if (useGyro) {
            gyro = new RevGyro(hardwareMap, telemetry);
        }
        ir.initialize();

        try {
            motorLeftFront = hardwareMap.dcMotor.get("front left");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Front Left", "Could not find.");
            useMotors = false;
        }

        try {
            motorRightFront = hardwareMap.dcMotor.get("front right");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Front Right", "Could not find.");
            useMotors = false;
        }

        try {
            motorRightBack = hardwareMap.dcMotor.get("back right");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Back Right", "Could not find.");
            useMotors = false;
        }

        try {
            motorLeftBack = hardwareMap.dcMotor.get("back left");
        } catch (IllegalArgumentException ex) {
            telemetry.addData("Back Left", "Could not find.");
            useMotors = false;
        }
    }

    public void setUseGyro(boolean useGyro) {
        Drive.useGyro = useGyro;
    }

    /**
     * sets all the motors to run using the PID algorithms and encoders
     */
    public void runWithEncoders(){
        if (verbose || !useMotors) { telemetry.addData("Encoders", "true"); }

        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if (motorLeftFront != null) {
                motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if (motorRightBack != null) {
                motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if (motorRightFront != null) {
                motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }

    /**
     * sets all the motors to run NOT using the PID algorithms and encoders
     */
    public void runWithoutEncoders(){
        if (verbose || !useMotors) { telemetry.addData("Encoders", "false"); }

        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if (motorLeftFront != null) {
                motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if (motorRightBack != null) {
                motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if (motorRightFront != null) {
                motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
    }

    /**
     * resents the encoder counts of all motors
     */
    public void resetEncoders() {
        if (verbose || !useMotors) { telemetry.addData("Encoders", "reset"); }

        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            if (motorLeftFront != null) {
                motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            if (motorRightBack != null) {
                motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            if (motorRightFront != null) {
                motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
        }
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
     * Returns val, unless it's within DEAD_ZONE of 0, then returns 0
     * @param val A value to test
     * @return val, adjusted for the deadzone
     */
    public double deadZone(double val) {
        if(Math.abs(val - DEADZONE_SIZE) <= 0) {return 0;}
        else { return val; }
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
    public void setMotorPower(double[] speedWheel, double speedFactor) {
        //Scales wheel speeds to fit motors
        for(int i = 0; i < 4; i++) {
            speedWheel[i] *= speedFactor;
            if(speedWheel[i] > 1) { speedWheel[i] = 1; }
            if(speedWheel[i] < -1) { speedWheel[i] = -1; }
        }

        if (useMotors) {
            //Sets the power
            if (motorLeftFront != null) {
                motorLeftFront.setPower(speedWheel[0]);
            }
            if (motorRightFront != null) {
                motorRightFront.setPower(-speedWheel[1]);
            } //The right motors are mounted "upside down", which is why we have to inverse this
            if (motorRightBack != null) {
                motorRightBack.setPower(-speedWheel[2]);
            }
            if (motorLeftBack != null) {
                motorLeftBack.setPower(speedWheel[3]);
            }
        }

        if (verbose || !useMotors) {
            //Prints power
            telemetry.addData("Left Front", speedWheel[0]);
            telemetry.addData("Right Front", -speedWheel[1]);
            telemetry.addData("Right Back", -speedWheel[2]);
            telemetry.addData("Left Back", speedWheel[3]);
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void toggleVerbose() {
        verbose = !verbose;
    }

    /**
     * finds and returns the largest of four doubles
     *
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     * @return return the maximum of all decimals
     */
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
