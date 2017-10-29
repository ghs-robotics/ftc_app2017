package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="BlueTopAuto", group="K9bot")
public class BlueTopAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {
        drive.setUseGyro(true);
        drive.initialize(telemetry, hardwareMap);

        super.setUp(drive, "bluetop.txt");
        waitForStart();

        //TODO: TEST THIS
        super.runAuto();
        
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