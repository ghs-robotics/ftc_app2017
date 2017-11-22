package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/13/2017.
 */

public class AnalogSensor {
    private AnalogInput sensor;
    private String name;
    private boolean isLongRange;

    public AnalogSensor(String name, boolean isLongRange) {
        this.name = name;
        this.isLongRange = isLongRange;
    }

    public void initialize(HardwareMap hardwareMap) {
        sensor = hardwareMap.analogInput.get(name);
    }

    public String getName() {
        return name;
    }

    public double getCmAvg() {
        double voltage = getVAvg();
        if (isLongRange) {
            return getCmAsLongIR(voltage);
        } else {
            return getCmAsShortIR(voltage);
        }
    }

    private double getVAvg() {
        if (sensor == null) { return -1; }
        double sum = 0;
        for (int i = 0; i < 250; i++) {
            sum += sensor.getVoltage();
        }
        double voltage = sum/250;
        return voltage;
    }

    /**
     * Parses the voltage and returns a centimeter distance, as mapped by a power function
     * @param voltage The voltage returned by the IR
     * @return The centimeter equivalent
     */
    private int getCmAsShortIR(double voltage) {
        if (voltage == -1) { return -1; }
        return (int)Math.round(90 * Math.pow(0.08, voltage) + 4.71592);
        //return (int)Math.round(63.9224 * Math.pow(0.106743, voltage) + 4.71592);
    }

    private int getCmAsLongIR(double voltage) {
        if (voltage == -1) { return -1; }
        return (int)Math.round(51.0608 * Math.pow(voltage, -1.2463) + 4.7463);
        //return (int)Math.round(51.0608 * Math.pow(voltage, -1.2463) - 1.2463);
    }

    /*private double getVReptAsShortIR() {
        if (sensor == null) { return -1; }
        SparseIntArray occurrences = new SparseIntArray(); //A list of inches and the number of times they've occurred
        while (true) {
            double voltage = sensor.getVoltage(); //Gets the voltage
            int inches = getIntFromVAsShortIR(voltage); //Gets the voltage in inches
            //Gets the number of times the voltage has occurred, or 0 if it hasn't yet
            int count = occurrences.get(inches, 0);

            if (count >= 4) { //If we've got the same number four times, assume that's the correct one and return it
                return inches;
            } else {
                occurrences.put(inches, count + 1); //Increment the count
            }
        }
    }*/
}
