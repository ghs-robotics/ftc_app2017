package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="TestAuto", group="K9bot")
public class TestAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {

        try {

            super.setUp(drive, "test.txt");
            try {
                waitForStart();
            } catch (InterruptedException ex) {
            }

            super.runAuto();
        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
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