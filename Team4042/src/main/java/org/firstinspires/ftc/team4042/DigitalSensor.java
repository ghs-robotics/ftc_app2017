package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/31/2017.
 */

public class DigitalSensor {

    DigitalChannel sensor;

    public void initialize(HardwareMap hardwareMap) {
        sensor = hardwareMap.digitalChannel.get("whisker");
        sensor.setState(false);
        sensor.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean getV() {
        return sensor.getState();
    }
}
