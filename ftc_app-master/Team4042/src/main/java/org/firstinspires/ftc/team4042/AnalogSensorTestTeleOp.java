package org.firstinspires.ftc.team4042;

/**
 * Created by Hazel on 11/7/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Analog Sensor Test Tele", group = "Iterative Opmode")
public class AnalogSensorTestTeleOp extends OpMode{
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
            telemetry.addData(shortRange.getName(), shortRange.getCmAvg());
        }
        for (AnalogSensor longRange : longRange) {
            telemetry.addData(longRange.getName(), longRange.getCmAvg());
        }
        telemetry.update();
    }

}
