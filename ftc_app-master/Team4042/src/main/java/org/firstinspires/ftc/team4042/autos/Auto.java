package org.firstinspires.ftc.team4042.autos;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team4042.drive.Direction;
import org.firstinspires.ftc.team4042.drive.Drive;
import org.firstinspires.ftc.team4042.drive.GlyphPlacementSystem;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;
import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses a file to figure out which instructions to run. CAN NOT ACTUALLY RUN INSTRUCTIONS.
 */
@Autonomous(name="Abstract Auto", group="autos")
public abstract class Auto extends LinearVisionOpMode {

    MecanumDrive drive = new MecanumDrive(true);
    //private VuMarkIdentifier vuMarkIdentifier = new VuMarkIdentifier();
    private RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.CENTER;

    private Telemetry.Log log;

    File file;

    ElapsedTime timer;

    private ArrayList<AutoInstruction> instructions = new ArrayList<>();

    private double startRoll;
    private double startPitch;


    public void setUp(MecanumDrive drive, String filePath) {
        timer = new ElapsedTime();
        this.drive = drive;

        log = telemetry.log();

        drive.initialize(telemetry, hardwareMap);

        //drive.glyph = new GlyphPlacementSystem(1, 0, hardwareMap, drive, false);

        //drive.setUseGyro(true);
        //telemetry.addData("glyph", drive.glyph.getTargetPositionAsString());

        //vuMarkIdentifier.initialize(telemetry, hardwareMap);

        log.add("Reading file " + filePath);
        file = new File("./storage/emulated/0/DCIM/" + filePath);

        loadFile();

        /*this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(900, 900));
        //enableExtension(Extensions.BEACON);
        enableExtension(Extensions.ROTATION);
        enableExtension(Extensions.CAMERA_CONTROL);
        rotation.setIsUsingSecondaryCamera(false);
        rotation.disableAutoRotate();
        rotation.setActivityOrientationFixed(ScreenOrientation.LANDSCAPE);
        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
        cameraControl.setAutoExposureCompensation();*/
    }

    /**
     * Reads from the file and puts the information into instructions
     */
    private void loadFile() {
        if (file == null) { return; } //Can't load a null file

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) { //Reads the lines from the file in order
                if (line.length() > 0) {
                    if (line.charAt(0) != '#') { //Use a # for a comment
                        HashMap<String, String> parameters = new HashMap<>();

                        //x:3 --> k = x, v = 3
                        String[] inputParameters = line.split(" ");
                        StringBuilder para = new StringBuilder("Parameter: ");
                        int i = 0;
                        while (i < inputParameters.length) {
                            String parameter = inputParameters[i];
                            int colon = parameter.indexOf(':');
                            String k = parameter.substring(0, colon);
                            String v = parameter.substring(colon + 1);
                            parameters.put(k, v); //Gets the next parameter and adds it to the list
                            para.append(k).append(":").append(v).append(" ");
                            i++;
                        }

                        log.add(para.toString());

                        //Stores those values as an instruction
                        AutoInstruction instruction = new AutoInstruction(parameters);
                        instructions.add(instruction);

                        telemetry.update();
                    }
                }
            }
            fileReader.close();
        } catch (Exception ex) {
            telemetry.addData("Error", "trying to load file");
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("error", sw.toString());

        }

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

    /**
     * Runs the list of instructions
     */
    public void runAuto() {
        gyro();
        drive.jewelUp();
        drive.resetEncoders();
        drive.setEncoders(true);
        drive.setVerbose(true);

        timer.reset();
        //Reads each instruction and acts accordingly
        int i = 0;
        while (i < instructions.size() && opModeIsActive()) {
            AutoInstruction instruction = instructions.get(i);
            String functionName = instruction.getFunctionName();
            HashMap<String, String> parameters = instruction.getParameters();
            log.add("function: " + functionName);
            switch (functionName) {
                case "d":
                    autoDrive(parameters);
                    break;
                case "doff":
                    autoDriveOff(parameters);
                    break;
                case "r":
                    autoRotate(parameters);
                    break;
                case "s":
                    autoSensorDrive(parameters);
                    break;
                case "up":
                    jewelUp(parameters);
                    break;
                case "jr":
                    knockRedJewel(parameters);
                    break;
                case "jb":
                    knockBlueJewel(parameters);
                    break;
                case "jleft":
                    knockLeftJewel(parameters);
                    break;
                case "jright":
                    knockRightJewel(parameters);
                    break;
                case "v":
                    getVuMark(parameters);
                    break;
                case "p":
                    placeGlyph(parameters);
                    break;
                case "a":
                    alignHorizontally(parameters);
                    break;
                default:
                    System.err.println("Unknown function called from file " + file);
                    break;
            }
            i++;
        }

        //autoDrive(new Direction(1, .5), Drive.FULL_SPEED, 1000);

        //autoSensorDrive(Direction.Forward, Drive.FULL_SPEED / 4, 7, drive.ir);

        //check sensor sums
        //robot starts facing right
        //scan vision patter
        //go to front of jewels
        //cv scan
        //knock off other jewel
        //head right
        //whisker sensor hits cryptobox
        //back up
        //repeat ^ until whisker disengages
        //move right until we see -^-^-| from ultrasonic
        //place block
        //detach and extend robot towards glyph
    }

    public void getVuMark(HashMap<String, String> parameters) {
        //vuMark = vuMarkIdentifier.getMark();
        telemetry.addData("vuMark", vuMark);
        telemetry.update();
    }

    /*
    public void placeGlyph(HashMap<String, String> parameters) {
        //The vumark placement system starts at (1, 0), which is the bottom of the center column
        if (vuMark.equals(RelicRecoveryVuMark.LEFT)) {
            drive.glyph.left();
        } else if (vuMark.equals(RelicRecoveryVuMark.RIGHT)) {
            drive.glyph.right();
        }

        if (!vuMark.equals(RelicRecoveryVuMark.UNKNOWN)) {
            drive.glyph.place();
        }

        telemetry.addData("glyph", drive.glyph.getTargetPositionAsString());
        telemetry.update();
    }
    */

    public void alignHorizontally(HashMap<String, String> parameters) {
        double prevMiddle = drive.shortIr[0].getCmAvg();
        double currMiddle;
        do {
            currMiddle = drive.shortIr[0].getCmAvg();
            if (Math.abs(prevMiddle - currMiddle) > 2) { //Moved too far left
                drive.driveXYR(.5, 1, 0, 0, false); //Move back right
            } else {
                drive.driveXYR(.5, -1, 0, 0, false); //Move left
            }
        } while (drive.shortIr[1].getCmAvg() > 15 && drive.shortIr[2].getCmAvg() > 15 && opModeIsActive());

        //When they're both non-infinite readings, stop
        drive.stopMotors();
    }

    public String getBallColor(Mat frame){
        log.add(frame.height() + " x " + frame.width());
        //Imgproc.resize(frame, frame, new Size(960, 720));
        telemetry.update();
        Rect left_crop = new Rect(new Point(215,585), new Point(380, 719));
        Rect right_crop = new Rect(new Point(460,585), new Point(620, 719));

        //Log.d("stupid", this.getFrameSize().width + " x " + this.getFrameSize().height);
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
        //TODO: MAKE THIS A USEFUL FUNCTION based on vuMark

        //drive.glyph.setHomeTarget();
        drive.setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.setVerticalDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        drive.glyph.setTarget(vuMark, 0);
        drive.stage = GlyphPlacementSystem.Stage.HOME;

        boolean done = false;

        do {
            drive.glyph.runToPosition();
            done = drive.uTrack(); //GETS STUCK IN THIS FUNCTION
        } while (opModeIsActive() && !done);
    }

    public void jewelUp(HashMap<String, String> parameters) {
        drive.jewelUp();
    }

    public void knockLeftJewel(HashMap<String, String> parameters) {
        jewelLeft();
    }

    public void knockRightJewel(HashMap<String, String> parameters) {
        jewelRight();
    }

    public void knockRedJewel(HashMap<String, String> parameters) {
        try {
            //String balls = getBallColor(vuMarkIdentifier.getFrameAsMat());
            String balls = getBallColor(getFrameRgba());
            telemetry.addData("ball orientation", balls);
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
        Mat mat = getFrameRgba();
        String balls = getBallColor(mat);
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
        Direction direction = new Direction(Double.parseDouble(parameters.get("x")), -Double.parseDouble(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));
        double targetTicks = Double.parseDouble(parameters.get("target"));
        double time = parameters.containsKey("time") ? Double.parseDouble(parameters.get("time")) : -1;
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
        while (opModeIsActive() && (!done && ((time != -1 && timer.seconds() <= time)) || (time == -1 && !done))) {
            //Keep going if (you're not done and the seconds are less than the target) or (you're not waiting for the timer and you're not done)
            done = drive.driveWithEncoders(direction, speed, targetTicks, useGyro, targetGyro);
            //telemetry.update();
        }
        drive.resetEncoders();
        drive.runWithEncoders();
    }

    private void autoDriveOff(HashMap<String, String> parameters) {
        Direction direction = new Direction(Double.parseDouble(parameters.get("x")), -Double.parseDouble(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));

        //Drive in the direction indicated
        autoDrive(direction, speed, 500, -1, false, 0);

        double roll;
        double pitch;

        do {
            drive.gyro.updateAngles();
            roll = drive.gyro.getRoll();
            pitch = drive.gyro.getPitch();
            autoDrive(direction, speed, 100, -1, false, 0);
        }
        while ((Math.abs(roll - startRoll) >= 3) ||
                (Math.abs(pitch - startPitch) >= 3) && opModeIsActive());
            //If you're too tipped forward/backwards or left/right, then keep driving
            //This effectively drives until you're off the balancing stone
    }

    public void autoRotate(HashMap<String, String> parameters) {
        double realR = Double.parseDouble(parameters.get("r"));

        double speed = Double.parseDouble(parameters.get("speed"));

        autoRotate(realR, speed);
    }

    /**
     * Drives in the given Rotation at the given speed until targetTicks is reached
     * @param realR The degree to rotate to
     * @param speed The speed to rotate at
     */
    private void autoRotate(double realR, double speed) {
        double gyro;
        do {
            gyro = drive.gyro.updateHeading();
            double diff = realR - gyro;
            telemetry.addData("gyro",gyro + " realR: " + realR + " diff: " + diff);
            if (diff > 270) { diff -= 360; }
            if (diff < -270) { diff += 360; }
            if (Math.abs(diff) > 2) {
                drive.driveXYR(speed, 0, 0, -.02 * diff, false, 0.008);
            }
        } while (Math.abs(gyro - realR) > 2 && opModeIsActive());

        drive.stopMotors();
        drive.resetEncoders();
        drive.runWithEncoders();
        telemetry.update();
    }

    public void autoSensorDrive(HashMap<String, String> parameters) {
        double speed = Double.parseDouble(parameters.get("speed"));

        double yTargetDistance = Double.parseDouble(parameters.get("ydistance"));
        int yIr = Integer.parseInt(parameters.get("yir"));
        boolean yLongIr = Boolean.parseBoolean(parameters.get("ylong"));

        if (parameters.containsKey("xdistance")) {
            double xTargetDistance = Double.parseDouble(parameters.get("xdistance"));
            int xIr = Integer.parseInt(parameters.get("xir"));
            boolean xLongIr = Boolean.parseBoolean(parameters.get("xlong"));
            autoSensorDrive(speed, xTargetDistance, xIr, xLongIr, yTargetDistance, yIr, yLongIr);
        }
        else {
            autoSensorDrive(speed, 0, 0, false, yTargetDistance, yIr, yLongIr);
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
    private void autoSensorDrive(double speed, double xTargetDistance, int xIrId, boolean xIsLongRange,
                                 double yTargetDistance, int yIrId, boolean yIsLongRange) {

        //autoDrive(direction, speed, targetTicks, -1, false, targetGyro);

        AnalogSensor xIr = xIsLongRange ? drive.longIr[xIrId] : drive.shortIr[xIrId];
        AnalogSensor yIr = yIsLongRange ? drive.longIr[yIrId] : drive.shortIr[yIrId];

        double xCurrDistance;
        double yCurrDistance;
        int i = 0;
        while (i < AnalogSensor.NUM_OF_READINGS && opModeIsActive()) {
            //read the IRs just to set them up
            xIr.addReading();
            yIr.addReading();
            i++;
        }

        double r = drive.useGyro(0) / 180;

        do {
            double speedFactor = speed;

            drive.updateRates();

            //Get the distances and derivative terms
            xCurrDistance = xIr.getCmAvg();
            yCurrDistance = yIr.getCmAvg();
            double xDerivValue = xIsLongRange ? drive.longIrRates[xIrId] : drive.shortIrRates[xIrId];
            double yDerivValue = yIsLongRange ? drive.longIrRates[yIrId] : drive.shortIrRates[yIrId];

            //Set up the derivative and proportional terms
            double xDeriv = xDerivValue * -.02;
            double xProportional = (xCurrDistance - xTargetDistance) * .1;

            double yDeriv = yDerivValue * -.02;
            double yProportional = (yCurrDistance - yTargetDistance) * .1;

            //Apply the controller
            double xFactor = (xDeriv + xProportional);
            double yFactor = (yDeriv + yProportional);
            telemetry.addData("yDerivValue", yDeriv);
            telemetry.addData("yCurrDistance - yTargetDistance", yProportional);
            telemetry.addData("y", yFactor * speedFactor);
            telemetry.update();

            //Actually drives
            drive.driveXYR(speedFactor, xFactor, yFactor, r, false);
        } while (((Math.abs(xTargetDistance - xCurrDistance) > 2) || true) && opModeIsActive());

        //If you're off your target distance by 2 cm or less, that's good enough : exit the while loop
        drive.stopMotors();

    }

    private void autoSensorDrive(double speed, double targetDistance) {
        telemetry.addData("ir", drive.shortIr[0]);
        telemetry.update();
        autoSensorDrive(speed, 0, 0, false, targetDistance, 0, false);
    }

    public void jewelLeft() {
        try {
            drive.resetEncoders();
            drive.runWithEncoders();
            ElapsedTime timer = new ElapsedTime();

            timer.reset();
            drive.jewelDown();

            while (timer.seconds() < 1) {
            }
            timer.reset();

            log.add("rotate left");

            //Moves the robot left
            autoRotate(7, Drive.FULL_SPEED/4);

            log.add("rotate right");

            autoRotate(0, Drive.FULL_SPEED/4);

            log.add("jewel up");

            drive.jewelUp();

            timer.reset();
            while (timer.seconds() < 1) {
            }
            //autoRotate(0, Drive.FULL_SPEED/4);
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
        }
    }

    public void jewelRight() {
        try {
            drive.resetEncoders();
            drive.runWithEncoders();
            ElapsedTime timer = new ElapsedTime();

            timer.reset();
            drive.jewelDown();

            while (timer.seconds() < 1) {
            }
            timer.reset();

            autoRotate(-7, Drive.FULL_SPEED/4);

            autoRotate(0, Drive.FULL_SPEED/4);

            drive.jewelUp();

            timer.reset();
            while (timer.seconds() < 1) {
            }
            //autoRotate(0, Drive.FULL_SPEED/4);
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
        }
    }
}