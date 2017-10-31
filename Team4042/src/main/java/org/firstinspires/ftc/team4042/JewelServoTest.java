package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Hazel on 10/30/2017.
 */
@TeleOp(name="Jewel Servo Test", group="Iterative Opmode")
public class JewelServoTest extends OpMode {

    Servo jewelServo;
    double position = 0;

    @Override
    public void init() {
        jewelServo = hardwareMap.servo.get("jewel");
        jewelServo.setPosition(position);
    }

    @Override
    public void loop() {
        if (gamepad1.y) {
            position += .1;
            jewelServo.setPosition(position);
        }

        if (gamepad1.a) {
            position -= .1;
            jewelServo.setPosition(position);
        }

        telemetry.addData("position", position);
        telemetry.update();
    }
}
