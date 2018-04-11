package org.firstinspires.ftc.team4042.testops;

/**
 * Created by Hazel on 11/7/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

@Autonomous(name = "Five Analog Sensor", group = "Iterative Opmode")
public class FiveAnalogSensorTest extends OpMode{
    private AnalogSensor[] shortRange = new AnalogSensor[3];
    private AnalogSensor[] longRange = new AnalogSensor[2];
    private AnalogSensor[] sonar = new AnalogSensor[2];
    private AnalogSensor[] lineFollow = new AnalogSensor[1];

    @Override
    public void init() {
        for (int i = 0; i < shortRange.length; i++) {
            shortRange[i] = new AnalogSensor("ir" + i, AnalogSensor.Type.SHORT_RANGE);
            if (shortRange[i] != null) {
                shortRange[i].initialize(hardwareMap);
                for (int j = 0; j < AnalogSensor.NUM_OF_READINGS; j++) {
                    shortRange[i].addReading();
                }
            }
        }
        for (int i = 0; i < longRange.length; i++) {
            longRange[i] = new AnalogSensor("longir" + i, AnalogSensor.Type.LONG_RANGE);
            if (longRange[i] != null) {
                longRange[i].initialize(hardwareMap);
                for (int j = 0; j < AnalogSensor.NUM_OF_READINGS; j++) {
                    longRange[i].addReading();
                }
            }
        }
        for (int i = 0; i < sonar.length; i++) {
            sonar[i] = new AnalogSensor("sonar" + i, AnalogSensor.Type.SONAR);
            if (sonar[i] != null) {
                sonar[i].initialize(hardwareMap);
                for (int j = 0; j < AnalogSensor.NUM_OF_READINGS; j++) {
                    sonar[i].addReading();
                }
            }
        }
        for (int i = 0; i < lineFollow.length; i++) {
            lineFollow[i] = new AnalogSensor("line follow" + i, AnalogSensor.Type.LINE_FOLLOW);
            if (lineFollow[i] != null) {
                lineFollow[i].initialize(hardwareMap);
                for (int j = 0; j < AnalogSensor.NUM_OF_READINGS; j++) {
                    lineFollow[i].addReading();
                }
            }
        }
    }

    @Override
    public void loop() {
        for (AnalogSensor shortRange : shortRange) {
            if (shortRange != null) {
                shortRange.addReading();
                telemetry.addData(shortRange.getName(), shortRange.getCmAvg());
            }
        }
        for (AnalogSensor longRange : longRange) {
            if (longRange != null) {
                longRange.addReading();
                telemetry.addData(longRange.getName(), longRange.getCmAvg());
            }
        }
        for (AnalogSensor sonar : sonar) {
            if (sonar != null) {
                sonar.addReading();
                telemetry.addData(sonar.getName(), sonar.getCmAvg());
            }
        }
        for (AnalogSensor follow : lineFollow) {
            if (follow != null) {
                follow.addReading();
                telemetry.addData(follow.getName(), follow.getVAvg());
            }
        }
        telemetry.update();
    }

}
