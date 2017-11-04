package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MecanumDrive extends Drive {

    //How much the robot is rotated when we start (as in, the wheels are in a diamond, not a square)
    //Used for not-field-oriented drive
    public static final int OFFSET = 0;

    public GlyphPlacementSystem glyph;

    private Servo jewelServo;

    private DcMotor intakeLeft;
    private DcMotor intakeRight;

    private CRServo inLServo;
    private CRServo inRServo;

    private DcMotor verticalDrive;

    private Servo grabbyBoi;
    private boolean handIsOpen = false;

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     */
    public MecanumDrive() {
        //Initialize motors and gyro
        super();
    }

    @Override
    public void initialize(Telemetry telemetry, HardwareMap hardwareMap) {
        jewelServo = hardwareMap.servo.get("jewel");
        jewelIn();
        super.initialize(telemetry, hardwareMap);

        this.grabbyBoi = hardwareMap.servo.get("hand");

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
        verticalDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //verticalDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public MecanumDrive(boolean verbose) {
        super(verbose);
    }

    public void toggleHand() {
        if (isHandOpen()) {
            closeHand();
        } else {
            openHand();
        }
        //The functions toggle the hand variable so we don't need to
    }

    public void openHand() {
        handIsOpen = true;
        grabbyBoi.setPosition(.57);
    }

    public void closeHand() {
        handIsOpen = false;
        grabbyBoi.setPosition(1);
    }

    public boolean isHandOpen() {
        return handIsOpen;
    }

    public void verticalDrive(double power) {
        verticalDrive.setPower(power);
    }

    public void verticalDriveMode(DcMotor.RunMode mode) {
        verticalDrive.setMode(mode);
    }

    public void verticalDrivePos(int position) {
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
        telemetry.addData("cr left servo", inLServo.getPower());
    }

    public void intakeRight(double power) {
        intakeRight.setPower(power);
        inRServo.setPower(power);
        telemetry.addData("cr right servo", inRServo.getPower());
    }

    public void jewelLeft() {
        try {
            super.resetEncoders();
            super.runWithEncoders();
            ElapsedTime timer = new ElapsedTime();

            timer.reset();
            jewelDown();

            while (timer.seconds() < 1) {
            }
            timer.reset();

            //Rotates the robot left
            while (!driveWithEncoders(Direction.Backward, Drive.FULL_SPEED, 200)) {
            }

            while (timer.seconds() < 1) {
            }
            timer.reset();

            jewelUp();

            while (timer.seconds() < 1) {
            }
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
        }
    }

    public void jewelRight() {
        try {
            super.resetEncoders();
            super.runWithEncoders();
            ElapsedTime timer = new ElapsedTime();

            timer.reset();
            jewelDown();

            while (timer.seconds() < 1) {
            }
            timer.reset();

            //Rotates the robot left
            while (!driveWithEncoders(Direction.Forward, Drive.FULL_SPEED, 200)) {
            }

            while (timer.seconds() < 1) {
            }
            timer.reset();

            jewelUp();

            while (timer.seconds() < 1) {
            }
        } catch (NullPointerException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("NullPointerException", sw.toString());
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

    public void jewelAdjust(double adjustAmt) {
        double currPos = jewelServo.getPosition();

        jewelServo.setPosition(Range.clip(currPos + adjustAmt, 0, 1));
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, Gamepad gamepad2, double speedFactor) {
        super.setEncoders(useEncoders);

        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double r = gamepad1.right_stick_x;

        driveXYR(speedFactor, x, y, r, useGyro);
    }

    /**
     * Drives an inputted amount
     * @param speedFactor the amount to scale the drive by
     * @param x x component
     * @param y y component
     * @param r rotate component
     */
    public void driveXYR(double speedFactor, double x, double y, double r, boolean useGyro) {
        double[] speedWheel = new double[4];

        //Deadzone for joysticks
        x = super.deadZone(x);
        y = super.deadZone(y);
        r = super.deadZone(r);

        if (verbose) {
            telemetry.addData("x", x);
            telemetry.addData("y", y);
            telemetry.addData("r", r);
        }

        double heading = OFFSET;
        if (useGyro) {
            heading = super.gyro.updateHeading();
            telemetry.addData("heading", heading);
        }

        /*
        Adjust x, y for gyro values
         */
        double gyroRadians = Math.toRadians(heading);
        double xPrime = x * Math.cos(gyroRadians) + y * Math.sin(gyroRadians);
        double yPrime = -x * Math.sin(gyroRadians) + y * Math.cos(gyroRadians);

        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] = -xPrime - yPrime - r;
        speedWheel[1] = xPrime - yPrime + r;
        speedWheel[2] = -xPrime - yPrime + r;
        speedWheel[3] = xPrime - yPrime - r;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel, speedFactor);
    }

    /**
     * Rotates the robot to the target location, returning true while it has not
     * reached the target then false once it has. Also speeds up and slows down
     *
     * @param targetTicks the tick count you want to reach with at least one of your motors
     * @param speed speed at which to travel
     * @param rotation which way to rotate
     * @return returns if it is completed (true if has reached target, false if it hasn't)
     */
    public boolean rotateWithEncoders(Direction.Rotation rotation, double speed, double targetTicks) throws IllegalArgumentException {
        //telemetry data
        telemetry.addData("Left Back", motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front", motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back", motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front", motorRightFront.getCurrentPosition());

        double scaledSpeed = setUpSpeed(speed, targetTicks);
        if (scaledSpeed == Math.PI) { //The target's been reached
            return true;
        }
        //if it hasn't reached the target (it won't have returned yet),
        // drive at the given speed (possibly scaled b/c of first and last fourth), and return false
        scaledSpeed = Range.clip(scaledSpeed, 0, FULL_SPEED);

        if (rotation.equals(Direction.Rotation.Clockwise) || rotation.equals(Direction.Rotation.Counterclockwise)) { //Rotating
            //Don't use the gyro because the robot is MEANT to be turning
            driveXYR(FULL_SPEED, 0, 0, -scaledSpeed, false);
        }
        else { //Null or other problematic directions
            throw new IllegalArgumentException("Illegal direction inputted! Direction was: " + rotation);
        }
        return false;
    }

    /**
     * Runs the robot to the target location, returning true while it has not
     * reached the target then false once it has. Also speeds up and slows down
     *
     * @param targetTicks the tick count you want to reach with at least one of your motors
     * @param speed speed at which to travel
     * @param direction which direction to go
     * @return returns if it is completed (true if has reached target, false if it hasn't)
     */
    public boolean driveWithEncoders(Direction direction, double speed, double targetTicks) throws IllegalArgumentException{
        //telemetry data
        log.add("x " + direction.getX());
        log.add("y " + direction.getY());

        double scaledSpeed = setUpSpeed(speed, targetTicks);
        if (scaledSpeed == Math.PI) { //The target's been reached
            return true;
        }
        //if it hasn't reached the target (it won't have returned yet),
        // drive at the given speed (possibly scaled b/c of first and last fourth), and return false
        scaledSpeed = Range.clip(scaledSpeed, 0, FULL_SPEED);

        double r = 0;
        if (useGyro) {
            r = useGyro();
        }
        log.add("r " + r);

        //Drives at x
        driveXYR(FULL_SPEED, direction.getX() * scaledSpeed, direction.getY() * scaledSpeed, r, false);
        return false;
    }

    /**
     * A helper function that scales speed if you're in the first or last fourth of the target encoder values
     * @param speed The inputted speed
     * @param targetTicks The final ticks for the encoders
     * @return Returns the speed, scaled, or Math.PI if you've already reached the value
     */
    private double setUpSpeed(double speed, double targetTicks) {
        //finds the maximum of all encoder counts
        double currentTicks = super.max(motorLeftBack.getCurrentPosition(),
                motorLeftFront.getCurrentPosition(),
                motorRightBack.getCurrentPosition(), motorRightFront.getCurrentPosition());
        //if it has not reached the target, it tests if it is in the
        // last or first fourth of the way there, and
        // scales the speed such that it speeds up and slows down
        // to BASE_SPEED as it reaches the target
        if (currentTicks <= targetTicks) {
            double difference = targetTicks / 4;
            if (currentTicks / targetTicks > .75) { //last fourth
                difference = targetTicks - currentTicks;
            } else if (currentTicks / targetTicks < .25) { //first fourth
                difference = currentTicks;
            }
            speed *= (BASE_SPEED + (difference / (targetTicks / 4)) * (1 - BASE_SPEED));
        } else {
            //if it has reached target, stop moving, reset encoders, and return PI
            stopMotors(); //stops the motors
            this.resetEncoders();
            this.runWithEncoders();
            return Math.PI;
        }
        return speed;
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

    /**
     * Stops all motors
     */
    public void stopMotors() {
        driveXYR(STOP_SPEED, 0, 0, 0, false);
    }

    /**
     * CODE FROM HERE DOWN IS AN ATTEMPT TO IMPLEMENT DYLAN'S DRIVE ALGORITHM
     */
    double lastX;
    double lastY;
    double lastR;
    double lastTime = System.currentTimeMillis();

    public void drive(boolean useEncoders, Gamepad gamepad1, double speedFactor) {
        super.setEncoders(useEncoders);

        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double r = gamepad1.right_stick_x;

        x = super.deadZone(x);
        y = super.deadZone(y);
        r = super.deadZone(r);

        double time = System.currentTimeMillis();
        double dX = (lastX - x) / (lastTime - time);
        double dY = (lastY - y) / (lastTime - time);
        double dR = (lastR - r) / (lastTime - time);

        double distanceFromCenter = 3;
        double rollerAngle = 0; //Math.PI / 4; //45 degrees, in radians

        double[] speedWheel = new double[4];

        try {
            for (int i = 0; i < speedWheel.length; i++) {
                double angleShaft = Math.PI / 4 + (Math.PI / 2) * i;

                double[][] xy = new double[1][2];
                xy[0][0] = dX;
                xy[0][1] = dY;

                double[][] sincos1 = new double[1][2];
                sincos1[0][0] = Math.sin(r + angleShaft + Math.PI / 2);
                sincos1[0][1] = Math.cos(r + angleShaft + Math.PI / 2);

                double[][] sincos2 = new double[2][1];
                sincos2[0][0] = Math.sin(r + angleShaft + rollerAngle);
                sincos2[1][0] = Math.cos(r + angleShaft + rollerAngle);

                speedWheel[i] =
                        multiplyMatrices(
                                addMatrices(
                                        xy,
                                        multiplyConstant(
                                                distanceFromCenter * dR,
                                                sincos1)
                                ),
                                sincos2
                        )[0][0] / Math.sin(rollerAngle);
            }

            super.setMotorPower(speedWheel, speedFactor);

            lastX = x;
            lastY = y;
            lastR = r;
            lastTime = time;
        } catch (ArrayIndexOutOfBoundsException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            telemetry.addData("ex", exceptionAsString);
        }
    }

    /**
     * Adds arrays matrix1 and matrix2 by standard rules of matrix addition
     * @param matrix1 The first matrix
     * @param matrix2 The second matrix
     * @return The result matrix
     */
    public double[][] addMatrices(double[][] matrix1, double[][] matrix2) {
        if ((matrix1.length != matrix2.length) || (matrix1[0].length != matrix2[0].length)) {
            throw(new IllegalArgumentException("Illegal matrix dimensions for addition."));
        }
        else {
            telemetry.addData("matrix1", matrix1.length + "x" + matrix1[0].length);
            telemetry.addData("matrix2", matrix2.length + "x" + matrix2[0].length);

            double[][] result = new double[matrix1.length][matrix1[0].length];
            for (int i = 0; i < matrix1.length; i++) {
                for (int j = 0; j < matrix1[0].length; i++) {
                    telemetry.addData("i", i);
                    telemetry.addData("j", j);

                    double one = matrix1[i][j];
                    double two = matrix2[i][j];
                    result[i][j] = one + two;
                }
            }
            return result;
        }
    }

    /**
     * Multiplies arrays matrix1 and matrix2 by standard rules of matrix multiplication
     * @param matrix1 The first matrix
     * @param matrix2 The second matrix
     * @return The result matrix
     */
    public double[][] multiplyMatrices(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != matrix2[0].length) {
            throw(new IllegalArgumentException("Illegal matrix dimensions for multiplication."));
        }
        else {
            double[][] result = new double[matrix1[0].length][matrix2.length];
            for (int i = 0; i < matrix1.length; i++) {
                for (int j = 0; j < matrix2[0].length; j++) {
                    for (int k = 0; k < matrix1[0].length; k++) {
                        result[i][j] += matrix1[i][k] * matrix2[k][j];
                    }
                }
            }
            return result;
        }
    }

    /**
     * Multiplies a matrix by a constant
     * @param constant The constant to multiply by
     * @param matrix The matrix to multiply
     * @return The result matrix
     */
    public double[][] multiplyConstant(double constant, double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = matrix[i][j] * constant;
            }
        }

        return result;
    }
}
