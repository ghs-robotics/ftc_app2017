package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Hazel on 10/30/2017.
 */
@TeleOp(name="Servo Test", group="testops")
public class ServoTest extends OpMode {

    //up left brake .12
    //deployed left brake .8

    //left catch engaged .17
    //left catch released .6
    private Servo servo;
    private double position = 0;
    private boolean y = false;
    private boolean a = false;

    @Override
    public void init() {
        servo = hardwareMap.servo.get("jewel");
        servo.setPosition(position);
    }

    @Override
    public void loop() {
        if (gamepad1.y && !y) {
            position += .01;
            servo.setPosition(position);
        }
        y = gamepad1.y;

        if (gamepad1.a && !a) {
            position -= .01;
            servo.setPosition(position);
        }
        a = gamepad1.a;

        telemetry.addData("position", position);
        telemetry.update();
    }
}
