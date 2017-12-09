package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Gautham on 12/2/2017.
 */
@TeleOp(name = "HiBrendan", group = "Iterative Opmode")
public class ServoAutoStuff extends OpMode {
    public Servo grabLeft;
    public Servo grabRight;
    public Servo pinch;
    public Servo jewel;

    @Override
    public void init() {
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        pinch = hardwareMap.servo.get("pinch");
        jewel = hardwareMap.servo.get("jewel");
    }

    @Override
    public void loop() {
        grabRight.setPosition(.1);
        grabLeft.setPosition(1);
        pinch.setPosition(.4);
        if (gamepad1.a) {
            jewel.setPosition(-1);
        }
        if (gamepad1.b) {
            jewel.setPosition(-1);
        }

    }
}
