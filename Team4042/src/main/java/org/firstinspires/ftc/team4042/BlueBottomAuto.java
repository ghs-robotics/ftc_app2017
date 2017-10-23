package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name="BlueBottomAuto", group="K9bot")
public class BlueBottomAuto extends LinearOpMode {

    MecanumDrive drive;
    Auto auto;

    @Override
    public void runOpMode() {
        Telemetry.Log log = telemetry.log();
        drive = new MecanumDrive(hardwareMap, telemetry, true);
        drive.setUseGyro(true);
        auto = new Auto(hardwareMap, drive, telemetry, "bluebottom.txt");
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