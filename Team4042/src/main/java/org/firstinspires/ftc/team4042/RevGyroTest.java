package org.firstinspires.ftc.team4042;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous ;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;

@Autonomous(name="Rev_Gyro_test", group="K9bot")
public class RevGyroTest extends LinearOpMode {

    BNO055IMU imu;

    Orientation angles;
    Acceleration gravity;


    @Override
    public void runOpMode() {

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


// Set up our telemetry dashboard
        while (opModeIsActive()) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            gravity = imu.getGravity();

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
            telemetry.addData("status", imu.getSystemStatus().toShortString());

            //see https://goo.gl/AnKWEn
            telemetry.addData("heading", angles.firstAngle);
            telemetry.addData("roll", angles.secondAngle);
            telemetry.addData("pitch", angles.thirdAngle);
            telemetry.addData("gravity", gravity.toString());

            telemetry.addData("mag", Math.sqrt(gravity.xAccel*gravity.xAccel + gravity.yAccel*gravity.yAccel + gravity.zAccel*gravity.zAccel));
            telemetry.update();
        }
    }
}