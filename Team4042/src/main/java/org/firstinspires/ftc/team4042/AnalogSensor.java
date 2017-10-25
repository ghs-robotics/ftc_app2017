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
    AnalogInput[] infrared = new AnalogInput[5];
    double[] vals = new double[250];

    public AnalogSensor(AnalogInput infrared) { }

    public void initialize(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        for(int i = 0; i < ultraCount; i++){
            infrared[i] = hardwareMap.analogInput.get("infrared" + i);
        }
    }

    public double getVoltageAvg(AnalogInput infrared) {
        if (infrared == null) { return -1; }
        double sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += infrared.getVoltage();
        }
        double voltage = sum/vals.length;
        return voltage;
    }

    public double getVoltageRept(AnalogInput infrared) {
        if (infrared == null) { return -1; }
        SparseIntArray occurrences = new SparseIntArray(); //A list of inches and the number of times they've occurred
        while (true) {
            double voltage = infrared.getVoltage(); //Gets the voltage
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

    public double getInchesAvg(AnalogInput infrared) {
        double voltage = getVoltageAvg(infrared);
        double inches = getInFromVolt(voltage);
        return inches;
    }

    public double getInchesRept(AnalogInput infrared) {
        double voltage = getVoltageRept(infrared);
        double inches = getInFromVolt(voltage);
        return inches;
    }
}
