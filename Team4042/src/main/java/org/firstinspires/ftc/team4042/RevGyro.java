package org.firstinspires.ftc.team4042;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class RevGyro {

    private BNO055IMU imu;

    private Orientation angles;

    private String gravity;
    private String status;
    private double heading;
    private double roll;
    private double pitch;
    private double mag;

    private double angleX  = 0;
    private double angleY = 0;
    private double angleZ = 0;

    Telemetry telemetry;
    HardwareMap hardwareMap;

    private double oldTime = 0;
    private ElapsedTime timer;

    final static double SCALE = 440;

    public RevGyro(HardwareMap hardwareMap, Telemetry telemetry) {

        this.telemetry = telemetry;

        timer = new ElapsedTime();

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

    public void update() {
        // Set up our telemetry dashboard
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        Acceleration grav = imu.getGravity();

        /*System Status Codes:
        Result  Meaning
        0       idle
        1       system error
        2       initializing peripherals
        3       system initialization
        4       executing self-test
        5       sensor fusion algorithm running
        6       system running without fusion algorithms
        */
        status = imu.getSystemStatus().toShortString();

        //see https://goo.gl/AnKWEn
        heading = angles.firstAngle;
        roll = angles.secondAngle;
        pitch = angles.thirdAngle;
        gravity = grav.toString();

        mag = Math.sqrt(grav.xAccel * grav.xAccel + grav.yAccel * grav.yAccel + grav.zAccel * grav.zAccel);
    }

    public double getHeading() {
        double diff = (heading + OmniDrive2.OFFSET) * (timer.milliseconds() - oldTime);
        heading += diff;
        oldTime = timer.milliseconds();

        double heading = Math.round(angleZ / SCALE) + OmniDrive2.OFFSET;
        heading = ((heading % 360) + 360) % 360;
        return heading;
    }

    public double getMag() {
        return mag;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public String getGravity() {
        return gravity;
    }

    public String getStatus() {
        return status;
    }
}
