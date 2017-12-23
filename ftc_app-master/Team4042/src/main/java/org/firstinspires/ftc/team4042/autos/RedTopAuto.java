package org.firstinspires.ftc.team4042.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team4042.drive.MecanumDrive;

/**
 * Parks in the safe zone, but does so badly
 */
@Autonomous(name="RedTopAuto", group="autos")
public class RedTopAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {

        super.setUp(drive, "redtop.txt");
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