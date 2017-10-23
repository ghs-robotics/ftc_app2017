package org.firstinspires.ftc.team4042;

import android.util.SparseIntArray;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/13/2017.
 */

public class AnalogSensor {
    HardwareMap hardwareMap;
    AnalogInput ultrasonic;
    double[] vals = new double[250];

    public AnalogSensor(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    public void initialize() {
        ultrasonic = hardwareMap.analogInput.get("ultrasonic");
    }

    public double getVoltageAvg() {
        if (ultrasonic == null) { return -1; }
        double sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += ultrasonic.getVoltage();
        }
        double voltage = sum/vals.length;
        return voltage;
    }

    public double getVoltageRept() {
        if (ultrasonic == null) { return -1; }
        SparseIntArray occurrences = new SparseIntArray(); //A list of inches and the number of times they've occurred
        while (true) {
            double voltage = ultrasonic.getVoltage(); //Gets the voltage
            int inches = getInFromVolt(voltage); //Gets the voltage in inches
            //Gets the number of times the voltage has occurred, or 0 if it hasn't yet
            int count = occurrences.get(inches, 0);

            if (count >= 4) { //If we've got the same number four times, assume that's the correct one and return it
                return inches;
            } else {
                occurrences.put(inches, count + 1); //Increment the count
            }
        }
    }

    /**
     * Parses the voltage and returns it as an integer, as mapped by a power function
     * @param voltage The voltage returned by the IR
     * @return The inch equivalent
     */
    private int getInFromVolt(double voltage) {
        if (voltage == -1) { return -1; }
        return (int)Math.round(6.48 * Math.pow(voltage, -1.5));
    }

    public double getInchesAvg() {
        double voltage = getVoltageAvg();
        double inches = getInFromVolt(voltage);
        return inches;
    }

    public double getInchesRept() {
        double voltage = getVoltageRept();
        double inches = getInFromVolt(voltage);
        return inches;
    }
}
