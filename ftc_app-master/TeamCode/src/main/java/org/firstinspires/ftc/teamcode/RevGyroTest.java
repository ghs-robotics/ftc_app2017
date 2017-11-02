package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Gets all the data from a gyro and prints it to telemetry
 */
@Autonomous(name="Rev_Gyro_test", group="K9bot")
public class RevGyroTest extends LinearOpMode {

    private RevGyro revGyro = new RevGyro();

    @Override
    public void runOpMode() {

        revGyro.initialize(telemetry, hardwareMap);


// Set up our telemetry dashboard
        while (opModeIsActive()) {
            revGyro.updateAngles();
            telemetry.addData("heading", revGyro.getHeading());
            telemetry.addData("pitch", revGyro.getPitch());
            telemetry.addData("roll", revGyro.getRoll());
            telemetry.update();
        }
    }
}