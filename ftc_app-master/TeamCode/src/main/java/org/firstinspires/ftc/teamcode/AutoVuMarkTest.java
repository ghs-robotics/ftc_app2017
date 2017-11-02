package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import java.util.HashMap;

/**
 * Created by Hazel on 10/29/2017.
 */

@Autonomous(name="Vu Mark Test Auto", group="K9bot")
public class AutoVuMarkTest extends LinearOpMode{

    private VuMarkIdentifier vuMarkIdentifier;

    private HashMap<RelicRecoveryVuMark, Integer> occurences;

    @Override
    public void runOpMode() throws InterruptedException {
        occurences = new HashMap<>();

        vuMarkIdentifier = new VuMarkIdentifier();
        vuMarkIdentifier.initialize(telemetry, hardwareMap);

        waitForStart();

        RelicRecoveryVuMark vuMark = vuMarkIdentifier.getMark();
        while (opModeIsActive()) {
            telemetry.addData("vuMark", vuMark);
            telemetry.update();
        }
    }
}
