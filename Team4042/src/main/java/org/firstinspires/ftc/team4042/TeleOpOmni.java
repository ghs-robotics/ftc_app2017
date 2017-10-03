package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Omni2", group = "Iterative Opmode")
public class TeleOpOmni extends OpMode {

    OmniDrive drive;

    @Override
    public void init() {
        drive = new OmniDrive(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive.drive(false, gamepad1, gamepad2, 1);
    }

}