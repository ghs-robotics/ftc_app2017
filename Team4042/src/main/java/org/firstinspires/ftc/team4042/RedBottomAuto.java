package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name="RedBottomAuto", group="K9bot")
public class RedBottomAuto extends LinearOpMode {

    MecanumDrive drive = new MecanumDrive(true);
    Auto auto;

    @Override
    public void runOpMode() {
        drive.setUseGyro(true);
        drive.initialize(telemetry, hardwareMap);

        auto = new Auto(hardwareMap, drive, telemetry, "redbottom.txt");
        waitForStart();

        //TODO: TEST THIS
        auto.runOpMode();
        
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