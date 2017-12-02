package org.firstinspires.ftc.team4042.autos;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team4042.drive.Direction;
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

    MecanumDrive drive = new MecanumDrive(false);
    private VuMarkIdentifier vuMarkIdentifier = new VuMarkIdentifier();
    private RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;

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

        vuMarkIdentifier.initialize(telemetry, hardwareMap);

        log.add("Reading file " + filePath);
        file = new File("./storage/emulated/0/DCIM/" + filePath);

        loadFile();

        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(900, 900));
        //enableExtension(Extensions.BEACON);
        enableExtension(Extensions.ROTATION);
        enableExtension(Extensions.CAMERA_CONTROL);
        rotation.setIsUsingSecondaryCamera(false);
        rotation.disableAutoRotate();
        rotation.setActivityOrientationFixed(ScreenOrientation.LANDSCAPE);
        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
        cameraControl.setAutoExposureCompensation();
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
                        for (String parameter : inputParameters) {
                            int colon = parameter.indexOf(':');
                            String k = parameter.substring(0, colon);
                            String v = parameter.substring(colon + 1);
                            parameters.put(k, v); //Gets the next parameter and adds it to the list
                            para.append(k).append(":").append(v).append(" ");
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
        drive.setVerbose(false);

        timer.reset();
        //Reads each instruction and acts accordingly
        for (AutoInstruction instruction : instructions) {
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
                default:
                    System.err.println("Unknown function called from file " + file);
                    break;
            }
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
        vuMark = vuMarkIdentifier.getMark();
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

    public String getBallColor(Mat frame){
        log.add(frame.height() + " x " + frame.width());
        Imgproc.resize(frame, frame, new Size(960, 720));
        telemetry.update();
        Rect left_crop = new Rect(new Point(215,585), new Point(380, 719));
        Rect right_crop = new Rect(new Point(460,585), new Point(620, 719));

        Log.d("stupid", this.getFrameSize().width + " x " + this.getFrameSize().height);
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
    }

    public void jewelUp(HashMap<String, String> parameters) {
        drive.jewelUp();
    }

    public void knockLeftJewel(HashMap<String, String> parameters) {
        drive.jewelLeft();
    }

    public void knockRightJewel(HashMap<String, String> parameters) {
        drive.jewelRight();
    }

    public void knockRedJewel(HashMap<String, String> parameters) {
        try {
            String balls = getBallColor(getFrameRgba());
            discardFrame();
            telemetry.addData("ball orientation", balls);
            switch (balls) {
                case "red":
                    drive.jewelLeft();
                    break;
                case "blue":
                    drive.jewelRight();
                    break;
                case "red, blue":
                    drive.jewelLeft();
                    break;
                case "blue, red":
                    drive.jewelRight();
                    break;
                case ", blue":
                    drive.jewelLeft();
                    break;
                case ", red":
                    drive.jewelRight();
                    break;
            }
        } catch (CvException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("CvException", sw.toString());
        }
    }

    public void knockBlueJewel(HashMap<String, String> parameters) {
        String balls = getBallColor(getFrameRgba());
        telemetry.addData("ball orientation", balls);
        switch (balls) {
            case "red":
                drive.jewelRight();
                break;
            case "blue":
                drive.jewelLeft();
                break;
            case "red, blue":
                drive.jewelRight();
                break;
            case "blue, red":
                drive.jewelLeft();
                break;
            case ", blue":
                drive.jewelRight();
                break;
            case ", red":
                drive.jewelLeft();
                break;
        }
    }

    public void autoDrive(HashMap<String, String> parameters) {
        Direction direction = new Direction(Double.parseDouble(parameters.get("x")), -Double.parseDouble(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));
        double targetTicks = Double.parseDouble(parameters.get("target"));

        autoDrive(direction, speed, targetTicks);
    }

    /**
     * Drives in the given Direction at the given speed until targetTicks is reached
     * @param direction The direction to head in
     * @param speed The speed to move at
     * @param targetTicks The final distance to have travelled, in encoder ticks
     */
    private void autoDrive(Direction direction, double speed, double targetTicks) {
        //log.add("autoDrive invoked with direction " + direction + " speed " + speed + " targetTicks " + targetTicks);
        boolean done = false;
        while (!done && opModeIsActive()) {
            done = drive.driveWithEncoders(direction, speed, targetTicks);
            //telemetry.update();
        }
    }

    private void autoDriveOff(HashMap<String, String> parameters) {
        Direction direction = new Direction(Double.parseDouble(parameters.get("x")), -Double.parseDouble(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));

        autoDrive(direction, speed, 500);

        double roll;
        double pitch;

        do {
            drive.gyro.updateAngles();
            roll = drive.gyro.getRoll();
            pitch = drive.gyro.getPitch();
            autoDrive(direction, speed, 100);
        }
        while ((Math.abs(roll - startRoll) >= 3) ||
                (Math.abs(pitch - startPitch) >= 3));
            //If too tipped forward/backwards
            //or left/right
            //keep driving
    }

    public void autoRotate(HashMap<String, String> parameters) {
        double realR = Double.parseDouble(parameters.get("r"));

        double speed = Double.parseDouble(parameters.get("speed"));

        double realGyro = drive.gyro.updateHeading();

        do {
            double gyro = realGyro;
            double r = realR;

            while (r < 0) { r += 360; }
            while (gyro < 0) { gyro += 360; }
            double d = Math.abs(r - gyro); //Larger the further you are from your target
            if (d > 180) { d = 360 - d; }
            //telemetry.addData("d", d + " speed + d/720 " + (speed/2 + d/360));
            telemetry.addData("d", d + " speed - 5/d " + (speed - 5/d));
            if (realGyro > realR) {
                //The further you are from your target, the faster you should move
                //drive.driveXYR(speed/2 + d/720, 0, 0, -1, false);
                drive.driveXYR(speed - 5/d, 0, 0, -1, false);
            } else {
                //drive.driveXYR(speed/2 + d/720, 0, 0, 1, false);
                drive.driveXYR(speed - 5/d, 0, 0, -1, false);
            }

            realGyro = drive.gyro.updateHeading();
        } while (Math.abs(realGyro - realR) > 5 && opModeIsActive());

        drive.stopMotors();
    }

    /**
     * Drives in the given Rotation at the given speed until targetTicks is reached
     * @param rotation The rotation to rotate in
     * @param speed The speed to rotate at
     * @param targetTicks The final distance to have travelled, in encoder ticks
     */
    private void autoRotate(Direction.Rotation rotation, double speed, double targetTicks) {
        boolean done = false;
        while (!done && opModeIsActive()) {
            done = drive.rotateWithEncoders(rotation, speed, targetTicks);
            telemetry.update();
        }
    }

    public void autoSensorDrive(HashMap<String, String> parameters) {
        Direction direction = new Direction(Double.parseDouble(parameters.get("x")), Double.parseDouble(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));
        double targetDistance = Double.parseDouble(parameters.get("distance"));
        double targetTicks = Double.parseDouble(parameters.get("target"));

        autoSensorDrive(direction, speed, targetDistance, targetTicks);
    }

    /**
     * Drives in the given Direction until a sensor returns a given value
     * @param direction The direction to move in
     * @param speed The speed to move at
     * @param targetDistance The final distance to have travelled, in encoder ticks
     * @param ir The sensor to read a distance from
     */
    private void autoSensorDrive(Direction direction, double speed, double targetDistance, double targetTicks, AnalogSensor ir) {

        autoDrive(direction, speed, targetTicks);

        double currDistance = ir.getCmAvg();
        if (currDistance == -1) {
            telemetry.addData("Error", "Couldn't find ultrasonic");
        } else {
            double r = drive.useGyro()/180;

            if (drive.verbose) {
                telemetry.addData("currDistance", currDistance);
                telemetry.addData("Reached target", Math.abs(targetDistance - currDistance) > 2);
                telemetry.addData("x", direction.getX());
                telemetry.addData("y", direction.getY());
                telemetry.addData("r", r);
                telemetry.update();
            }

            //If you're off your target by more than 2 cm, try to adjust
            while ((Math.abs(targetDistance - currDistance) > 2) && opModeIsActive()) {
                if (((targetDistance > currDistance) && direction.isBackward()) ||
                        ((targetDistance < currDistance) && direction.isForward())) { //If you're not far enough, keep driving
                    drive.driveWithEncoders(direction, .25, 100);
                } else if (((targetDistance > currDistance) && direction.isForward()) ||
                        ((targetDistance < currDistance) && direction.isBackward())) { //If you're too far, drive backwards slightly
                    drive.driveWithEncoders(direction, -.25, 100);
                }
                currDistance = ir.getCmAvg();
            }

            //If you're off your target distance by 2 cm or less, that's good enough : exit the while loop
            drive.stopMotors();
        }
    }

    private void autoSensorDrive(Direction direction, double speed, double targetDistance, double targetTicks) {
        autoSensorDrive(direction, speed, targetDistance, targetTicks, drive.shortIr[0]);
    }
}