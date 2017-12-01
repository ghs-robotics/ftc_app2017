package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.team4042.drive.Direction;
import org.firstinspires.ftc.team4042.drive.Drive;

@Autonomous(name="One Encoder Test", group="testops")
public class OneEncoderTest extends LinearOpMode {

    private DcMotor motor;

    @Override
    public void runOpMode() {
        motor = hardwareMap.dcMotor.get("intake left");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        motor.setPower(1);

        while (opModeIsActive()) {
            telemetry.addData("encoder ", motor.getCurrentPosition());
            telemetry.update();
        }
    }
}