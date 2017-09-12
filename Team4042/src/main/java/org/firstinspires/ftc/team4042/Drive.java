package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drive {
    //Initializes a factor for the speed of movement to a position
    public static final double BASE_SPEED = .3;
    //How much the robot is rotated when we start (as in, the wheels are in a diamond, not a square)
    public static final int OFFSET = 225;
    //For the button pusher

    /***instance variables***/

    //stores the components of the movement vector the robot will use
    double xComp;
    double yComp;
    double rot;

    //stores gyro data
    int oldGyro = OFFSET;
    //int newGyro = OFFSET;

    //Scales the rotation speed by this factor
    static final double ROT_RATIO = .7;

    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    Gyro gyro;
    Telemetry telemetry;

    /**
     * Constructor for Drive, it creates the motors and the gyro objects
     *
     * @param hardwareMap hardware map of robot so Drive can use motors
     * @param gyroName name of gyro in config file to initialize it
     * @param telemetry telemetry so Drive can send data to the phone
     */
    public Drive(HardwareMap hardwareMap, String gyroName, Telemetry telemetry) {
        //Initialize motors and gyro
        motorLeftBack = hardwareMap.dcMotor.get("back left");
        motorLeftFront = hardwareMap.dcMotor.get("front left");
        motorRightBack = hardwareMap.dcMotor.get("back right");
        motorRightFront = hardwareMap.dcMotor.get("front right");

        gyro = new Gyro(hardwareMap, gyroName);
        this.telemetry = telemetry;
    }

    /**
     * sets all the motors to run using the PID algorithms and encoders
     */
    public void runWithEncoders(){
        motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * sets all the motors to run NOT using the PID algorithms and encoders
     */
    public void runWithoutEncoders(){
        motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * resents the encoder counts of all motors
     */
    public void resetEncoders() {
        motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Runs the robot to the target location, returning true while it has not
     * reached the target then false once it has. Also speeds up and slows down
     *
     * @param targetTicks the tick count you want to reach with at least one of your motors
     * @param speed speed at which to travel
     * @return returns if it is still NOT completed yet (true if hasn't reached target, false if it has)
     */
    public boolean driveToPosition(double targetTicks, double speed) {
        telemetry.addData("Left Back: ", motorLeftBack.getCurrentPosition());
        telemetry.addData("Left Front: ", motorLeftFront.getCurrentPosition());
        telemetry.addData("Right Back: ", motorRightBack.getCurrentPosition());
        telemetry.addData("Right Front: ", motorRightFront.getCurrentPosition());

        //finds the maximum of all encoder counts
        double currentTicks = max(motorLeftBack.getCurrentPosition(),
                motorLeftFront.getCurrentPosition(),
                motorRightBack.getCurrentPosition(), motorRightFront.getCurrentPosition());
        //if it has not reached the target, it tests if it is in the
        // last or first fifth of the way there, and
        //scales the speed such that it speeds up and slows down
        // to BASE_SPEED as it reaches the target
        if (currentTicks <= targetTicks) {
            double difference = targetTicks / 4;
            if (currentTicks / targetTicks > .75) { //last fourth
                difference = targetTicks - currentTicks;
            } else if (currentTicks / targetTicks < .25) { //first fourth
                difference = currentTicks;
            }
            speed *= (BASE_SPEED + (difference / (targetTicks / 4)) * (1 - BASE_SPEED));
            //if it has reached target, stop moving, reset encoders, and return false
        } else {
            drive(0, false, true);
            this.resetEncoders();
            this.runWithEncoders();
            return false;
        }
        //if it hasn't reached the target, drive at the given speed, autocorrect any shifting
        // off the path, and return true
        speed = Range.clip(speed, 0, 1);
        useGyro();
        drive(speed, true, true);
        return true;
    }

    /**
     * resets the gyro value to the OFFSET value
     */
    public void reset(int position) {
        gyro.reset(position);
        oldGyro = (OFFSET + position) % 360;
    }

    /**
     * uses the gyro, first reading from the gyro then setting rotation to
     * auto correct if the robot gets off
     */
    public void useGyro() {
        gyro.readZ();
        double r = 0;
        telemetry.addData("gyro", gyro.getAngleZ());
        if (rot == 0) { //if you're not supposed to have rotated, make sure you actually haven't
            double gyroDiff = gyro.getAngleZ() - oldGyro; //hopefully is zero
            telemetry.addData("oldGyro: ", oldGyro);
            telemetry.addData("gyroDiff: ", gyroDiff);
            //If you're moving forwards and you drift, this should correct it.
            //Accounts for if you go from 1 degree to 360 degrees
            // which is only a difference of one degree,
            //but the bot thinks that's 359 degree difference
            //Also scales -180 to 180 ==> -1 to 1
            if (gyroDiff < -180) {
                r = (180 + gyroDiff) / 180;
            }else if (gyroDiff > 180) {
                r = (gyroDiff - 180) / 180;
            }else {
                r = (gyroDiff) / 180;
            }
        } else {
            //If the bot is turning, then update the gyro data in drive again
            oldGyro = gyro.getAngleZ();
            r = rot;
        }

        rot = Range.clip(r, -1, 1);
    }

    /**
     * main drive function: drives all omni drive motors at specified speeds
     *
     * @param speed speed at which to move robot, 0 to 1
     * @param useEncoders enables or disables PID and encoder control
     */
    public void drive(double speed, boolean useEncoders, boolean useGyro) {

        if (useEncoders) {
            this.runWithEncoders();
        } else {
            this.runWithoutEncoders();
        }

        double[] speedWheel = new double[4];

        int m;
        if (useGyro) {
            m = oldGyro;
        }
        else {
            m = Drive.OFFSET;
        }

        telemetry.addData("x", xComp);
        telemetry.addData("y", yComp);
        telemetry.addData("r", rot);

        for (int n = 0; n <= 3; n++) {
            //This \/ rotates the control input to make it work on each motor
            // and assigns the initial wheel power ratios
            speedWheel[n] = xComp * Math.cos(Math.toRadians(m)) +
                    yComp * Math.sin(Math.toRadians(m)) + ROT_RATIO * rot;
            m = (m + 90) % 360;

        }

        //In order to handle the problem if the values in speedWheel[] are greater than 1,
        //this scales them so the ratio between the values stays the same, but makes sure they're
        //less than 1. Then it multiplies it by speed to incorporate the speed at which
        //you want the robot to go
        double scaler = Math.abs(max(speedWheel[0], speedWheel[1], speedWheel[2], speedWheel[3]));
        //if the scaler is 0, it will cause a divide by 0 error
        if (scaler != 0) {
            for (int n = 0; n < 4; n++) {
                speedWheel[n] *= (speed / scaler);
            }
        }

        //sets the wheel powers to the appropriate ratios
        motorRightFront.setPower(speedWheel[0]);
        motorLeftFront.setPower(speedWheel[1]);
        motorLeftBack.setPower(speedWheel[2]);
        motorRightBack.setPower(speedWheel[3]);
    }

    /**
     * finds and returns the largest magnitude of four doubles
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

        double max = a;
        double[] vals = {b, c, d};

        for (int i = 0; i < 3; i++) {
            if (vals[i] > max) {
                max = vals[i];
            }
        }
        return max;
    }

    /**
     * sets this.xComp, this.yComp, and this.zComp values to the given parameters
     * @param x the value to set this.xComp to
     * @param y the value to set this.yComp to
     * @param r the value to set this.zComp to
     */
    public void setValues(double x, double y, double r){
        if (!Double.isNaN(x)) {
            this.xComp = x;
        }
        if (!Double.isNaN(y)) {
            this.yComp = y;
        }
        if (!Double.isNaN(r)) {
            this.rot = r;
        }
    }
}
