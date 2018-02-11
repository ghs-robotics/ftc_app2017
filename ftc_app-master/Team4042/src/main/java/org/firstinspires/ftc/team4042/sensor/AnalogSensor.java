package org.firstinspires.ftc.team4042.sensor;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Hazel on 10/13/2017.
 *
 * Reads data from an analog input and can return it in centimeters
 */

public class AnalogSensor {
    public AnalogInput sensor;
    private String name;
    public Type type;
    public enum Type { SHORT_RANGE, LONG_RANGE, SONAR }

    public static final int NUM_OF_READINGS = 6;

    private int curr = 0;

    private boolean firstLoop = true;

    private double[] readings = new double[NUM_OF_READINGS];

    public AnalogSensor(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public void initialize(HardwareMap hardwareMap) {
        try {
            sensor = hardwareMap.analogInput.get(name);
        } catch (IllegalArgumentException ex) {
            sensor = null;
        }
    }

    public String getName() {
        return name;
    }

    public double getCmAvg() {
        double voltage = getVAvg();
        switch (type) {
            case SHORT_RANGE:
                return getCmAsShortIR(voltage);
            case LONG_RANGE:
                return getCmAsLongIR(voltage);
            case SONAR:
                return getCmAsSonar(voltage);
        }
        return -1;
    }

    public void addReading(boolean remove) {
        if (sensor == null) { readings[curr] = 0; }
        else if (remove && (sensor.getVoltage() > 0.121412 && sensor.getVoltage() < .148169)) { }
        else {
            readings[curr] = sensor.getVoltage();
            curr++;

            if (firstLoop && curr >= NUM_OF_READINGS) {
                firstLoop = false;
            }

            curr %= NUM_OF_READINGS;
        }
    }

    public void addReading() {
        this.addReading(false);
    }

    public double getVAvg() {
        if (sensor == null) { return -1; }
        double sum = 0;
        double numToRead = firstLoop ? curr + 1 : NUM_OF_READINGS;

        for (int i = 0; i < numToRead; i++) {
            sum += readings[i];
        }
        double voltage = sum/NUM_OF_READINGS;
        return voltage;
    }

    public double getCmAvg(double threshold, double offset) {
        if (sensor == null) { return -1; }
        double sum = 0;
        double numToRead = firstLoop ? curr + 1 : NUM_OF_READINGS;

        double failures = 0;

        for (int i = 0; i < numToRead; i++) {
            double add = readings[i];
            switch (type) {
                case SHORT_RANGE:
                    add = getCmAsShortIR(add);
                case LONG_RANGE:
                    add = getCmAsLongIR(add);
                case SONAR:
                    add = getCmAsSonar(add);
            }
            if (add < threshold) { add += offset; }
            sum += add;
        }
        double voltage = sum/NUM_OF_READINGS;
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

    private int getCmAsSonar(double voltage) {
        if (voltage == -1) { return -1; }
        return (int)Math.round(747.47*voltage - 0.7522);
    }

    //90 - 110
    //0.121412 - .148169

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
