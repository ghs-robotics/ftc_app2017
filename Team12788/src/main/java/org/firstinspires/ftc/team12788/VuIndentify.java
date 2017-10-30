package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by Bren on 10/30/2017.
 */

public class VuIndentify {

    private VuforiaLocalizer vuforia;
    private int cameraMonitorViewId;
    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;


    /**
     * Creates the camera object for VuMark identification
     * @param hardwareMap Hardware Map of OpMode
     */
    public VuIndentify(HardwareMap hardwareMap) {
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    }

    /**
     * Initializes camera and mark code
     */
    public void init() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AYQeJpT/////AAAAGTYIMyXJhERVtNOZjU3Ij86J7nhWU3V9lqwfv7A2iNUi52+j5tjLmc2XIa89AQXNmcyeExAdhhPy6zwU6R6T2fCLS2fr8YeYzG64ljMLW9VNwArYgr1CPSG+79VWsMrC67AfiWtTiQVx2X0b96dIrrACtijHut4BDc/JI9Lth1vHWkR4f5BtclEtnMz/dnRmP8M1QFnxPLCRm8jQVQUAwmedpLAYfeasec2ZcWSb5JRDJwuBmQMDBD6sAHcyfDy9EZXsFHfoscAv/t6U37x3/VoRQncONjYr8/Et7Km4inIzs/N3Dc4h63vPkSu61eZGlJojCwfbHlwshGzq4fti5PCfqVnJv6NI6LijknZ3vSfy";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        relicTrackables.activate();
    }

    /**
     * Returns RelicRecoveryVuMark enum of either LEFT, RIGHT, CENTER, or UNKNOWN
     * @return value
     */
    public RelicRecoveryVuMark read() {
        return RelicRecoveryVuMark.from(relicTemplate);
    }
}
