package org.firstinspires.ftc.team4042.drive;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team4042.autos.C;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

/**
 * Created by Hazel on 9/22/2017.
 */

public abstract class Drive {

    //Require drive() in subclasses
    public abstract void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor);
    public abstract void driveXYR(double speedFactor, double x, double y, double r, boolean useGyro);
    public abstract void driveLR(double speedFactor, double l, double r);
    public abstract void stopMotors();

    //Initializes a factor for the speed of movement to a position when driving with encoders
    public static final double BASE_SPEED = C.get().getDouble("base");
    //The deadzone size for the joystick inputs
    public static final double DEADZONE_SIZE = C.get().getDouble("ds");
    //The largest speed factor possible
    public static final double FULL_SPEED = C.get().getDouble("full");
    //The power to put to the motors to stop them
    public static final double STOP_SPEED = C.get().getDouble("stop");

    public static final double MAGIC_NUMBER = C.get().getDouble("magic");

    public static final double COLOR_THRESHOLD = C.get().getDouble("threshold");

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
    public DcMotor motorLeftFront;
    public DcMotor motorRightFront;
    public DcMotor motorLeftBack;
    public DcMotor motorRightBack;

    private Servo jewelServo;
    private Servo jewelHead;

    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    private CRServo inLServo;
    private CRServo inRServo;

    private Servo leftBrake;
    private Servo rightBrake;

    private Servo winch;

    private Servo leftCatch;
    private Servo rightCatch;

    private DcMotor verticalDrive;

    private CRServo horizontalDrive;
    private DigitalChannel center;
    private DigitalChannel bottom;
    private DigitalChannel collected;

    private Servo grabbyBoi;
    private boolean handIsOpen = false;

    public GlyphPlacementSystem glyph;
    public GlyphPlacementSystem.Stage stage;
    public ElapsedTime handDropTimer = new ElapsedTime();
    public boolean uTrackAtBottom = true;
    public GlyphPlacementSystem.Position targetY;
    public GlyphPlacementSystem.HorizPos targetX;

    public Cryptobox cryptobox;

    public int[] deriv = new int[3];
    public int derivCycle = 0;


    Telemetry telemetry;

    public RevGyro gyro = new RevGyro();

    public AnalogSensor[] shortIr = new AnalogSensor[3];
    public AnalogSensor[] longIr = new AnalogSensor[2];
    public AnalogSensor[] sonar = new AnalogSensor[2];
    public AnalogSensor[] lineFollow = new AnalogSensor[1];

    public boolean verbose;

    public DanceStage dStage;

    Telemetry.Log log;

    public boolean useSensors;

    public static String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public Drive() {
        for(int i = 0; i < shortIr.length; i++){
            shortIr[i] = new AnalogSensor("ir" + i, AnalogSensor.Type.SHORT_RANGE);
        }

        for(int i = 0; i < longIr.length; i++){
            longIr[i] = new AnalogSensor("longir" + i, AnalogSensor.Type.LONG_RANGE);
        }

        for (int i = 0; i < sonar.length; i++){
            sonar[i] = new AnalogSensor("sonar"+i, AnalogSensor.Type.SONAR);
        }

        for (int i = 0; i < lineFollow.length; i++){
            lineFollow[i] = new AnalogSensor("line follow"+i, AnalogSensor.Type.LINE_FOLLOW);
        }

        verbose = false;
        useSensors = true;
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

        dStage = DanceStage.BACK;

        glyph = new GlyphPlacementSystem(hardwareMap, this);

        cryptobox = new Cryptobox(telemetry, glyph);

        if (useGyro) {
            initializeGyro(telemetry, hardwareMap);
        }

        log.add("useGyro: " + useGyro);

        if (useSensors) {
            for (AnalogSensor aShortIr : shortIr) {
                aShortIr.initialize(hardwareMap);
            }

            for (AnalogSensor aLongIr : shortIr) {
                aLongIr.initialize(hardwareMap);
            }

            for (AnalogSensor aSonar: sonar) {
                aSonar.initialize(hardwareMap);
            }

            for (AnalogSensor aLineFollow : lineFollow){
                aLineFollow.initialize(hardwareMap);
            }
            /*try {
                colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color sensor");
            }catch (IllegalArgumentException ex){
                colorSensor = null;
            }*/
        }

        winch = hardwareMap.servo.get("winch");
        motorLeftFront = initializeMotor(hardwareMap, "front left");
        motorRightFront = initializeMotor(hardwareMap, "front right");
        motorLeftBack = initializeMotor(hardwareMap, "back left");
        motorRightBack = initializeMotor(hardwareMap, "back right");

        jewelServo = initializeServo(hardwareMap, "jewel");
        jewelHead = initializeServo(hardwareMap, "head");

        grabbyBoi = initializeServo(hardwareMap, "hand");

        leftBrake = initializeServo(hardwareMap, "left brake");
        rightBrake = initializeServo(hardwareMap, "right brake");

        leftCatch = initializeServo(hardwareMap, "left catch");
        rightCatch = initializeServo(hardwareMap, "right catch");

        horizontalDrive = initializeCrServo(hardwareMap, "horizontal");

        center = initializeDigital(hardwareMap, "center");
        collected = initializeDigital(hardwareMap, "collected");
        try {
            center.setState(false);
            center.setMode(DigitalChannel.Mode.INPUT);
        } catch (NullPointerException ex) { }

        bottom = initializeDigital(hardwareMap, "bottom");
        try {
            bottom.setState(false);
            bottom.setMode(DigitalChannel.Mode.INPUT);
        } catch (NullPointerException ex) { }

        intakeLeft = initializeMotor(hardwareMap, "intake left");
        intakeRight = initializeMotor(hardwareMap, "intake right");
        //The left intake is mounted "backwards"
        try {
            intakeLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        } catch (NullPointerException ex) { }

        inLServo = initializeCrServo(hardwareMap, "intake left servo");
        inRServo = initializeCrServo(hardwareMap, "intake right servo");
        //The left intake servo is mounted "backwards"
        try {
            inLServo.setDirection(DcMotorSimple.Direction.REVERSE);
        } catch (NullPointerException ex) { }

        verticalDrive = initializeMotor(hardwareMap, "vertical drive");
        try {
            verticalDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            verticalDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } catch (NullPointerException ex) { }
        //verticalDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void readSensorsSetUp() {
        for (int i = 0; i < AnalogSensor.NUM_OF_READINGS; i++ ) {
            for (AnalogSensor shortIr : shortIr) {
                shortIr.addReading();
            }
            for (AnalogSensor longIr : longIr) {
                longIr.addReading();
            }
            for (AnalogSensor sonar : sonar) {
                sonar.addReading();
            }
            for (AnalogSensor lineFollow : lineFollow){
                lineFollow.addReading();
            }
        }
    }

    public Cryptobox.GlyphColor getGlyphColor() {
        //return Cryptobox.GlyphColor.GREY;
        for (int i = 0; i < AnalogSensor.NUM_OF_READINGS; i++){
            for (AnalogSensor lineFollow : lineFollow){
                lineFollow.addReading();
            }
        }
        return lineFollow[0].getVAvg() > 1.5 ? Cryptobox.GlyphColor.NONE :
                lineFollow[0].getVAvg() > COLOR_THRESHOLD ? Cryptobox.GlyphColor.BROWN : Cryptobox.GlyphColor.GREY;
    }

    /*private float[] getRGB(){
        NormalizedRGBA color = colorSensor.getNormalizedColors();
        float[] val = {color.red, color.green, color.blue, color.alpha};
        return val;
    }

    public float[] getHSV() {
        NormalizedRGBA color = colorSensor.getNormalizedColors();
        float[] hsvValues = new float[3];
        Color.colorToHSV(color.toColor(), hsvValues);
        return hsvValues;
    }*/

    public ElapsedTime extendoTimer = new ElapsedTime();

    public void toggleExtendo() {
        //It's extendo, so put it back together
        extendoTimer = new ElapsedTime();
        Drive.isExtendo = !Drive.isExtendo;
        extendoTimer.reset();
        log.add("EIEIO");
    }

    public void extendoStep() {
        //Moving into extendo, so take it apart
        if (Drive.isExtendo) {
            if (extendoTimer.seconds() < .15) {
                driveXYR(1, 0, 1, 0, false);
            } else if (extendoTimer.seconds() < .4) {
                servoExtendo();
                driveLR(1, 1, 1);
            } else {
                driveXYR(1, 0, 0, 0, false);
            }
        }
        //Moving out of extendo, so put it together
        else {
            if (extendoTimer.seconds() < .2) {
                servoNotExtendo();
                pushRobotTogether();
            } else {
                driveXYR(1, 0, 0, 0, false);
            }
        }
    }

    public void toggleServoExtendo() {
        Drive.isExtendo = !Drive.isExtendo;
        if (Drive.isExtendo) {
            servoExtendo();
        } else {
            servoNotExtendo();
        }
    }

    private void servoExtendo() {
        lowerBrakes();
        unlockCatches();
        runBackWithEncoders();
    }

    private void servoNotExtendo() {
        raiseBrakes();
        lockCatches();
        freezeBack();
    }

    private DcMotor initializeMotor(HardwareMap hardwareMap, String motorName) {
        DcMotor motor = null;
        try {
            motor = hardwareMap.dcMotor.get(motorName);
        } catch (IllegalArgumentException ex) {
            telemetry.addData(motorName, "Could not find.");
            useMotors = false;
        }
        return motor;
    }

    private Servo initializeServo(HardwareMap hardwareMap, String servoName) {
        Servo servo = null;
        try {
            servo = hardwareMap.servo.get(servoName);
        } catch (IllegalArgumentException ex) {
            telemetry.addData(servoName, "Could not find.");
        }
        return servo;
    }

    private CRServo initializeCrServo(HardwareMap hardwareMap, String crServoName) {
        CRServo crServo = null;
        try {
            crServo = hardwareMap.crservo.get(crServoName);
        } catch (IllegalArgumentException ex) {
            telemetry.addData(crServoName, "Could not find.");
        }
        return crServo;
    }

    private DigitalChannel initializeDigital(HardwareMap hardwareMap, String digitalName) {
        DigitalChannel digitalChannel = null;
        try {
            digitalChannel = hardwareMap.digitalChannel.get(digitalName);
        } catch (IllegalArgumentException ex) {
            telemetry.addData(digitalName, "Could not find.");
        }
        return digitalChannel;
    }

    private double[] lastEncoders = new double[4];
    private double lastMilli = 0;
    public double[] encoderRates = new double[4];

    private double lastGyro = 0;
    public double gyroRate = 0;

    private final static int READINGS = 3;

    private double[][] lastShortIr = new double[READINGS][3];
    private double[][] lastLongIr = new double[READINGS][2];
    private double[][] lastSonar = new double[READINGS][2];

    public double[] shortIrRates = new double[3];
    public double[] longIrRates = new double[2];
    public double[] sonarRates = new double[2];

    private double lastUTrack = 0;
    public double uTrackRate = 0;

    private int index = 0;

    public void updateRates() {
        updateRates(0);
    }
    public void updateRates(double offset) {
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
            currShortIr[i] = sIr.getCmAvg(100, offset);
            shortIrRates[i] = (currShortIr[i] - lastShortIr[index][i]) / (currMilli - lastMilli);
        }

        double[] currLongIr = new double[2];
        for (int i = 0; i < currLongIr.length; i++) {
            AnalogSensor lIr = longIr[i];
            lIr.addReading();
            currLongIr[i] = lIr.getCmAvg(100, offset);
            longIrRates[i] = (currLongIr[i] - lastLongIr[index][i]) / (currMilli - lastMilli);
        }

        double[] currSonar = new double[2];
        for (int i = 0; i < currSonar.length; i++) {
            AnalogSensor sIr = sonar[i];
            sIr.addReading();
            currSonar[i] = sIr.getCmAvg(100, offset);
            sonarRates[i] = (currSonar[i] - lastSonar[index][i]) / (currMilli - lastMilli);
        }
        for (AnalogSensor lineFollow : lineFollow) {
            lineFollow.addReading();
        }

        //Gyro rates
        gyroRate = (currGyro - lastGyro) / (currMilli - lastMilli);

        //Update last to current
        lastGyro = currGyro;

        index = ++index % READINGS;

        lastShortIr[index] = currShortIr;
        lastLongIr[index] = currLongIr;
        lastSonar[index] = currSonar;

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

    public void glyphLocate() {
        targetY = GlyphPlacementSystem.Position.values()[glyph.uiTargetY + 3];
        targetX = GlyphPlacementSystem.HorizPos.values()[glyph.uiTargetX];
    }

    private ElapsedTime glyphCollectionTimer = new ElapsedTime();

    /**
     * One step in collecting a glyph - designed to be looped over
     * @return Whether a glyph has been collected
     */
    public boolean collectGlyphStep() {
        double glyphIn = C.get().getDouble("glyphIn");
        double glyphOut = C.get().getDouble("glyphOut");

        shortIr[0].addReading();
        double frontDistance = shortIr[0].getCmAvg();

        shortIr[1].addReading();
        double backDistance = shortIr[1].getCmAvg();

        telemetry.addData("frontDistance", frontDistance);
        telemetry.addData("backDistance", backDistance);
        //If the IR reading is closer to glyphIn than glyphOut, we assume the glyph is in
        boolean isGlyphIn = Math.abs(frontDistance - glyphIn) < Math.abs(frontDistance - glyphOut);
        boolean isGlyphBack = Math.abs(backDistance - glyphIn) < Math.abs(backDistance - glyphOut);

        if (!isGlyphIn && !isGlyphBack) {
            //Step 1: intakes in until a glyph is found
            intakeLeft(1);
            intakeRight(1);
            dance();
            glyphCollectionTimer.reset();
            return false;
        } else if (!isGlyphBack && glyphCollectionTimer.seconds() < C.get().getDouble("time")/2){
            //Step 2: after a glyph gets in, until half of time, rotate the glyph
            intakeLeft(-1);
            intakeRight(1);
            return false;
        } else if (!isGlyphBack && glyphCollectionTimer.seconds() < C.get().getDouble("time") * 3/2) {
            //Step 3: after a glyph gets in and has been rotated, pull it in
            intakeLeft(1);
            intakeRight(1);
            return false;
        } else if (!isGlyphBack && glyphCollectionTimer.seconds() >= C.get().getDouble("time") * 3/2) {
            //Step 4: If the glyph still isn't in, reset the glyphCollectionTimer to loop us back through to step 2
            glyphCollectionTimer.reset();
            return false;
            /** Should be made to run the robot backwards for a short period of time and reconnect, then break and return true */
        } else if (isGlyphBack) {
            //Step 5: But if the glyph is in, then stop the intakes and wait
            intakeLeft(0);
            intakeRight(0);

            pullBack();
            return true;
        }
        return false;
    }

    public enum DanceStage {
        BACK, TRANS, FORWARD
    }
    /**
     * Needs to be written: should make the robot move forwards and backwards randomly
     */
    private void dance() {
        switch (dStage){
            case BACK:{
                driveLRWithEncoders(1, -1, -1, 400, 1);


            }
        }
    }

    /**
     * Needs to be written: should run the front motors back aggressively
     */
    private void pullBack() {

    }

    public boolean driveLRWithEncoders(double left, double right, double speed, double targetTicks, double mulch) throws IllegalArgumentException{

        double currentTicks = max(motorLeftBack.getCurrentPosition(),
                motorLeftFront.getCurrentPosition(),
                motorRightBack.getCurrentPosition(), motorRightFront.getCurrentPosition());
        //if it has not reached the target, it tests if it is in the
        // last or first fourth of the way there, and
        // scales the speed such that it speeds up and slows down
        // to BASE_SPEED as it reaches the target
        if (currentTicks > targetTicks) {
            //if it has reached target, stop moving, reset encoders, and return PI
            stopMotors(); //stops the motors
            this.resetEncoders();
            this.runWithEncoders();
        }

        speed *= mulch;

        //if it hasn't reached the target (it won't have returned yet),
        // drive at the given speed (possibly scaled b/c of first and last fourth), and return false
        speed = Range.clip(speed, 0, FULL_SPEED);

        //Drives at x
        driveLR(FULL_SPEED, left * speed, right * speed);
        return false;
    }

    public int random (int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public int[] uTrackAutoTarget(Cryptobox.GlyphColor newGlyph) {
        return cryptobox.placeGlyph(newGlyph);
    }


    private int[] predict = {0};
    public int[] uTrackAutoTarget(Gamepad gamepad2) {
        int numPlaces = cryptobox.getNumGlyphsPlaced();
        if (uTrackAtBottom && collected.getState()) {
            /*if (!gamepad2.left_bumper && !gamepad2.right_bumper && gamepad2.right_trigger < DEADZONE_SIZE && gamepad2.left_trigger < DEADZONE_SIZE) {
                internalIntakeRight(0);
                internalIntakeLeft(0);
            }*/

            Cryptobox.GlyphColor color = getGlyphColor();

            if (!color.equals(Cryptobox.GlyphColor.NONE)) {
                predict = uTrackAutoTarget(color);
                telemetry.log().add("greybrown: [" + predict[0] + ", " + predict[1] + "]");
                /*if (!(((predict[0] + predict[1]) == 0) && !(numPlaces == 11))) {*/
                telemetry.log().add("brown placed");
                uTrack();
            }
        } /*else if(uTrackAtBottom && !collected.getState()) {
            if(!stage.equals(GlyphPlacementSystem.Stage.HOME) && !stage.equals(GlyphPlacementSystem.Stage.GRAB) &&
                    !gamepad2.left_bumper && !gamepad2.right_bumper && gamepad2.right_trigger < DEADZONE_SIZE && gamepad2.left_trigger < DEADZONE_SIZE){
                internalIntakeLeft(1);
                internalIntakeRight(1);
            }
        } */else if (!uTrackAtBottom) {
            /*if(!stage.equals(GlyphPlacementSystem.Stage.HOME) && !stage.equals(GlyphPlacementSystem.Stage.GRAB) &&
                    !gamepad2.left_bumper && !gamepad2.right_bumper && gamepad2.right_trigger < DEADZONE_SIZE && gamepad2.left_trigger < DEADZONE_SIZE){
                internalIntakeLeft(1);
                internalIntakeRight(1);
            }*/
            telemetry.log().add("running");
            uTrack();
        }
        return predict;
    }

    public boolean uTrack() {
        telemetry.addData("stage", stage);
        telemetry.addData("horizontal timer", glyph.horizontalTimer.seconds());
        telemetry.addData("power", getHorizontalDrive());
        switch (stage) {
            case RESET: { reset(); return false; }
            case HOME: { home(); return false; }
            case GRAB: { grab(); return false; }
            case PLACE1: { place1(); return false; }
            case PAUSE1: { pause1(); return false; }
            case PLACE2:{ place2(); return false; }
            case RETURN1: { return1(); return false; }
            case RELEASE: { release(); return false; }
            case PAUSE2: { pause2(); return false; }
            case RETURN2: { return return2(); }
        }
        return false;
    }
    private void home() {
        //Close the hand
        //Jaden closed his hand
        //setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        closeHand();
        //jewelOut();
        glyphLocate();
        handDropTimer.reset();

        glyph.currentY = GlyphPlacementSystem.Position.HOME;
        glyph.currentX = GlyphPlacementSystem.HorizPos.CENTER;

        stage = GlyphPlacementSystem.Stage.GRAB;
    }
    private void grab() {
        if (handDropTimer.seconds() >= .5) {
            stage = GlyphPlacementSystem.Stage.PLACE1;
        }
    }
    private void place1() {
        //Raise the u-track
        setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISED);
        if(glyph.currentY.equals(GlyphPlacementSystem.Position.RAISED)) {
            stage = GlyphPlacementSystem.Stage.PAUSE1;
            glyph.setXPower(targetX);
        }
    }
    private void pause1() {
        //Move to target X location
        if(glyph.xTargetReached(targetX)) {
            stage = GlyphPlacementSystem.Stage.PLACE2;
        }
    }
    private void place2() {
        //Move to target Y location
        glyph.setTargetPosition(targetY);
        if(glyph.currentY.equals(targetY)) {
            stage = GlyphPlacementSystem.Stage.RETURN1;
        }
    }
    private void return1() {
        //Open the hand; raise the u-track
        openHand();
        handDropTimer.reset();

        stage = GlyphPlacementSystem.Stage.RELEASE;
    }
    private void release() {
        if (handDropTimer.seconds() >= .5) {
            glyph.setTargetPosition(GlyphPlacementSystem.Position.RAISEDBACK);
            if (glyph.currentY.equals(GlyphPlacementSystem.Position.RAISEDBACK)) {
                stage = GlyphPlacementSystem.Stage.PAUSE2;
                glyph.setXPower(GlyphPlacementSystem.HorizPos.CENTER);
            }
        }
    }
    private void pause2() {
        //Move back to center x location (so the hand fits back in the robot)
        //glyph.setXPower(GlyphPlacementSystem.HorizPos.CENTER);
        if(glyph.xTargetReached(GlyphPlacementSystem.HorizPos.CENTER)) {
            log.add("reached x target, center is " + getCenterState());
            stage = GlyphPlacementSystem.Stage.RETURN2;
            setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            glyph.setAboveHomeTarget();
            setVerticalDrive(-1);
        }
    }
    private boolean return2() {
        //Move back to the bottom and get ready to do it again
        if(glyph.currentY.equals(GlyphPlacementSystem.Position.ABOVEHOME)) {
            stage = GlyphPlacementSystem.Stage.RESET;
            setVerticalDrive(0);
            //jewelUp();
            uTrackAtBottom = true;
            return true;
        }
        return false;
    }

    private boolean lastBottom;
    private ElapsedTime bottomTimer = new ElapsedTime();

    private void reset() {
        boolean currBottom = getBottomState();
        uTrackAtBottom = false;

        if(!currBottom) {
            setVerticalDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
            setVerticalDrive(-1);
        } else {
            //setVerticalDrive(-.5);
            resetUTrack();
        }
    }

    public void resetUTrack() {
        stage = GlyphPlacementSystem.Stage.HOME;
        setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        //jewelUp();
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
        heading = heading / 15;

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
        grabbyBoi.setPosition(.54);
    }

    public void closeHand() {
        handIsOpen = false;
        grabbyBoi.setPosition(.88);
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
        intakeLeft.setPower(power);
    }

    public void intakeRight(double power) {
        intakeRight.setPower(power);
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
        jewelServo.setPosition(.82);
    }

    public void jewelUp() {
        jewelServo.setPosition(.47);
        jewelCenter();
    }

    public void jewelCenter() {
        jewelHead.setPosition(.47);
    }

    public void jewelLeft() {
        jewelHead.setPosition(0.01);
    }

    public void jewelRight() {
        jewelHead.setPosition(0.87);
    }

    /*
    Moves the jewel out of the way
     */
    public void jewelOut() {
        jewelServo.setPosition(.80);
        jewelCenter();

    }

    public void jewelStowed() {
        jewelServo.setPosition(.35);
        jewelCenter();
    }

    public void lockCatches() {
        rightCatch.setPosition(1);
        leftCatch.setPosition(0);
    }

    public void unlockCatches() {
        rightCatch.setPosition(.12);
        leftCatch.setPosition(.9);
    }

    public void lowerBrakes() {
        leftBrake.setPosition(.8);
        rightBrake.setPosition(.01);
    }

    public void raiseBrakes() {
        leftBrake.setPosition(0.03);
        rightBrake.setPosition(.66);
    }

    public boolean winchOpen = false;

    public void toggleWinch() {
        if (winchOpen) {
            stowWinch();
        } else {
            openWinch();
        }
    }

    public void stowWinch() {
        winch.setPosition(.97);
        winchOpen = false;
    }

    public void openWinch() {
        winch.setPosition(.69);
        winchOpen = true;
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
