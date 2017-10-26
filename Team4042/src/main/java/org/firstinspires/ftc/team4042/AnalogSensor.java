package org.firstinspires.ftc.team4042;

import android.util.SparseIntArray;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/13/2017.
 */

public class AnalogSensor {
    private int ultraCount = 5;
    HardwareMap hardwareMap;
    AnalogInput infrared;
    String ir;
    double[] vals = new double[250];

    public AnalogSensor(String ir) {
        this.ir = ir;
    }

    public void initialize(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        infrared = hardwareMap.analogInput.get(ir);
    }

    public double getVoltageAvg() {
        if (infrared == null) { return -1; }
        double sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += infrared.getVoltage();
        }
        double voltage = sum/vals.length;
        return voltage;
    }

    /**
     * Parses the voltage and returns it as an integer, as mapped by a power function
     * @param voltage The voltage returned by the IR
     * @return The inch equivalent
     */
    private double getInFromVolt(double voltage) {
        if (voltage == -1) { return -1; }
        return 63.9224 * Math.pow(0.106743, voltage) + 4.71592;
    }

    public double getCmAvg() {
        double voltage = getVoltageAvg();
        double inches = getInFromVolt(voltage);
        return inches;
    }
}
