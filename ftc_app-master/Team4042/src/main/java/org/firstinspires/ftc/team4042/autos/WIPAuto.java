package org.firstinspires.ftc.team4042.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team4042.drive.Drive;
import org.firstinspires.ftc.team4042.drive.MecanumDrive;

@Autonomous(name="WIPAuto", group="autos")
public class WIPAuto extends Auto {

    MecanumDrive drive = new MecanumDrive(true);

    @Override
    public void runOpMode() {
        try {
            super.setUp(drive, "wip.txt");
            waitForStart();

            super.runAuto();
        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
        }
    }
}