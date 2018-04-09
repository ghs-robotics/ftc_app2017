package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

@TeleOp(name = "Gamepad Test", group = "Iterative Opmode")
@Disabled
public class GamepadTest extends OpMode {

    @Override
    public void init() { }
    
    @Override
    public void loop() {
        telemetryGamepad(gamepad1, 1);
        telemetryGamepad(gamepad2, 2);
    }
    
    private void telemetryGamepad(Gamepad gamepad, int gamepadNum) {
        String gNum = Integer.toString(gamepadNum);
        telemetry.addData(gNum + ": left stick x", gamepad.left_stick_x);
        telemetry.addData(gNum + ": left stick y", gamepad.left_stick_y);
        telemetry.addData(gNum + ": right stick x", gamepad.right_stick_x);
        telemetry.addData(gNum + ": right stick y", gamepad.right_stick_y);

        telemetry.addData(gNum + ": y", gamepad.y);
        telemetry.addData(gNum + ": b", gamepad.b);
        telemetry.addData(gNum + ": a", gamepad.a);
        telemetry.addData(gNum + ": x", gamepad.x);

        telemetry.addData(gNum + ": dpad up", gamepad.dpad_up);
        telemetry.addData(gNum + ": dpad right", gamepad.dpad_right);
        telemetry.addData(gNum + ": dpad down", gamepad.dpad_down);
        telemetry.addData(gNum + ": dpad left", gamepad.dpad_left);

        telemetry.addData(gNum + ": left bumper", gamepad.left_bumper);
        telemetry.addData(gNum + ": right bumper", gamepad.right_bumper);
        telemetry.addData(gNum + ": left trigger", gamepad.left_trigger);
        telemetry.addData(gNum + ": right trigger", gamepad.right_bumper);
    }

}