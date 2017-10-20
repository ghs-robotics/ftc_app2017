package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.code.Exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Auto {

    MecanumDrive drive;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Telemetry.Log log;

    File file;

    ArrayList<AutoInstruction> instructions = new ArrayList<>();

    public Auto(HardwareMap hardwareMap, MecanumDrive drive, Telemetry telemetry, String filePath) {
        this.drive = drive;
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        log = telemetry.log();

        log.add("Loading file");
        file = new File("./storage/emulated/0/DCIM/" + filePath);

        loadFile();
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
            double numLines = 0;
            while ((line = bufferedReader.readLine()) != null) { //Reads the lines from the file in order
                numLines++;
                log.add("Reading line " + numLines);
                telemetry.update();
                if (line.charAt(0) != '#') { //Use a # for a comment
                    HashMap<String, String> parameters = new HashMap<>();

                    //x:3 --> k = x, v = 3
                    String[] inputParameters = line.split(" ");
                    log.add("Parsing " + inputParameters.length + " parameters");
                    telemetry.update();
                    for (String parameter : inputParameters) {
                        int colon = parameter.indexOf(':');
                        String k = parameter.substring(0, colon);
                        String v = parameter.substring(colon + 1);
                        parameters.put(k, v); //Gets the next parameter and adds it to the list
                    }

                    log.add("Getting function");
                    telemetry.update();
                    String functionName = "";
                    switch (parameters.get("function")) {
                        case "d":
                            functionName = "autoDrive";
                            break;
                        case "r":
                            functionName = "autoRotate";
                            break;
                        case "s":
                            functionName = "autoSensorMove";
                            break;
                        default:
                            System.err.println("Unknown function called from file " + file);
                            break;
                    }
                    //Stores those values as an instruction
                    log.add("Adding instruction");
                    AutoInstruction instruction = new AutoInstruction(functionName, parameters);
                    instructions.add(instruction);

                    telemetry.update();
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
     * Runs the list of instructions
     */
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, telemetry, false);
        drive.setUseGyro(true);
        telemetry.update();

        //TODO: TEST THIS
        drive.resetEncoders();
        drive.setEncoders(true);

        //Reads each instruction and acts accordingly
        for (AutoInstruction instruction : instructions) {
            String functionName = instruction.getFunctionName();
            HashMap<String, String> parameters = instruction.getParameters();
            try {
                //Calls the function "functionName" with parameters "parameters"
                Method m = Auto.class.getMethod(functionName, parameters.getClass());
                m.invoke(functionName, parameters);
                telemetry.addData("Invoking function", functionName);
                telemetry.update();
            } catch (NoSuchMethodException ex) {
                telemetry.addData("error", "could not find function " + functionName);
            } catch (Exception ex) {
                telemetry.addData("error", "trying to invoke function " + functionName);
            }

        }

        //autoDrive(new Direction(1, .5), Drive.FULL_SPEED, 1000);

        //autoSensorMove(Direction.Forward, Drive.FULL_SPEED / 4, 7, drive.ir);

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

    public void autoDrive(HashMap<String, String> parameters) {
        Direction direction = new Direction(Integer.parseInt(parameters.get("x")), Integer.parseInt(parameters.get("y")));
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
        boolean done = false;
        while (!done) {
            done = drive.driveWithEncoders(direction, speed, targetTicks);
            telemetry.update();
        }
    }

    public void autoRotate(HashMap<String, String> parameters) {
        int r = Integer.parseInt(parameters.get("r"));
        Direction.Rotation rotation;
        if (r < 0) {
            rotation = Direction.Rotation.Counterclockwise;
        } else {
            rotation = Direction.Rotation.Clockwise;
        }

        double speed = Double.parseDouble(parameters.get("speed"));
        double targetTicks = Double.parseDouble(parameters.get("target"));
    }

    /**
     * Drives in the given Rotation at the given speed until targetTicks is reached
     * @param rotation The rotation to rotate in
     * @param speed The speed to rotate at
     * @param targetTicks The final distance to have travelled, in encoder ticks
     */
    private void autoRotate(Direction.Rotation rotation, double speed, double targetTicks) {
        boolean done = false;
        while (!done) {
            done = drive.rotateWithEncoders(rotation, speed, targetTicks);
            telemetry.update();
        }
    }

    public void autoSensorMove(HashMap<String, String> parameters) {
        Direction direction = new Direction(Integer.parseInt(parameters.get("x")), Integer.parseInt(parameters.get("y")));
        double speed = Double.parseDouble(parameters.get("speed"));
        double targetDistance = Double.parseDouble(parameters.get("target"));

        autoSensorMove(direction, speed, targetDistance);
    }

    /**
     * Drives in the given Direction until a sensor returns a given value
     * @param direction The direction to move in
     * @param speed The speed to move at
     * @param targetDistance The final distance to have travelled, in encoder ticks
     * @param ir The sensor to read a distance from
     */
    private void autoSensorMove(Direction direction, double speed, double targetDistance, AnalogSensor ir) {
        boolean done = false;
        while (!done) {
            done = drive.driveWithSensor(direction, speed, targetDistance, ir);
            telemetry.update();
        }
    }

    private void autoSensorMove(Direction direction, double speed, double targetDistance) {
        boolean done = false;
        while (!done) {
            done = drive.driveWithSensor(direction, speed, targetDistance, drive.ir);
            telemetry.update();
        }
    }
}