package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="BlueBottomAuto", group="K9bot")
public class BlueBottomAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {

        super.setUp(drive, "bluebottom.txt");
        waitForStart();

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