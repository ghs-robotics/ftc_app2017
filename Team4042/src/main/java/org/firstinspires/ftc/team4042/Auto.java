package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="TestAuto", group="Autonomous")
public class Auto extends LinearOpMode {

    MecanumDrive drive;

    public final static double RIGHT = 2;
    public final static double LEFT = -2;
    public final static double FORWARD = 1;
    public final static double BACKWARD = -1;

    @Override
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, telemetry, true);

        waitForStart();

        boolean done = false;
        while (opModeIsActive() && !done) {
            telemetry.addData("done", done);
            done = drive.driveWithEncoders(RIGHT, 1, 100);
        }

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
}