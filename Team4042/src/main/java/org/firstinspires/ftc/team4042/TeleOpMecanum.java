package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    boolean aPushed = false;

    //True if the back wheels are mecanum, false if they're tank
    final boolean useBackMecanum = true;

    /* Declare OpMode members. */
    Drive drive;

    @Override
    public void init() {
        if (useBackMecanum) {
            drive = new MecanumDrive(hardwareMap, telemetry);
        } else {
            drive = new HalfMecanumDrive(hardwareMap, telemetry);
        }
    }
    
    @Override
    public void loop() {
        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, 1);
    }

}