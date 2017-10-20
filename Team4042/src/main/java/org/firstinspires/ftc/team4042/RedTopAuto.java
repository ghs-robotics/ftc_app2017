package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name="RedTopAuto", group="K9bot")
public class RedTopAuto extends LinearOpMode {

    MecanumDrive drive;
    Auto auto;

    @Override
    public void runOpMode() {
        Telemetry.Log log = telemetry.log();
        drive = new MecanumDrive(hardwareMap, telemetry, false);
        drive.setUseGyro(true);
        auto = new Auto(hardwareMap, drive, telemetry, "auto.txt");
        waitForStart();

        //TODO: TEST THIS
        auto.runOpMode();

        //autoSensorMove(Direction.Forward, Drive.FULL_SPEED / 4, 7, drive.ir);

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