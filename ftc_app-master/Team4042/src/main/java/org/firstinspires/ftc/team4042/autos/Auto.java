package org.firstinspires.ftc.team4042.autos;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.team4042.drive.Cryptobox;
import org.firstinspires.ftc.team4042.drive.Direction;
import org.firstinspires.ftc.team4042.drive.Drive;
import org.firstinspires.ftc.team4042.drive.GlyphPlacementSystem;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Parses a file to figure out which instructions to run. CAN NOT ACTUALLY RUN INSTRUCTIONS.
 */
@Autonomous(name="Abstract Auto", group="autos")
public abstract class Auto extends LinearVisionOpMode {

    MecanumDrive drive = new MecanumDrive(true);
    private VuMarkIdentifier vuMarkIdentifier = new VuMarkIdentifier();
    private RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;

    private double PROPORTIONAL_ROTATE = C.get().getDouble("PropRot");
    private double DERIV_ROTATE = C.get().getDouble("DerivRot");

    public static final File autoRoot = new File ("./storage/emulated/0/bluetooth/");

    private Telemetry.Log log;

    private double startRoll;
    private double startPitch;
    private ElapsedTime intakeTimer = new ElapsedTime();
    private int intakeCount = -1;
    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime colorTime = new ElapsedTime();
    private double colorV = 100;

    private boolean readMark = false;
    private boolean done = true;
    private boolean placeNew = false;

    private AutoParser parser;

    public void setUp(MecanumDrive drive, String filePath) {
        this.drive = drive;

        log = telemetry.log();

        drive.initialize(telemetry, hardwareMap);

        drive.targetY = GlyphPlacementSystem.Position.TOP;
        drive.targetX = GlyphPlacementSystem.HorizPos.LEFT;
        drive.stage = GlyphPlacementSystem.Stage.HOME;
        drive.glyph.setHomeTarget();
        drive.cryptobox.clear();
        drive.cryptobox.writeFile();

        vuMarkIdentifier.initialize(telemetry, hardwareMap);

        parser = new AutoParser(telemetry, filePath);
        drive.initializeGyro(telemetry, hardwareMap);
    }

    /**
     * Tries to initialize the gyro until it works
     */
    public void gyro() {
        drive.initializeGyro(telemetry, hardwareMap);

        do {
            drive.gyro.updateAngles();
            startRoll = drive.gyro.getRoll();
            startPitch = drive.gyro.getPitch();
        } while (startRoll == 0 && startPitch == 0 && opModeIsActive());
    }

    public void runAuto(boolean useSensors) {
        if (useSensors) runAuto();
        
        while(true) {
            RelicRecoveryVuMark j = vuMarkIdentifier.getMark();
            //String j = getBallColor(x);
            Log.d("TOMMY", j.toString());
        }
    }

    /**
     * Runs the list of instructions
     */
    public void runAuto() {
        //vuMarkIdentifier.initialize(telemetry, hardwareMap);

        timer.reset();
        log.add("running auto");
        gyro();

        try {
            drive.jewelOut();
            drive.jewelCenter();
        } catch (NullPointerException ex) { }

        drive.resetEncoders();
        drive.setEncoders(true);
        drive.setVerbose(false);

        Drive.isExtendo = false;
        Drive.crawl = false;
        Drive.tank = false;
        Drive.ivan = true;
        Drive.useSideLimits = true;
        Drive.top = false;



        //Reads each instruction and acts accordingly
        AutoInstruction instruction = parser.popNext();
        while (instruction != null && opModeIsActive()) {
            String functionName = instruction.getFunctionName();
            HashMap<String, String> parameters = instruction.getParameters();
            log.add("function: " + functionName);

            switch (functionName) {
                case "drive":
                    autoDrive(parameters);
                    break;
                case "motor":
                    motorDrive(parameters);
                    break;
                case "doff":
                    autoDriveOff(parameters);
                    break;
                case "rot":
                    autoRotate(parameters);
                    break;
                case "sdrive":
                    autoSensorDrive(parameters);
                    break;
                case "s2drive":
                    autoTwoSensorDrive(parameters);
                    break;
                case "driveLR":
                    autoDriveLR(parameters);
                case "up":
                    jewelUp(parameters);
                    break;
                case "center":
                    jewelCenter(parameters);
                    break;
                case "down":
                    drive.jewelDown();
                    break;
                case "knockr":
                    knockRedJewel(parameters);
                    break;
                case "knockb":
                    knockBlueJewel(parameters);
                    break;
                case "jleft":
                    knockLeftJewel(parameters);
                    break;
                case "jright":
                    knockRightJewel(parameters);
                    break;
                case "getmark":
                    getVuMark(parameters);
                    break;
                case "place":
                    placeGlyph(parameters);
                    break;
                case "align":
                    alignHorizontally(parameters);
                    break;
                case "glyph":
                    grabGlyph(parameters);
                    break;
                case "open":
                    openIntakes(parameters);
                    break;
                case "brace":
                    drive.openWinch();
                    break;
                case "wait":
                    wait(parameters);
                    break;
                case "break":
                    breakRobot(parameters);
                    break;
                case "place2":
                    place2(parameters);
                    break;
                case "placeFront":
                    placeInFront(parameters);
                    break;
                default:
                    System.err.println("Unknown function called from file " + parser.getFile());
                    break;
            }
            instruction = parser.popNext();
        }
        this.waitNew(30);
    }

    private void motorDrive(HashMap<String, String> parameters) {
        double leftFront = parser.getParam(parameters, "0");
        double rightFront = parser.getParam(parameters, "1");
        double rightBack = parser.getParam(parameters, "2");
        double leftBack = parser.getParam(parameters, "3");

        double[] motors = {leftFront, rightFront, rightBack, leftBack};

        double speedFactor = parser.getParam(parameters, "speedFactor", 1);
        double time = parser.getParam(parameters, "time");

        ElapsedTime timer = new ElapsedTime();

        while (timer.seconds() < time && opModeIsActive()) {
            drive.setMotorPower(motors, speedFactor);
        }
        drive.stopMotors();
    }

    private void breakRobot(HashMap<String, String> parameters) {
        drive.toggleExtendo();
        while (!drive.extendoStep() && opModeIsActive()) {}
    }

    private void getVuMark(HashMap<String, String> parameters) {
        readMark = true;
        vuMarkIdentifier.prepareMark();
    }

    public void wait(HashMap<String, String> parameters) {
        double seconds = parser.getParam(parameters, "sec");

        waitNew(seconds);
    }

    public void waitNew(double seconds) {
        ElapsedTime waitTimer = new ElapsedTime();
        while (waitTimer.seconds() < seconds && opModeIsActive());
    }

    public void grabGlyph(HashMap<String, String> parameters) {
        drive.readSensorsSetUp();
        drive.intakeLeft(1);
        drive.intakeRight(1);

        while (opModeIsActive()) {
            while (opModeIsActive() && !drive.collectGlyphStep());
            //while (opModeIsActive() && !drive.driveLRWithEncoders(-1, -1, 1, 100, 1));
            //while (opModeIsActive() && !drive.driveLRWithEncoders(-1, 1, 1, 70, 1));
            while (opModeIsActive() && !drive.driveLRWithEncoders(-1, -1, 1, 500, 1));
        }
    }

    public void alignHorizontally(HashMap<String, String> parameters) {
        double speed = parser.getParam(parameters, "speed");
        boolean isRed = parameters.get("color").equalsIgnoreCase("r");
        AnalogSensor sonar = isRed ? drive.sonar[1] : drive.sonar[0];
        double y = parameters.containsKey("y") ? parser.getParam(parameters,"y") : 0;
        double targetGyro = parser.getParam(parameters, "gyro");
        double time = parameters.containsKey("time") ? parser.getParam(parameters, "time") : -1;
        int target = parser.getParam(parameters, "target", -1);

        double left = parser.getParam(parameters, "lPos");
        double center = parser.getParam(parameters, "cPos");
        double right = parser.getParam(parameters, "rPos");
        vuMark = vuMark.equals(RelicRecoveryVuMark.UNKNOWN) ? RelicRecoveryVuMark.CENTER : vuMark;
        double dist = /*vuMark.equals(RelicRecoveryVuMark.LEFT) ? left : vuMark.equals(RelicRecoveryVuMark.RIGHT) ? right : */center;

        drive.stopMotors();
        drive.resetEncoders();
        drive.runWithEncoders();

        double xCurrDistance;
        int i = 0;
        while (i < AnalogSensor.NUM_OF_READINGS && opModeIsActive()) {
            //read the IRs just to set them up
            sonar.addReading();
            i++;
        }

        ElapsedTime timeout = new ElapsedTime();
        timeout.reset();

        do {
            drive.updateRates();

            double r = drive.useGyro(targetGyro) * .75 + 5 * drive.gyroRate;
            r = r < .05 && r > 0 ? 0 : r;
            r = r > -.05 && r < 0 ? 0 : r;

            //Get the distances and derivative terms
            xCurrDistance = sonar.getCmAvg();
            double xDerivValue = isRed ? drive.sonarRates[1] : drive.sonarRates[0];

            //Set up the derivative and proportional terms
            double xDeriv = xDerivValue * 20;
            double xProportional = (xCurrDistance - dist) * .025;

            //Apply the controller
            double xFactor = isRed ? -(xProportional + xDeriv) : (xProportional + xDeriv);
            //telemetry.addData("xIr cm", xCurrDistance);
            //telemetry.addData("x", xFactor);
            //telemetry.addData("r", r);
            telemetry.log().add("" + xDeriv);
            //telemetry.update();

            xFactor = Range.clip(xFactor, -1, 1);
            r = Range.clip(r, -1, 1);

            drive.runWithEncoders();
            drive.driveXYR(1, xFactor, y, r, false);
        } while (((Math.abs(dist - xCurrDistance) > 2) || target > 0) && (target < 0 || target > drive.getEncoderTravel()) && (time < 0 || timeout.seconds() < time) && opModeIsActive());

        //If you're off your target distance by 2 cm or less, that's good enough : exit the while loop
        telemetry.log().add("target: " + dist);
        telemetry.log().add("curr: " + xCurrDistance);

        drive.stopMotors();
        drive.resetEncoders();
        drive.runWithEncoders();
    }

    public String getBallColor(Mat frame){
        //log.add(frame.height() + " x " + frame.width());
        //Imgproc.resize(frame, frame, new Size(960, 720));
        //telemetry.update();
        //Rect left_crop = new Rect(new Point(215,585), new Point(380, 719));
        //Rect right_crop = new Rect(new Point(460,585), new Point(620, 719));
        Rect left_crop = new Rect(new Point(464,688), new Point(617, 719));
        Rect right_crop = new Rect(new Point(771,672), new Point(942, 719));

        //Log.d("A", this.getFrameSize().width + " x " + this.getFrameSize().height);
        Mat right = new Mat(frame, right_crop);
        Mat left = new Mat(frame, left_crop);


        String result = "unspecified";
        Scalar left_colors  = Core.sumElems(left);
        Scalar right_colors = Core.sumElems(right);

        if(left_colors.val[0] >= left_colors.val[2]){
            result = "red";
        } else {
            result = "blue";
        }

        if(right_colors.val[0] >= right_colors.val[2]){
            result = result.concat(", red");
        } else {
            result = result.concat(", blue");
    }

        return result;
    }

    public void placeGlyph(HashMap<String, String> parameters) {

        drive.setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (vuMark == RelicRecoveryVuMark.UNKNOWN){
            vuMark = RelicRecoveryVuMark.CENTER;
        }
        vuMark = RelicRecoveryVuMark.LEFT;

        int column;
        switch (vuMark) {
            case RIGHT:
                column = 0;
                break;
            case CENTER:
                column = 1;
                break;
            case LEFT:
                column = 2;
                break;
            default:
                column = 1;
                break;
        }

        Cryptobox.GlyphColor newGlyph = Cryptobox.GlyphColor.GREY; //drive.getGlyphColor(getColorVoltage());

        drive.cryptobox.updateCipher(newGlyph, column);

        //Place the first glyph to match the vumark
        drive.cryptobox.driveGlyphPlacer(newGlyph, 0, column);

        drive.stage = GlyphPlacementSystem.Stage.HOME;

        done = drive.uTrack();
        drive.uTrackAtBottom = false;
    }

    public void placeInFront(HashMap<String, String> parameters) {
        drive.setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (vuMark == RelicRecoveryVuMark.UNKNOWN){
            vuMark = RelicRecoveryVuMark.CENTER;
        }

        int column;
        switch (vuMark) {
            case RIGHT:
                column = 0;
                break;
            case CENTER:
                column = 1;
                break;
            case LEFT:
                column = 2;
                break;
            default:
                column = 1;
                break;
        }

        Cryptobox.GlyphColor newGlyph = Cryptobox.GlyphColor.GREY; //drive.getGlyphColor(getColorVoltage());

        drive.cryptobox.updateCipher(newGlyph, column);

        //Place the first glyph to match the vumark
        drive.cryptobox.driveGlyphPlacer(newGlyph, 0, column);

        drive.glyph.uiTarget(1, 2);
        drive.glyphLocate();

        drive.stage = GlyphPlacementSystem.Stage.HOME;

        done = drive.uTrack();
        drive.uTrackAtBottom = false;
    }

    private double getColorVoltage() {
        ElapsedTime colorTimer = new ElapsedTime();
        double smallVoltage = Double.MAX_VALUE;

        while (colorTimer.seconds() < C.get().getDouble("colorReadTimer") && opModeIsActive()) {
            double currVoltage = drive.lineFollow[0].getV();
            if (currVoltage < smallVoltage && currVoltage > .1) {
                smallVoltage = currVoltage;
            }
        }
        return smallVoltage;
    }

    public void place2 (HashMap<String, String> parameters) {
        //while(!done && opModeIsActive());
        placeNew = true;
        //drive.stage = GlyphPlacementSystem.Stage.RESET;
    }

    public void jewelUp(HashMap<String, String> parameters) {
        drive.jewelOut();
    }

    public void jewelCenter(HashMap<String, String> parameters) {
        drive.jewelCenter();
    }

    public void knockLeftJewel(HashMap<String, String> parameters) {
        jewelLeft();
    }

    public void knockRightJewel(HashMap<String, String> parameters) {
        jewelRight();
    }

    public void knockRedJewel(HashMap<String, String> parameters) {
        try {
            String balls = getBallColor(vuMarkIdentifier.getFrame());
            //telemetry.addData("ball orientation", balls);
            switch (balls) {
                case "red":
                    jewelLeft();
                    break;
                case "blue":
                    jewelRight();
                    break;
                case "red, blue":
                    jewelLeft();
                    break;
                case "blue, red":
                    jewelRight();
                    break;
                case ", blue":
                    jewelLeft();
                    break;
                case ", red":
                    jewelRight();
                    break;
            }
        } catch (CvException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("CvException", sw.toString());
        }
    }

    public void knockBlueJewel(HashMap<String, String> parameters) {
        log.add("blue jewel");
        //Mat mat = getFrameRgba();
        //String balls = getBallColor(getFrameRgba());
        String balls = getBallColor(vuMarkIdentifier.getFrame());
        //String balls = "red, blue";
        log.add("ball orientation: " + balls);
        switch (balls) {
            case "red":
                jewelRight();
                break;
            case "blue":
                jewelLeft();
                break;
            case "red, blue":
                jewelRight();
                break;
            case "blue, red":
                jewelLeft();
                break;
            case ", blue":
                jewelRight();
                break;
            case ", red":
                jewelLeft();
                break;
        }
    }

    public void autoDrive(HashMap<String, String> parameters) {
        Direction direction = new Direction(parser.getParam(parameters, "x"), -parser.getParam(parameters, "y"));
        double speed = parser.getParam(parameters, "speed");
        double targetTicks = parser.getParam(parameters, "target");
        double time = parser.getParam(parameters, "time", -1.0);
        boolean useGyro = parameters.containsKey("gyro");
        double targetGyro = useGyro ? Double.parseDouble(parameters.get("gyro")) : 0;

        autoDrive(direction, speed, targetTicks, time, useGyro, targetGyro);
    }

    /**
     * Drives in the given Direction at the given speed until targetTicks is reached
     * @param direction The direction to head in
     * @param speed The speed to move at
     * @param targetTicks The final distance to have travelled, in encoder ticks
     */
    private void autoDrive(Direction direction, double speed, double targetTicks, double time, boolean useGyro, double targetGyro) {
        //log.add("autoDrive invoked with direction " + direction + " speed " + speed + " targetTicks " + targetTicks);
        boolean done = false;
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while (opModeIsActive() && !done && (timer.seconds() <= time || time <= 0)) {
            //Keep going if (you're not done and the seconds are less than the target) or (you're not waiting for the timer and you're not done)
            done = drive.driveWithEncoders(direction, speed, targetTicks, useGyro, targetGyro, false, 1);
            //telemetry.addData("targetTime", time);
            //telemetry.addData("time", timer.seconds());
            //telemetry.addData("DONE", done);
            //telemetry.update();
            //telemetry.update();
        }
        drive.resetEncoders();
        drive.runWithEncoders();
    }

    private void autoDriveOff(HashMap<String, String> parameters) {
        Direction direction = new Direction(parser.getParam(parameters, "x"),
                -parser.getParam(parameters, "y"));
        double speed = parser.getParam(parameters, "speed");
        double gyro = parser.getParam(parameters, "gyro");
        int sonarId = parameters.containsKey("sonar") ? Integer.parseInt(parameters.get("sonar")) : -1;
        double dist = parser.getParam(parameters, "dist", 0);

        //Drive in the direction indicated
        if (sonarId >= 0) {
            AnalogSensor xSonar = drive.sonar[sonarId];

            boolean done;
            for (int i = 0; i < AnalogSensor.NUM_OF_READINGS && opModeIsActive(); i++) {
                xSonar.addReading();
            }

            do {
                drive.updateRates();

                //Get the distances and derivative terms
                xSonar.addReading();

                //Proportional/derivative controller
                double xFactor = getSensorFactor(AnalogSensor.Type.SONAR, sonarId, xSonar.getCmAvg(), dist);
                Direction newDir = new Direction(Range.clip(direction.getX() + xFactor, -1, 1), direction.getY());

                done = drive.driveWithEncoders(newDir, speed, 750, true, gyro, false, 1);
            } while (opModeIsActive() && !done);

            drive.resetEncoders();
            drive.runWithEncoders();
        } else {
            autoDrive(direction, speed, 750, -1, true, gyro);
        }

        double roll;
        double pitch;

        do {
            drive.gyro.updateAngles();
            roll = drive.gyro.getRoll();
            pitch = drive.gyro.getPitch();
            log.add("roll: " + roll + " pitch: " + pitch);
            //log.add("" + opModeIsActive());
            autoDrive(direction, speed, 100, -1, true, gyro);
        }
        while ((Math.abs(roll - startRoll) >= 3) ||
                (Math.abs(pitch - startPitch) >= 3) && opModeIsActive());
            //If you're too tipped forward/backwards or left/right, then keep driving
            //This effectively drives until you're off the balancing stone

        drive.resetEncoders();
        drive.runWithEncoders();
    }

    public void autoRotate(HashMap<String, String> parameters) {
        double realR = parser.getParam(parameters, "r");

        double speed = parser.getParam(parameters, "speed");

        double timeout = parameters.containsKey("time") ? parser.getParam(parameters, "time") : -1;
        autoRotate(realR, speed, timeout);

        drive.resetEncoders();
        drive.runWithEncoders();
    }

    /**
     * Drives in the given Rotation at the given speed until targetTicks is reached
     * @param realR The degree to rotate to
     * @param speed The speed to rotate at
     */
    private void autoRotate(double realR, double speed, double time) {
        log.add("Got to rotate:");
        log.add("" + opModeIsActive());
        double gyro;
        ElapsedTime timer = new ElapsedTime();
        do {
            gyro = drive.gyro.updateHeading();
            double diff = realR - gyro;
            //telemetry.addData("gyro",gyro + " realR: " + realR + " diff: " + diff);
            if (diff > 270) { diff -= 360; }
            if (diff < -270) { diff += 360; }
            if (Math.abs(diff) > 2) {
                drive.driveXYR(speed, 0, 0, DERIV_ROTATE * diff, false, PROPORTIONAL_ROTATE);
            }
            log.add("" + opModeIsActive());
        } while (Math.abs(gyro - realR) > 2 && opModeIsActive() && (time < 0 || timer.seconds() < time));

        drive.stopMotors();
        drive.resetEncoders();
        drive.runWithEncoders();
        //telemetry.update();
    }

    public void autoTwoSensorDrive(HashMap<String, String> parameters) {
        double speed = Double.parseDouble(parameters.get("speed"));

        double yTargetDistance = Double.parseDouble(parameters.get("ydistance"));
        int yIr = Integer.parseInt(parameters.get("yir"));
        boolean yLongIr = Boolean.parseBoolean(parameters.get("ylong"));
        double targetGyro = Double.parseDouble(parameters.get("gyro"));

        log.add("" + parameters.containsKey("xdistance"));
        if (parameters.containsKey("xdistance")) {
            double xTargetDistance = Double.parseDouble(parameters.get("xdistance"));
            int xIr = Integer.parseInt(parameters.get("xir"));
            boolean xLongIr = Boolean.parseBoolean(parameters.get("xlong"));
            autoTwoSensorDrive(speed, xTargetDistance, xIr, xLongIr, true, yTargetDistance, yIr, yLongIr, targetGyro);
        }
        else {
            autoTwoSensorDrive(speed, 0, 0, false, false, yTargetDistance, yIr, yLongIr, targetGyro);
        }
    }

    /**
     * Drives until two sensors returns a target value, one for the x positioning and one for the y
     * @param speed The speed to move at
     * @param xTargetDistance The final distance for the x sensor to return
     * @param xIrId The sensor to read an x distance from
     * @param xIsLongRange Whether the x sensor is long-range or not
     * @param yTargetDistance The final distance for the y sensor to return
     * @param yIrId The sensor to read an y distance from
     * @param yIsLongRange Whether the y sensor is long-range or not
     */
    //35, 33 diagonal
    private void autoTwoSensorDrive(double speed, double xTargetDistance, int xIrId, boolean xIsLongRange, boolean useX,
                                    double yTargetDistance, int yIrId, boolean yIsLongRange, double targetGyro) {

        //autoDrive(direction, speed, targetTicks, -1, false, targetGyro);

        AnalogSensor xIr = xIsLongRange ? drive.longIr[xIrId] : drive.shortIr[xIrId];
        AnalogSensor yIr = yIsLongRange ? drive.longIr[yIrId] : drive.shortIr[yIrId];

        telemetry.addData("xIr", xIr + " yIr " + yIr);

        double xCurrDistance;
        double yCurrDistance;
        int i = 0;
        while (i < AnalogSensor.NUM_OF_READINGS && opModeIsActive()) {
            //read the IRs just to set them up
            xIr.addReading();
            yIr.addReading();
            telemetry.addData("xIr cm", xIr.getCmAvg());
            telemetry.addData("yIr cm", yIr.getCmAvg());
            //telemetry.update();
            i++;
        }

        ElapsedTime timeout = new ElapsedTime();
        timeout.reset();

        do {
            double speedFactor = speed;

            drive.updateRates();

            double r = drive.useGyro(targetGyro) * .75 + 5 * drive.gyroRate;
            r = r < .05 && r > 0 ? 0 : r;
            r = r > -.05 && r < 0 ? 0 : r;

            //Get the distances and derivative terms
            xCurrDistance = xIr.getCmAvg();
            yCurrDistance = yIr.getCmAvg();
            double xDerivValue = xIsLongRange ? drive.longIrRates[xIrId] : drive.shortIrRates[xIrId];
            double yDerivValue = yIsLongRange ? drive.longIrRates[yIrId] : drive.shortIrRates[yIrId];

            //Set up the derivative and proportional terms
            double xDeriv = xDerivValue * -10;
            double xProportional = (xCurrDistance - xTargetDistance) * .025;

            double yDeriv = yDerivValue * -10;
            double yProportional = (yCurrDistance - yTargetDistance) * .025;

            //Apply the controller
            double xFactor = (xProportional - xDeriv);
            double yFactor = (yProportional - yDeriv);
            telemetry.addData("xIr cm", xCurrDistance);
            telemetry.addData("yIr cm", yCurrDistance);
            telemetry.addData("x", xFactor);
            telemetry.addData("y", yFactor);
            telemetry.addData("r", r);
            //telemetry.update();

            xFactor = Range.clip(xFactor, -1, 1);
            yFactor = Range.clip(yFactor, -1, 1);
            r = Range.clip(r, -1, 1);

            //Actually drives
            if (useX) {
                //drive.driveXYR(speedFactor, xFactor/2, -yFactor/2, r, false);
                drive.runWithoutEncoders();
                drive.driveXYR(1, xFactor * 4.5, -yFactor / 2, r, false);
            } else {
                //drive.driveXYR(speedFactor, 0, -yFactor/2, r, false);
                drive.driveXYR(1, 0, -yFactor / 2, r, false);
            }
        } while (((Math.abs(xTargetDistance - xCurrDistance) > 2)) && timeout.seconds() < 5 && opModeIsActive());

        //If you're off your target distance by 2 cm or less, that's good enough : exit the while loop
        drive.stopMotors();
        drive.runWithEncoders();
    }

    public void autoDriveLR(HashMap<String, String> parameters){
        double l = Double.parseDouble(parameters.get("l"));
        double r = Double.parseDouble(parameters.get("r"));
        double speed = Double.parseDouble(parameters.get("speed"));
        int target = Integer.parseInt(parameters.get("target"));
        boolean intake = parameters.containsKey("intake");

        if (intake) {
            drive.intakeLeft(1);
            drive.intakeRight(1);
        }

        while (opModeIsActive() && !drive.driveLRWithEncoders(l, r, speed, target, 1));
        drive.resetEncoders();
        drive.runWithEncoders();
    }

    public void autoSensorDrive(HashMap<String, String> parameters) {
        double targetGyro = parser.getParam(parameters, "gyro");

        boolean useX = parameters.containsKey("xdistance");
        boolean useY = parameters.containsKey("ydistance");

        double yTargetDistance = useY ? Double.parseDouble(parameters.get("ydistance")) : 0;
        int yIr = useY ? Integer.parseInt(parameters.get("yir")) : 0;

        double offset = parser.getParam(parameters, "offset", 0);

        double speed = parser.getParam(parameters, "speed", 1);

        AnalogSensor.Type yType = AnalogSensor.Type.SHORT_RANGE;
        if (useY) {
            switch (parameters.get("ytype")) {
                case "short":
                    yType = AnalogSensor.Type.SHORT_RANGE;
                    break;
                case "long":
                    yType = AnalogSensor.Type.LONG_RANGE;
                    break;
                case "sonar":
                    yType = AnalogSensor.Type.SONAR;
                    break;
                default:
                    yType = AnalogSensor.Type.SHORT_RANGE;
                    break;
            }
        }

        log.add("" + parameters.containsKey("offset"));

        double xTargetDistance = useX ? Double.parseDouble(parameters.get("xdistance")) : 0;
        int xIr = useX ? Integer.parseInt(parameters.get("xir")) : 0;

        AnalogSensor.Type xType = AnalogSensor.Type.SHORT_RANGE;
        if (useX) {
            switch (parameters.get("xtype")) {
                case "short":
                    xType = AnalogSensor.Type.SHORT_RANGE;
                    break;
                case "long":
                    xType = AnalogSensor.Type.LONG_RANGE;
                    break;
                case "sonar":
                    xType = AnalogSensor.Type.SONAR;
                    break;
                default:
                    xType = AnalogSensor.Type.SHORT_RANGE;
                    break;
            }
        }

        boolean testMode = parameters.containsKey("test");

        double time = parser.getParam(parameters, "time", 2.5);

        double prop = parser.getParam(parameters, "prop");

        boolean remove = parameters.containsKey("remove") && Boolean.parseBoolean(parameters.get("remove"));

        autoSensorDrive(xTargetDistance, xIr, xType, useX, yTargetDistance, yIr, yType, useY, targetGyro, offset, speed, testMode, time, prop, remove);
    }

    private boolean bang = false;

    /**
     * Drives until two sensors returns a target value, one for the x positioning and one for the y
     * @param xTargetDistance The final distance for the x sensor to return
     * @param xIrId The sensor to read an x distance from
     * @param xType Whether the x sensor is long-range or not
     * @param yTargetDistance The final distance for the y sensor to return
     * @param yIrId The sensor to read an y distance from
     * @param yType Whether the y sensor is long-range or not
     */
    private void autoSensorDrive(double xTargetDistance, int xIrId, AnalogSensor.Type xType, boolean useX,
                                 double yTargetDistance, int yIrId, AnalogSensor.Type yType, boolean useY,
                                 double targetGyro, double offset, double speed, boolean testMode, double time, double prop, boolean remove) {
        if (useX) {
            AnalogSensor xIr = null;
            switch (xType) {
                case SHORT_RANGE:
                    xIr = drive.shortIr[xIrId];
                    break;
                case LONG_RANGE:
                    xIr = drive.longIr[xIrId];
                    break;
                case SONAR:
                    xIr = drive.sonar[xIrId];
                    break;
            }

            double xCurrDistance;
            int i = 0;
            while (i < AnalogSensor.NUM_OF_READINGS && opModeIsActive()) {
                //read the IRs just to set them up
                xIr.addReading();
                i++;
            }

            ElapsedTime timeout = new ElapsedTime();
            timeout.reset();

            bangBangTimer.reset();

            do {
                xIr.addReading();

                drive.updateRates(offset);
                double r = getSensorR(targetGyro);

                double wallAdjust = false ? 0 : .75;

                drive.driveXYR(1, 0, wallAdjust, r * 3 / 2, false);

                if (bangBangTimer.seconds() > C.get().getDouble("BangTimer")) {
                    //Get the distances and derivative terms
                    xIr.addReading(remove);

                    xCurrDistance = xIr.getCmAvg(100, offset);

                    double xPower = (xCurrDistance - xTargetDistance) / Math.abs(xCurrDistance - xTargetDistance) * -speed;

                    log.add("xPower: " + xPower);
                    log.add("diff: " + (xCurrDistance - xTargetDistance));
                    drive.resetEncoders();
                    drive.runWithEncoders();

                    boolean done = false;
                    while (opModeIsActive() && !done) {
                        Direction dir = new Direction(xPower, wallAdjust);

                        double targetTicks = Math.abs(xCurrDistance - xTargetDistance) > 6 ? 300 : Math.abs(xCurrDistance - xTargetDistance) * prop;
                        log.add(xCurrDistance + "");

                        if (Math.abs(xCurrDistance - xTargetDistance) < 0.1) { break; }

                        done = drive.driveWithEncoders(dir, 1, targetTicks, true, targetGyro,false, C.get().getDouble("mulch"));

                    }

                    drive.resetEncoders();
                    drive.runWithEncoders();
                    bangBangTimer.reset();
                }
            }
            while (opModeIsActive() && ((timeout.seconds() < C.get().getDouble("SensorTimeout") && !testMode) || (testMode)));

            //If you're off your target distance by 2 cm or less, that's good enough : exit the while loop
            drive.stopMotors();
            drive.runWithEncoders();
        }
    }

    private ElapsedTime bangBangTimer = new ElapsedTime();

    private double getSensorFactor(AnalogSensor.Type type, int irId, double currDistance, double targetDistance) {
        double derivValue = 0;
        switch (type) {
            case SHORT_RANGE:
                derivValue = drive.shortIrRates[irId];
                break;
            case LONG_RANGE:
                derivValue = drive.longIrRates[irId];
                break;
            case SONAR:
                derivValue = drive.sonarRates[irId];
                break;
        }

        //Set up the derivative and proportional terms
        double deriv = derivValue * C.get().getDouble("SensorDeriv");
        double proportional = (currDistance - targetDistance) * C.get().getDouble("SensorProp");

        //telemetry.addData("derivative", deriv);
        //telemetry.addData("proportional", proportional);

        //Apply the controller
        double factor = (proportional + deriv);

        //factor = Range.clip(factor, -1, 1);
        return factor;
    }
    private double getSensorR(double targetGyro) {
        double r = drive.useGyro(targetGyro) * .75 + 5 * drive.gyroRate;
        r = r < .05 && r > 0 ? 0 : r;
        r = r > -.05 && r < 0 ? 0 : r;
        r = Range.clip(r, -1, 1);
        return r;
    }

    private void autoSensorDrive(double targetDistance) {
        telemetry.addData("ir", drive.shortIr[0]);
        telemetry.update();
        autoSensorDrive(0, 0, AnalogSensor.Type.SHORT_RANGE, false, targetDistance, 0, AnalogSensor.Type.SHORT_RANGE, true, 0, 0, 1, false, 2.5, 40, false);
    }

    public void jewelLeft() {
        try {
            drive.jewelLeft();

            waitNew(.25);

            drive.resetEncoders();
            drive.runWithEncoders();

            drive.jewelDown();

            /*autoRotate(14, Drive.FULL_SPEED);

            drive.jewelUp();

            autoRotate(0, Drive.FULL_SPEED);*/
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
        }
    }

    public void jewelRight() {
        try {
            drive.jewelRight();

            waitNew(.25);

            drive.resetEncoders();
            drive.runWithEncoders();

            drive.jewelDown();

            /*autoRotate(-14, Drive.FULL_SPEED);

            drive.jewelUp();

            autoRotate(0, Drive.FULL_SPEED);*/
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
        }
    }

    @Override
    public boolean opModeIsActive(){
        //telemetry.addData("time", timer.seconds());
        //telemetry.addData("Extendo", Drive.isExtendo);
        //telemetry.addData("Cryptobox", drive.cryptobox == null ? "" : drive.cryptobox.uiToString(false));
        //telemetry.update();

        if (intakeCount > 0 && this.intakeTimer.milliseconds() / 1000 < C.get().getDouble("intakeForwardTime")){
            drive.intakeLeft(1);
            drive.intakeRight(1);
        }else if (intakeCount > 0 && this.intakeTimer.milliseconds() / 1000 < (C.get().getDouble("intakeForwardTime") + C.get().getDouble("intakeBackTime"))){
            drive.intakeLeft(-1);
            drive.intakeRight(-1);
        }else if (intakeCount >= 0 && this.intakeTimer.milliseconds() / 1000 > (C.get().getDouble("intakeForwardTime") + C.get().getDouble("intakeBackTime"))){
            drive.intakeLeft(0);
            drive.intakeRight(0);
            if (intakeCount > 0){
                intakeCount--;
                intakeTimer.reset();
            }
        }if (readMark){
            vuMark = vuMarkIdentifier.getMarkInstant();
            if (vuMark != RelicRecoveryVuMark.UNKNOWN){
                readMark = false;
                com.vuforia.CameraDevice.getInstance().setFlashTorchMode(false);
            }
        }
        if (!done){
            drive.uTrackUpdate();
            done = drive.uTrack();
            if (!drive.stage.equals(GlyphPlacementSystem.Stage.RESET)) {
                drive.glyph.runToPosition(0);
            }
        } if (placeNew) {
            drive.internalIntakeLeft(1);
            drive.internalIntakeRight(1);
            if (drive.uTrackAtBottom && drive.getCollectedState() && timer.seconds() < 240000) {
                done = drive.uTrack();
                colorTime.reset();
                colorV = 100;
                telemetry.log().add("time: " + timer.seconds());
            } else if(drive.uTrackAtBottom && !drive.getCollectedState()) {
                drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());
                drive.glyph.runToPosition(0);
            } else if (!drive.uTrackAtBottom && colorTime.seconds() < 1.25) {
                double newV = drive.lineFollow[0].getV();
                colorV = (newV > .01 && newV < colorV) ? newV : colorV;
            } else if (!drive.uTrackAtBottom && colorTime.seconds() > 1.25 && colorV != 100) {
                Cryptobox.GlyphColor color = drive.getGlyphColor(colorV);
                drive.uTrackAutoTarget(color);
                telemetry.log().add("voltage: " + colorV + "  " + color);
                colorV = 100;
            }
            //telemetry.addData("Mark", vuMark);
        }
        return super.opModeIsActive();
    }

    public void openIntakes(HashMap<String, String> parameters) {
        this.intakeCount = 3;
        this.intakeTimer.reset();
    }
}
