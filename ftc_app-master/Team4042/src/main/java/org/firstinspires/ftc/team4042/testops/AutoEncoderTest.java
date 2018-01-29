package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.team4042.drive.Direction;
import org.firstinspires.ftc.team4042.drive.Drive;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;

@Autonomous(name="EncoderTest", group="testops")
@Disabled
public class AutoEncoderTest extends LinearOpMode {

    public DcMotor motorLeftFront;
    public DcMotor motorRightFront;
    public DcMotor motorLeftBack;
    public DcMotor motorRightBack;

    public void runOpMode() {

        motorLeftFront = hardwareMap.dcMotor.get("front left");
        motorRightFront = hardwareMap.dcMotor.get("front right");
        motorRightBack = hardwareMap.dcMotor.get("back right");
        motorLeftBack = hardwareMap.dcMotor.get("back left");

        motorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            motorLeftFront.setPower(1);
            motorRightFront.setPower(1);
            motorRightBack.setPower(1);
            motorLeftBack.setPower(1);

            telemetry.addData("front left", motorLeftFront.getCurrentPosition());
            telemetry.addData("front right", motorRightFront.getCurrentPosition());
            telemetry.addData("back left", motorLeftBack.getCurrentPosition());
            telemetry.addData("back right", motorRightBack.getCurrentPosition());
            telemetry.update();
        }
    }

    /*MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        //drive.setUseGyro(true);

        waitForStart();

        drive.setEncoders(true);
        autoDrive(Direction.Forward, Drive.FULL_SPEED, 1000000, true);

    }

    /**
     * Drives in the given Direction at the given speed until targetTicks is reached
     * @param direction The direction to head in
     * @param speed The speed to move at
     * @param targetTicks The final distance to have travelled, in encoder ticks

    private void autoDrive(Direction direction, double speed, double targetTicks, boolean useGyro) {
        boolean done = false;
        while (opModeIsActive() && !done) {
            done = drive.driveWithEncoders(direction, speed, targetTicks, useGyro, 0);

        }
    }*/
}