package org.firstinspires.ftc.team4042.autos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.team4042.drive.MecanumDrive;

@TeleOp(name = "Motor Test", group = "Iterative Opmode")
public class MotorTest extends OpMode {

    private MecanumDrive drive = new MecanumDrive(true);

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor backLeft;

    @Override
    public void init() {

        frontLeft = hardwareMap.dcMotor.get("front left");
        frontRight = hardwareMap.dcMotor.get("front right");
        backRight = hardwareMap.dcMotor.get("back right");
        backLeft = hardwareMap.dcMotor.get("back left");

        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    
    @Override
    public void loop() {
        if (gamepad1.y) {
            frontLeft.setPower(1);
        } else {
            frontLeft.setPower(0);
        }

        if (gamepad1.b) {
            frontRight.setPower(1);
        } else {
            frontRight.setPower(0);
        }

        if (gamepad1.a) {
            backRight.setPower(1);
        } else {
            backRight.setPower(0);
        }

        if (gamepad1.x) {
            backLeft.setPower(1);
        } else {
            backLeft.setPower(0);
        }
    }

}