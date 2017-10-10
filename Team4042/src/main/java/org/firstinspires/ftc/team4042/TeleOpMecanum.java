package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    private boolean aPushed = false;

    //True if the back wheels are mecanum, false if they're tank
    private final boolean useBackMecanum = true;

    //Declare OpMode members.
    private MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, telemetry, true);
    }
    
    @Override
    public void loop() {
        if (gamepad1.a && !aPushed) {
            drive.toggleVerbose();
        }
        aPushed = gamepad1.a;
        drive.drive(false, gamepad1, gamepad2, Drive.FULL_SPEED);
        drive.useGyro();
        telemetry.update();

    }

    /* CODE FROM HERE DOWN IS AN ATTEMPT TO IMPLEMENT DYLAN'S DRIVE ALGORITHM
    MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive.drive(false, gamepad1, 1);
    }*/

}