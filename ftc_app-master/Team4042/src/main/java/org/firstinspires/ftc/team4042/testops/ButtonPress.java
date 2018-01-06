package org.firstinspires.ftc.team4042.testops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Hazel on 1/5/2018.
 */
@TeleOp(name = "ButtonPress", group = "testops")
public class ButtonPress extends OpMode {

    private ElapsedTime timer = new ElapsedTime();
    private boolean a = false;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        if (gamepad1.a && !a) {
            timer.reset();
        }
        if (!gamepad1.a && a) {
            telemetry.addData("nanoseconds", timer.nanoseconds());
            telemetry.update();
        }
        a = gamepad1.a;
    }
}
