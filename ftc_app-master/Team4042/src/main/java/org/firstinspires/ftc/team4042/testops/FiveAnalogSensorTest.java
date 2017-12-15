package org.firstinspires.ftc.team4042.testops;

/**
 * Created by Hazel on 11/7/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

@TeleOp(name = "Five Analog Sensor", group = "Iterative Opmode")
public class FiveAnalogSensorTest extends OpMode{
    private AnalogSensor[] shortRange = new AnalogSensor[3];
    private AnalogSensor[] longRange = new AnalogSensor[2];

    @Override
    public void init() {
        for (int i = 0; i < shortRange.length; i++) {
            shortRange[i] = new AnalogSensor("ir" + i, false);
            shortRange[i].initialize(hardwareMap);
        }
        for (int i = 0; i < longRange.length; i++) {
            longRange[i] = new AnalogSensor("longir" + i, true);
            longRange[i].initialize(hardwareMap);
        }
    }

    @Override
    public void loop() {
        for (AnalogSensor shortRange : shortRange) {
            shortRange.addReading();
            telemetry.addData(shortRange.getName(), shortRange.getCmAvg());
        }
        for (AnalogSensor longRange : longRange) {
            longRange.addReading();
            telemetry.addData(longRange.getName(), longRange.getCmAvg());
        }
        telemetry.update();
    }

}
