package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;
import org.firstinspires.ftc.team4042.sensor.DigitalSensor;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    public static final boolean THE_FAST_ONES_ARE_THE_FRONT_ONES = true;
    public static final double LOW_SPEED_MOTOR_THINGS = 7;
    public static final double LOW_SPEED_WHEEL_THINGS = 5;
    public static final double HIGH_SPEED_MOTOR_THINGS = 3;
    public static final double HIGH_SPEED_WHEEL_THINGS = 2;
    public static final double HIGH_SPEED_SLOWER_DOWNER_NUMBER =
            (LOW_SPEED_MOTOR_THINGS/LOW_SPEED_WHEEL_THINGS) /
            (HIGH_SPEED_MOTOR_THINGS/HIGH_SPEED_WHEEL_THINGS);

    //How much the robot is rotated when we start (as in, the wheels are in a diamond, not a square)
    //Used for not-field-oriented drive
    public static final int OFFSET = 0;

    //Use gyro - true/false
    public static boolean useGyro = false;

    //Whether the robot is attached to itself or not
    public static boolean isExtendo = false;

    //Set to false to just get outputs as telemetry
    public static boolean useMotors = true;

    //adjusted power for power levels

    /***instance variables**/
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    private Servo jewelServo;

    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    private CRServo inLServo;
    private CRServo inRServo;

    private Servo leftBrake;
    private Servo rightBrake;

    private Servo leftCatch;
    private Servo rightCatch;

    private DcMotor verticalDrive;

    private CRServo horizontalU;
    private DigitalSensor center = new DigitalSensor("center");

    private Servo grabbyBoi;
    private boolean handIsOpen = false;

    public GlyphPlacementSystem glyph;



    Telemetry telemetry;

    public RevGyro gyro = new RevGyro();

    public AnalogSensor[] shortIr = new AnalogSensor[3];
    public AnalogSensor[] longIr = new AnalogSensor[2];

    public boolean verbose;

    Telemetry.Log log;

    public static String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    //Require drive() in subclasses
    public abstract void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor);

    public Drive() {
        for(int i = 0; i < shortIr.length; i++){
            shortIr[i] = new AnalogSensor("ir" + i, false);
        }

        for(int i = 0; i < longIr.length; i++){
            shortIr[i] = new AnalogSensor("longir" + i, true);
        }

        verbose = false;
    }

    public Drive(boolean verbose) {
        this();
        this.verbose = verbose;
    }

    public void initializeGyro(Telemetry telemetry, HardwareMap hardwareMap) {
        gyro.initialize(telemetry, hardwareMap);
    }

    public void initialize(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        this.log = telemetry.log();

        glyph = new GlyphPlacementSystem(hardwareMap, this);

        if (useGyro) {
            initializeGyro(telemetry, hardwareMap);
        }

        log.add("useGyro: " + useGyro);

        for (int i = 0; i < shortIr.length; i++) {
            shortIr[i].initialize(hardwareMap);
        }

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

        jewelServo = hardwareMap.servo.get("jewel");
        jewelIn();

        grabbyBoi = hardwareMap.servo.get("hand");

        leftBrake = hardwareMap.servo.get("left brake");
        rightBrake = hardwareMap.servo.get("right brake");

        leftCatch = hardwareMap.servo.get("left catch");
        rightCatch = hardwareMap.servo.get("right catch");

        horizontalU = hardwareMap.crservo.get("horizontal");
        center.initialize(hardwareMap);

        intakeLeft = hardwareMap.dcMotor.get("intake left");
        intakeRight = hardwareMap.dcMotor.get("intake right");
        //The left intake is mounted "backwards"
        intakeLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        inLServo = hardwareMap.crservo.get("intake left servo");
        inRServo = hardwareMap.crservo.get("intake right servo");
        //The left intake servo is mounted "backwards"
        inLServo.setDirection(DcMotorSimple.Direction.REVERSE);

        verticalDrive = hardwareMap.dcMotor.get("vertical drive");
        verticalDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //verticalDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * uses the gyro, first reading from the gyro then setting rotation to
     * auto correct if the robot gets off
     */
    public double useGyro() {
        double heading = gyro.updateHeading(); //hopefully still 0
        //If you're moving forwards and you drift, this should correct it.
        //Accounts for if you go from -180 degrees to 180 degrees
        // which is only a difference of one degree,
        // but the bot thinks that's 359 degree difference
        if (heading < -180) {
            heading += 180;
        } else if (heading > 180) {
            heading -= 180;
        }

        // Scales -180 to 180 ==> -8 to 8
        heading = heading / 22.5;

        return heading;
    }

    public static void waitSec(double seconds) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while (timer.seconds() < seconds) { }
    }

    public void toggleHand() {
        if (isHandOpen()) {
            closeHand();
        } else {
            openHand();
        }
        //The functions toggle the hand variable so we don't need to
    }

    public boolean getCenterState() {
        return center.getState();
    }

    public void openHand() {
        handIsOpen = true;
        grabbyBoi.setPosition(.57);
    }

    public void closeHand() {
        handIsOpen = false;
        grabbyBoi.setPosition(1);
    }

    public void setHorizontalU(double power) {
        horizontalU.setPower(power);
    }

    public double getHorizontalU() {
        return horizontalU.getPower();
    }

    public boolean isHandOpen() {
        return handIsOpen;
    }

    public void setVerticalDrive(double power) {
        verticalDrive.setPower(power);
    }

    public void setVerticalDriveMode(DcMotor.RunMode mode) {
        verticalDrive.setMode(mode);
    }

    public void setVerticalDrivePos(int position) {
        verticalDrive.setTargetPosition(position);
    }

    public int verticalDriveCurrPos() {
        return verticalDrive.getCurrentPosition();
    }

    public int verticalDriveTargetPos() {
        return verticalDrive.getTargetPosition();
    }

    public void verticalDriveDir(DcMotorSimple.Direction dir) { verticalDrive.setDirection(dir);}

    public void intakeLeft(double power) {
        intakeLeft.setPower(power);
        inLServo.setPower(power);
        if (verbose) {
            telemetry.addData("cr left servo", inLServo.getPower());
        }
    }

    public void intakeRight(double power) {
        intakeRight.setPower(power);
        inRServo.setPower(power);
        if (verbose) {
            telemetry.addData("cr right servo", inRServo.getPower());
        }
    }

    public void jewelDown() {
        jewelServo.setPosition(0);
        while (jewelServo.getPosition() != 0) {  }
    }

    public void jewelUp() {
        jewelServo.setPosition(.6);
        while (jewelServo.getPosition() != .6) {  }
    }

    public void jewelIn() {
        jewelServo.setPosition(.8);
        while (jewelServo.getPosition() != .8) {  }
    }

    public void lockCatches() {
        rightCatch.setPosition(.9);
        leftCatch.setPosition(.17);
    }

    public void unlockCatches() {
        rightCatch.setPosition(.46);
        leftCatch.setPosition(.6);
    }

    public void lowerBrakes() {
        leftBrake.setPosition(.8);
        rightBrake.setPosition(0);
    }

    public void raiseBrakes() {
        leftBrake.setPosition(.12);
        rightBrake.setPosition(.65);
    }

    public void jewelAdjust(double adjustAmt) {
        double currPos = jewelServo.getPosition();

        jewelServo.setPosition(Range.clip(currPos + adjustAmt, 0, 1));
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
     * Runs the motors really aggressively to push the robot together
     */
    public void pushRobotTogether() {
        double[] speedWheel = new double[4];

        //Front wheels backwards
        speedWheel[0] = 1;
        speedWheel[1] = 1;
        //Back wheels forwards
        speedWheel[2] = -1;
        speedWheel[3] = -1;

        setMotorPower(speedWheel, FULL_SPEED);
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
        double max = max(speedWheel[0], speedWheel[1], speedWheel[2], speedWheel[3]);
        //Since max is an absolute value function, this also accounts for a data set like [3, 1, 0, -5], since max will be 5
        for(int i = 0; i < 4; i++) {
            speedWheel[i] *= speedFactor;
            if (max > 1) {
                speedWheel[i] /= max;
            }
        }

        if (useMotors) {
            //which ones to scale down? fast ones. if THE_FAST_ONES_ARE_THE_FRONT_ONES, the front.
            if(THE_FAST_ONES_ARE_THE_FRONT_ONES) {
                if (motorLeftFront != null) {
                    motorLeftFront.setPower(deadZone(speedWheel[0]) * HIGH_SPEED_SLOWER_DOWNER_NUMBER);
                }
                if (motorRightFront != null) {
                    motorRightFront.setPower(deadZone(-speedWheel[1]) * HIGH_SPEED_SLOWER_DOWNER_NUMBER);
                } //The right motors are mounted "upside down", which is why we have to inverse this
                if (motorRightBack != null) {
                    motorRightBack.setPower(deadZone(-speedWheel[2]));
                }
                if (motorLeftBack != null) {
                    motorLeftBack.setPower(deadZone(speedWheel[3]));
                }
            } else {
                if (motorLeftFront != null) {
                    motorLeftFront.setPower(deadZone(speedWheel[0]));
                }
                if (motorRightFront != null) {
                    motorRightFront.setPower(deadZone(-speedWheel[1]));
                } //The right motors are mounted "upside down", which is why we have to inverse this
                if (motorRightBack != null) {
                    motorRightBack.setPower(deadZone(-speedWheel[2]) * HIGH_SPEED_SLOWER_DOWNER_NUMBER);
                }
                if (motorLeftBack != null) {
                    motorLeftBack.setPower(deadZone(speedWheel[3]) * HIGH_SPEED_SLOWER_DOWNER_NUMBER);
                }
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
        
        return Math.max(Math.max(Math.max(a, b), c), d);
    }

    public double min(double a, double b, double c, double d) {
        return Math.min(a, (Math.min(b, (Math.min(c, d)))));
    }
}
