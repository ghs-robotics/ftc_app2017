package org.firstinspires.ftc.team4042;

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

    public double getCmAvgAsShortIR() {
        double voltage = getVAvg();
        double cm = getCmAsShortIR(voltage);
        return cm;
    }

    private double getVAvg() {
        if (infrared == null) { return -1; }
        double sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += infrared.getVoltage();
        }
        double voltage = sum/vals.length;
        return voltage;
    }

    public double getCmAvgAsLongIR() {
        double voltage = getVAvg();
        double cm = getCmAsLongIR(voltage);
        return cm;
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
        return (int)Math.round(51.0608 * Math.pow(voltage, -1.2463) - -1.2463);
    }

    /*private double getVReptAsShortIR() {
        if (infrared == null) { return -1; }
        SparseIntArray occurrences = new SparseIntArray(); //A list of inches and the number of times they've occurred
        while (true) {
            double voltage = infrared.getVoltage(); //Gets the voltage
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
