package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

public class MecanumDrive extends Drive {

    //How much the robot is rotated when we start (as in, the wheels are in a diamond, not a square)
    //Used for not-field-oriented drive
    public static final int OFFSET = 0;

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     */
    public MecanumDrive() {
        //Initialize motors and gyro
        super();
    }

    /**
     * uses joystick inputs to set motor speeds for mecanum drive
     *
     * @param useEncoders determines whether or not the motors use encoders
     */
    public void drive(boolean useEncoders, Gamepad gamepad1, double speedFactor, boolean invert) {
        super.setEncoders(useEncoders);

        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y; //Y is the opposite direction of what's intuitive: forward is -1, backwards is 1
        double r = gamepad1.right_stick_x;

        if (invert) {
            x = -1 * x;
            y = -1 * y;
        }

        driveXYR(speedFactor, x, y, r);
    }

    /**
     * Drives an inputted amount
     *
     * @param speedFactor the amount to scale the drive by
     * @param x           x component
     * @param y           y component
     * @param r           rotate component
     */
    public void driveXYR(double speedFactor, double x, double y, double r) {
        double[] speedWheel = new double[4];

        //Deadzone for joysticks
        x = super.deadZone(x);
        y = super.deadZone(y);
        r = super.deadZone(r);

        telemetry.addData("speed wheel 0", speedWheel[0]);
        telemetry.addData("speed wheel 1", speedWheel[1]);
        telemetry.addData("speed wheel 2", speedWheel[2]);
        telemetry.addData("speed wheel 3", speedWheel[3]);
        /*
        Adjust x, y for gyro values
         */
        //Sets relative wheel speeds for mecanum drive based on controller inputs
        speedWheel[0] = -x - y - r;
        speedWheel[1] = x - y + r;
        speedWheel[2] = -x - y + r;
        speedWheel[3] = x - y - r;

        //sets the wheel powers to the appropriate ratios
        super.setMotorPower(speedWheel, speedFactor);
    }

    /**
     * Moves at a direction until the AnalogSensor returns the desired input
     *
     * @param direction      The direction to move in
     * @param speed          The speed to move at
     * @param targetDistance The distance to end up at
     * @param ir             The AnalogSensor with which to get the distance
     * @return Whether you've reached the target point
     */
    public boolean driveWithSensor(Direction direction, double speed, double targetDistance, double targetTicks, AnalogSensor ir) {
        //Ping 10 times and average the results
        /*double sum = 0;
        for (int i = 0; i < 10; i++) { sum += ir.getCmAvg(); }
        double currDistance = sum / 10;*/

        //TODO: THIS FUNCTION IS ALL WRONG

        double currDistance = ir.getCmAvg();
        if (currDistance == -1) {
            telemetry.addData("Error", "Couldn't find ultrasonic");
            return true;
        } else {

            telemetry.addData("currDistance", currDistance);
            telemetry.addData("Reached target", Math.abs(targetDistance - currDistance) > 0.5);
            telemetry.addData("x", direction.getX());
            telemetry.addData("y", direction.getY());
            telemetry.addData("r", 0);

            if (((direction.getY() >= 0) && (currDistance - 3 < targetDistance)) || //driving forwards and reached distance (0.5 inch tolerance)
                    ((direction.getY() < 0) && (currDistance + 3 > targetDistance))) { //driving backwards and reached distance (0.5 inch tolerance)
                stopMotors();
                return true;
            } else { //haven't reached point yet
                driveXYR(speed, direction.getX(), direction.getY(), 0);
                return false;
            }
        }
    }

    /**
     * Rotates the robot to the target location, returning true while it has not
     * reached the target then false once it has. Also speeds up and slows down
     *
     * @param targetTicks the tick count you want to reach with at least one of your motors
     * @param speed       speed at which to travel
     * @param rotation    which way to rotate
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
            driveXYR(FULL_SPEED, 0, 0, -scaledSpeed);
        } else { //Null or other problematic directions
            throw new IllegalArgumentException("Illegal direction inputted! Direction was: " + rotation);
        }
        return false;
    }

    /**
     * Runs the robot to the target location, returning true while it has not
     * reached the target then false once it has. Also speeds up and slows down
     *
     * @param targetTicks the tick count you want to reach with at least one of your motors
     * @param speed       speed at which to travel
     * @param direction   which direction to go
     * @return returns if it is completed (true if has reached target, false if it hasn't)
     */
    public boolean driveWithEncoders(Direction direction, double speed, double targetTicks) throws IllegalArgumentException {
        //telemetry data
        //log.add("x " + direction.getX());
        //log.add("y " + direction.getY());
        telemetry.addData("Left Back", motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front", motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back", motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front", motorRightFront.getCurrentPosition());
        telemetry.update();

        double scaledSpeed = setUpSpeed(speed, targetTicks);
        if (scaledSpeed == Math.PI) { //The target's been reached
            stopMotors();
            return true;
        }
        //if it hasn't reached the target (it won't have returned yet),
        // drive at the given speed (possibly scaled b/c of first and last fourth), and return false
        scaledSpeed = Range.clip(scaledSpeed, 0, FULL_SPEED);

        //Drives at x
        driveXYR(FULL_SPEED, direction.getX() * scaledSpeed, direction.getY() * scaledSpeed, 0);
        return false;
    }

    /**
     * A helper function that scales speed if you're in the first or last fourth of the target encoder values
     *
     * @param speed       The inputted speed
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
        // to BASE_SPEED as it reaches the target+
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
            this.runWithoutEncoders();
            return Math.PI;
        }
        return speed;
    }

    /**
     * Stops all motors
     */
    public void stopMotors() {
        driveXYR(STOP_SPEED, 0, 0, 0);
    }
}