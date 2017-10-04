package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="TestAuto", group="Autonomous")
public class Auto extends LinearOpMode {

    MecanumDrive drive;

    public enum Direction {Right, Left, Forward, Backward};

    @Override
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, telemetry, true);

        waitForStart();

        autoDrive(Direction.Forward, Drive.FULL_SPEED, 50);
        autoDrive(Direction.Left, Drive.FULL_SPEED, 50);
        //check sensor sums
        //robot starts facing right
        //scan vision patter
        //go to front of jewels
        //cv scan
        //knock off other jewel
        //head right
        //whisker sensor hits cryptobox
        //back up
        //repeat ^ until whisker disengages
        //move right until we see -^-^-| from ultrasonic
        //place block
        //detach and extend robot towards glyph
    }

    private void autoDrive(Direction direction, double speed, double targetTicks) {
        boolean done = false;
        while (opModeIsActive() && !done) {
            done = drive.driveWithEncoders(direction, speed, targetTicks);
        }
    }
}