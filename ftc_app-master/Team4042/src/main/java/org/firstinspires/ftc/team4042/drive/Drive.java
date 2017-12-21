package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Hazel on 9/22/2017.
 */

public abstract class Drive {

    //Require drive() in subclasses
    public abstract void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor);
    public abstract void driveXYR(double speedFactor, double x, double y, double r, boolean useGyro);

    //Initializes a factor for the speed of movement to a position when driving with encoders
    public static final double BASE_SPEED = .3;
    //The deadzone size for the joystick inputs
    public static final double DEADZONE_SIZE = .01;
    //The largest speed factor possible
    public static final double FULL_SPEED = 1;
    //The power to put to the motors to stop them
    public static final double STOP_SPEED = 0;

    public static final double MAGIC_NUMBER = .8;

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
    public static boolean crawl = false;
    public static boolean tank = false;

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

    private CRServo horizontalDrive;
    private DigitalChannel center;
    private DigitalChannel bottom;

    private Servo grabbyBoi;
    private boolean handIsOpen = false;

    public GlyphPlacementSystem glyph;
    public GlyphPlacementSystem.Stage stage;
    public ElapsedTime handDropTimer = new ElapsedTime();
    public boolean uTrackAtBottom = true;
    public GlyphPlacementSystem.Position targetY;
    public GlyphPlacementSystem.HorizPos targetX;

    public int[] deriv = new int[3];
    public int derivCycle = 0;


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

    public Drive() {
        for(int i = 0; i < shortIr.length; i++){
            shortIr[i] = new AnalogSensor("ir" + i, false);
        }

        for(int i = 0; i < longIr.length; i++){
            longIr[i] = new AnalogSensor("longir" + i, true);
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

        for (AnalogSensor aShortIr : shortIr) {
            aShortIr.initialize(hardwareMap);
        }

        for (AnalogSensor aLongIr : shortIr) {
            aLongIr.initialize(hardwareMap);
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

        horizontalDrive = hardwareMap.crservo.get("horizontal");

        center = hardwareMap.digitalChannel.get("center");
        center.setState(false);
        center.setMode(DigitalChannel.Mode.INPUT);

        bottom = hardwareMap.digitalChannel.get("bottom");
        bottom.setState(false);
        bottom.setMode(DigitalChannel.Mode.INPUT);

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

    private double[] lastEncoders = new double[4];
    private double lastMilli = 0;
    public double[] encoderRates = new double[4];

    private double lastGyro = 0;
    public double gyroRate = 0;

    private double[] lastShortIr = new double[3];
    private double[] lastLongIr = new double[2];
    public double[] shortIrRates = new double[3];
    public double[] longIrRates = new double[2];

    private double lastUTrack = 0;
    public double uTrackRate = 0;
    public void updateRates() {
        //System time
        double currMilli = System.currentTimeMillis();

        //Encoder rates
        double[] currEncoders = new double[4];
        currEncoders[0] = motorLeftFront.getCurrentPosition();
        currEncoders[1] = motorRightFront.getCurrentPosition();
        currEncoders[2] = motorRightBack.getCurrentPosition();
        currEncoders[3] = motorLeftBack.getCurrentPosition();
        double currGyro = gyro.updateHeading();

        for (int i = 0; i < currEncoders.length; i++) {
            encoderRates[i] = (currEncoders[i] - lastEncoders[i]) / (currMilli - lastMilli);
        }
        lastEncoders = currEncoders;

        //IR rates
        double[] currShortIr = new double[3];
        for (int i = 0; i < currShortIr.length; i++) {
            AnalogSensor sIr = shortIr[i];
            sIr.addReading();
            currShortIr[i] = sIr.getCmAvg();
            shortIrRates[i] = (currShortIr[i] - lastShortIr[i]) / (currMilli - lastMilli);
        }

        double[] currLongIr = new double[2];
        for (int i = 0; i < currLongIr.length; i++) {
            AnalogSensor lIr = longIr[i];
            lIr.addReading();
            currLongIr[i] = lIr.getCmAvg();
            longIrRates[i] = (currLongIr[i] - lastLongIr[i]) / (currMilli - lastMilli);
        }

        //Gyro rates
        gyroRate = (currGyro - lastGyro) / (currMilli - lastMilli);

        //Update last to current
        lastGyro = currGyro;
        lastShortIr = currShortIr;
        lastLongIr = currLongIr;

        lastMilli = currMilli;
    }

    public void uTrackUpdate() {
        //Vertical drive
        double currUTrack = verticalDriveCurrPos();
        double currMilli = System.currentTimeMillis();
        uTrackRate = (currUTrack - lastUTrack) / (currMilli - lastMilli);

        lastUTrack = currUTrack;
        lastMilli = currMilli;
    }

    private void glyphLocate() {
        targetY = GlyphPlacementSystem.Position.values()[glyph.uiTargetY + 3];
        targetX = GlyphPlacementSystem.HorizPos.values()[glyph.uiTargetX];
    }

    public boolean uTrack() {
        telemetry.addData("stage", stage);
        switch (stage) {
            case HOME: {
                //Close the hand
                closeHand();
                jewelOut();
                glyphLocate();
                handDropTimer.reset();

                glyph.currentY = GlyphPlacementSystem.Position.HOME;
                glyph.currentX = GlyphPlacementSystem.HorizPos.CENTER;

                stage = GlyphPlacementSystem.Stage.GRAB;
                uTrackAtBottom = false;
                return false;
            }
            case GRAB: {
                if (handDropTimer.seconds() >= 1) {
                    stage = GlyphPlacementSystem.Stage.PLACE1;
                }
                return false;
            }
            case PLACE1: {
                //Raise the u-track
                glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISED);
                if(glyph.currentY.equals(GlyphPlacementSystem.Position.RAISED)) {
                    stage = GlyphPlacementSystem.Stage.PAUSE1;
                    glyph.setXPower(targetX);
                }
                return false;
            }
            case PAUSE1: {
                //Move to target X location
                if(glyph.xTargetReached(targetX)) {
                    stage = GlyphPlacementSystem.Stage.PLACE2;
                }
                return false;
            }
            case PLACE2:{
                //Move to target Y location
                glyph.setTargetPosition(targetY);
                if(glyph.currentY.equals(targetY)) {
                    stage = GlyphPlacementSystem.Stage.RETURN1;
                }
                return false;
            }
            case RETURN1: {
                //Open the hand; raise the u-track
                openHand();
                handDropTimer.reset();

                stage = GlyphPlacementSystem.Stage.RELEASE;
                return false;
            }
            case RELEASE: {
                if (handDropTimer.seconds() >= 1) {
                    glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISEDBACK);
                    if (glyph.currentY.equals(GlyphPlacementSystem.Position.RAISEDBACK)) {
                        stage = GlyphPlacementSystem.Stage.PAUSE2;
                    }
                }
                return false;
            }
            case PAUSE2: {
                //Move back to center x location (so the hand fits back in the robot)
                glyph.setXPower(GlyphPlacementSystem.HorizPos.CENTER);
                if(glyph.xTargetReached(GlyphPlacementSystem.HorizPos.CENTER)) {
                    stage = GlyphPlacementSystem.Stage.RETURN2;
                    if (targetX.equals(GlyphPlacementSystem.HorizPos.LEFT)) {
                        glyph.adjustBack(-1);
                    }
                }
                return false;
            }
            case RETURN2: {
                //Move back to the bottom and get ready to do it again
                glyph.setHomeTarget();
                setVerticalDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                setVerticalDrive(-1);
                stage = GlyphPlacementSystem.Stage.RESET;
                return false;
            }
            case RESET: {
                if (getBottomState()) {
                    resetUTrack();
                    setVerticalDrive(0);
                    setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                uTrackAtBottom = true;
                return true;
            }
        }
        return false;
    }

    public void resetUTrack() {
        stage = GlyphPlacementSystem.Stage.HOME;
        jewelUp();
        setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        uTrackAtBottom = true;
    }

    /**
     * uses the gyro, first reading from the gyro then setting rotation to
     * auto correct if the robot gets off
     */
    public double useGyro(double targetGyro) {
        double heading = gyro.updateHeading() - targetGyro; //hopefully still 0
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

    public boolean getBottomState() {
        return bottom.getState();
    }

    public void openHand() {
        handIsOpen = true;
        grabbyBoi.setPosition(.57);
    }

    public void closeHand() {
        handIsOpen = false;
        grabbyBoi.setPosition(1);
    }

    public void setHorizontalDrive(double power) {
        horizontalDrive.setPower(power);
    }

    public double getHorizontalDrive() {
        return horizontalDrive.getPower();
    }

    public boolean isHandOpen() {
        return handIsOpen;
    }

    public double getVerticalDrive() { return verticalDrive.getPower(); }

    public void setVerticalDrive(double power) {
        verticalDrive.setPower(power);
    }

    public void setVerticalDriveMode(DcMotor.RunMode mode) {
        verticalDrive.setMode(mode);
    }

    public DcMotor.RunMode getVerticalDriveMode() { return verticalDrive.getMode(); }

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
        intakeLeft.setPower(power * .75);
    }

    public void intakeRight(double power) {
        intakeRight.setPower(power * .75);
    }

    public void internalIntakeLeft(double power) {
        inLServo.setPower(power * .82);
        if (verbose) {
            telemetry.addData("cr left servo", inLServo.getPower());
        }
    }

    public void internalIntakeRight(double power) {
        inRServo.setPower(power * .82);
        if (verbose) {
            telemetry.addData("cr right servo", inRServo.getPower());
        }
    }

    public void jewelDown() {
        jewelServo.setPosition(.1);
        while (jewelServo.getPosition() != .1) {  }
    }

    public void jewelUp() {
        jewelServo.setPosition(.6);
        while (jewelServo.getPosition() != .6) {  }
    }

    public void jewelIn() {
        jewelServo.setPosition(.83);
        while (jewelServo.getPosition() != .83) {  }
    }

    /*
    Moves the jewel out of the way
     */
    public void jewelOut() {
        jewelServo.setPosition(.43);
        while (jewelServo.getPosition() != .43) {  }
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
        leftBrake.setPosition(.9);
        rightBrake.setPosition(0);
    }

    public void raiseBrakes() {
        leftBrake.setPosition(.1);
        rightBrake.setPosition(.68);
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

    public void freezeBack() {
        runBackToPosition();
        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setTargetPosition(0);
            }
            if (motorRightBack != null) {
                motorRightBack.setTargetPosition(0);
            }
        }
    }

    public void runBackToPosition() {
        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if (motorRightBack != null) {
                motorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }
    }

    public void runBackWithEncoders() {
        if (useMotors) {
            if (motorLeftBack != null) {
                motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if (motorRightBack != null) {
                motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        speedFactor = speedFactor > 1 ? 1 : speedFactor;
        speedFactor = speedFactor < -1 ? -1 : speedFactor;

        //Since max is an absolute value function, this also accounts for a data set like [3, 1, 0, -5], since max will be 5
        for(int i = 0; i < 4; i++) {
            speedWheel[i] *= speedFactor;
            if (max > 1) {
                speedWheel[i] /= max;
            }
        }

        if (useMotors) {
            if (motorLeftFront != null) {
                motorLeftFront.setPower(deadZone(speedWheel[0]));
                telemetry.addData("left front", motorLeftFront.getPower());
            }
            if (motorRightFront != null) {
                motorRightFront.setPower(deadZone(-speedWheel[1]));
                telemetry.addData("right front", motorRightFront.getPower());
                motorRightFront.getPower();
            } //The right motors are mounted "upside down", which is why we have to inverse this
            if (motorRightBack != null) {
                motorRightBack.setPower(deadZone(-speedWheel[2]));
                telemetry.addData("right back", motorRightBack.getPower());
            }
            if (motorLeftBack != null) {
                motorLeftBack.setPower(deadZone(speedWheel[3]));
                telemetry.addData("left back", motorLeftBack.getPower());
            }
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
