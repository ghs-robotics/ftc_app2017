package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.code.Exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Auto {

    MecanumDrive drive;
    HardwareMap hardwareMap;
    Telemetry telemetry;

    File file;

    ArrayList<AutoInstruction> instructions = new ArrayList<>();

    public Auto(HardwareMap hardwareMap, Telemetry telemetry, String filePath) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        file = new File(filePath);
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
            while ((line = bufferedReader.readLine()) != null) { //Reads the lines from the file in order
                if (line.charAt(0) != '#') { //Use a # for a comment
                    int space1 = line.indexOf(" ");
                    int space2 = line.indexOf(" ", space1 + 1);
                    int space3 = line.indexOf(" ", space2 + 1);
                    int space4 = line.indexOf(" ", space3 + 1);

                    //Gets the x, y, and speed, assuming they're separated by a space
                    String type = line.substring(0, space1);
                    String x = line.substring(space1 + 1, space2);
                    String y = line.substring(space2 + 1, space3);
                    String speed = line.substring(space3 + 1, space4);
                    String time = line.substring(space4 + 1);

                    //Stores those values as an instruction
                    AutoInstruction instruction = new AutoInstruction(x, y, speed, time, type);
                    instructions.add(instruction);
                }
            }
            fileReader.close();
        } catch (Exception ex) {
            telemetry.addData("Error", "trying to load file");
        }

    }

    /**
     * Runs the list of instructions
     */
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, telemetry, false, true);
        telemetry.update();

        //TODO: TEST THIS
        drive.resetEncoders();
        drive.setEncoders(true);

        //Reads each instruction and acts accordingly
        for (AutoInstruction instruction : instructions) {
            switch (instruction.getType()) {
                case 'd': //Dead-reckoning drive
                    autoDrive(instruction.getDirection(), instruction.getSpeed(), instruction.getTime());
                    break;
                case 'r': //Gyro rotation (x = -1 for counter-clockwise; 1 for clockwise) until distance
                    if (instruction.getDirection().getX() > 0) {
                        autoRotate(Direction.Rotation.Clockwise, instruction.getSpeed(), instruction.getTime());
                    }
                    else if (instruction.getDirection().getX() < 0) {
                        autoRotate(Direction.Rotation.Counterclockwise, instruction.getSpeed(), instruction.getTime());
                    }
                    break;
                case 's': //Sensor drive until ir returns distance
                    autoSensorMove(instruction.getDirection(), instruction.getSpeed(), instruction.getTime(), drive.ir);
                    break;
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
}