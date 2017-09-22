package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMechanim extends OpMode {

    /* Declare OpMode members. */
    MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, telemetry);
    }
    
    @Override
    public void loop() {
        drive.drive(false, gamepad1, 1);
    }

}
