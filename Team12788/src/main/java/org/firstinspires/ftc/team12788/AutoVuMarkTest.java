package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Vu Mark Test Auto", group="K9bot")
public class AutoVuMarkTest extends LinearOpMode{

    //reader
    private VuMarkIdentifier vuMarkIdentifier;

    @Override
    public void runOpMode() throws InterruptedException {

        //init
        vuMarkIdentifier = new VuMarkIdentifier();
        vuMarkIdentifier.initialize(telemetry, hardwareMap);
        waitForStart();

        //reading
        while (opModeIsActive()) {
            telemetry.addData("vuMark", vuMarkIdentifier.getMark());
            telemetry.update();
        }
    }
}
