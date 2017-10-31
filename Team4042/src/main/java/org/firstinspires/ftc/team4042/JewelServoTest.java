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

    private Servo jewelServo;
    private double position = 0;
    private boolean y = false;
    private boolean a = false;

    @Override
    public void init() {
        jewelServo = hardwareMap.servo.get("jewel");
        jewelServo.setPosition(position);
    }

    @Override
    public void loop() {
        if (gamepad1.y && !y) {
            position += .1;
            jewelServo.setPosition(position);
        }
        y = gamepad1.y;

        if (gamepad1.a && !a) {
            position -= .1;
            jewelServo.setPosition(position);
        }
        a = gamepad1.a;

        telemetry.addData("position", position);
        telemetry.update();
    }
}
