package org.firstinspires.ftc.team4042;

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
        /*vuMarkIdentifier.initialize(telemetry, hardwareMap);

        waitForStart();

        vuMarkIdentifier.start();

        while (opModeIsActive()) {
            RelicRecoveryVuMark vuMark;
            do {
                vuMark = vuMarkIdentifier.getMark();
                telemetry.addData("vuMark", "UNKNOWN");
                telemetry.update();
            } while (vuMark.equals(RelicRecoveryVuMark.UNKNOWN));

            //Increments the value for the associated vumark
            if (occurences.containsKey(vuMark)) {
                occurences.put(vuMark, occurences.get(vuMark) + 1);
            } else {
                occurences.put(vuMark, 1);
            }

            telemetry.addData("vuMark", vuMark);
            telemetry.addData("Left", occurences.get(RelicRecoveryVuMark.LEFT));
            telemetry.addData("Center", occurences.get(RelicRecoveryVuMark.CENTER));
            telemetry.addData("Right", occurences.get(RelicRecoveryVuMark.RIGHT));
            telemetry.update();
        }*/

        waitForStart();

        vuMarkIdentifier.initialize(telemetry, hardwareMap);
    }
}
