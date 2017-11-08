package org.firstinspires.ftc.team4042;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class RevGyro {

    private static final double OFFSET = 180;

    private BNO055IMU imu;

    private Orientation angles;

    private String gravity;
    private String status;
    private double heading = 0;
    private double roll = 0;
    private double pitch = 0;
    private double mag = 0;

    Telemetry telemetry;
    HardwareMap hardwareMap;

    private double adjust = 0;

    private final static double SCALE = 440;

    public RevGyro() {

    }

    public void initialize(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
// Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
// on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
// and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void setAdjust(double adjust) {
        this.adjust = adjust;
    }

    /**
     * Updates all gyro values.
     */
    public void update() {
        updateAngles();
        updateGravMag();
        updateStatus();
    }

    /**
     * Updates heading, roll, and pitch
     */
    public void updateAngles() {
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        //see https://goo.gl/AnKWEn
        heading = angles.firstAngle + adjust;
        //Since the gyro should be from -180 to 180
        while (heading > 180) {
            heading -= 360;
        }
        while (heading < -180) {
            heading += 360;
        }

        roll = angles.secondAngle;
        pitch = angles.thirdAngle;
    }

    /**
     * Updates status
     */
    public void updateStatus() {
        status = imu.getSystemStatus().toShortString();
    }

    /**
     * Updates gravity and mag
     */
    public void updateGravMag() {
        Acceleration grav = imu.getGravity();

        gravity = grav.toString();
        mag = Math.sqrt(grav.xAccel * grav.xAccel + grav.yAccel * grav.yAccel + grav.zAccel * grav.zAccel);
    }

    /**
     * Updates the heading value.
     */
    public double updateHeading() {
        updateAngles();
        return heading;
    }

    /**
     * Returns the last read heading value
     * @return Heading
     */
    public double getHeading() {
        return heading;
    }

    public double updatePitch() {
        updateAngles();
        return pitch;
    }

    public double getPitch() {
        return pitch;
    }

    public double updateRoll() {
        updateAngles();
        return roll;
    }

    public double getRoll() {
        return roll;
    }

    private double formatAngle(double oldAngle) {
        double newAngle = Math.round(oldAngle / SCALE) + OFFSET;
        newAngle = ((newAngle % 360) + 360) % 360;
        return newAngle;
    }


    public double getMag() {
        return mag;
    }

    public String getGravity() {
        return gravity;
    }

    /**
     * System Status Codes:
     * Result  Meaning
     * 0       idle
     * 1       system error
     * 2       initializing peripherals
     * 3       system initialization
     * 4       executing self-test
     * 5       sensor fusion algorithm running
     * 6       system running without fusion algorithms
     * @return The gyro's current status
     */
    public int getStatus() {
        return Integer.parseInt(status);
    }
}
