package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/31/2017.
 */

public class DigitalSensor {

    private String name;
    private DigitalChannel sensor;

    public DigitalSensor(String name) {
        this.name = name;
    }

    public void initialize(HardwareMap hardwareMap) {
        sensor = hardwareMap.digitalChannel.get(name);
        sensor.setState(false);
        sensor.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean getState() {
        return sensor.getState();
    }
}
