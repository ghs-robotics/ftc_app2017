package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Omni2", group = "Iterative Opmode")
public class TeleOpOmni2 extends OpMode {

    OmniDrive2 drive;

    @Override
    public void init() {
        drive = new OmniDrive2(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive.drive(false, gamepad1, gamepad2, 1);
    }

}