package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Hazel on 10/30/2017.
 * Runs a servo a given amount, as inputted by the controller.
 */
@TeleOp(name="Servo Test", group="testops")
public class ServoTest extends OpMode {

    private Servo servo;
    private double position = 0;
    private boolean y = false;
    private boolean a = false;
    private boolean x = false;
    private boolean b = false;

    private final static double STEP = .01;

    @Override
    public void init() {
        servo = hardwareMap.servo.get("servo");
        servo.setPosition(position);
    }

    @Override
    public void loop() {
        if (gamepad1.y && !y) {
            position += STEP * 10;
            servo.setPosition(position);
        }
        y = gamepad1.y;

        if (gamepad1.x && !x) {
            position += STEP;
            servo.setPosition(position);
        }
        x = gamepad1.x;

        if (gamepad1.a && !a) {
            position -= STEP * 10;
            servo.setPosition(position);
        }
        a = gamepad1.a;

        if (gamepad1.b && !b) {
            position -= STEP;
            servo.setPosition(position);
        }
        b = gamepad1.b;

        telemetry.addData("position", position);
        telemetry.update();
    }
}
