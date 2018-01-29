package org.firstinspires.ftc.team4042.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.team4042.autos.Auto;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;

/**
 * Reads the vuforia mark
 */
@Autonomous(name="JewelAuto", group="autos")
@Disabled
public class JewelAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {

        super.setUp(drive, "jewel.txt");
        try {
            waitForStart();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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