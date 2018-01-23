package org.firstinspires.ftc.team4042.sensor;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
import com.qualcomm.robotcore.util.TypeConversion;

import java.util.ArrayList;

/**
 * Created by Ryan Whiting on 10/3/2017.
 */
//@I2cSensor(name = "Brenda's Color Sensor", description = "Adafruit RGB Sensor", xmlTag = "TCS34725")
public class ColorSensor extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    private boolean isRanging = false;

    public enum Register {
        ADDRESS(0x29),
        ENABLE(0x80),
        ATIME(0x81),
        CONTROL(0x8F),
        ID(0x92),
        STATUS(0x93),
        CDATAL(0x94),
        GAIN_1X(0x00),
        GAIN_4X(0x01),
        GAIN_16X(0x02),
        GAIN_60X(0x03);

        public int bVal;

        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    public ColorSensor(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(I2cAddr.create8bit(Register.ADDRESS.bVal));

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    @Override
    public Manufacturer getManufacturer() {

        return Manufacturer.Other;
    }

    @Override
    protected synchronized boolean doInitialize() {

        return true;
    }

    @Override
    public String getDeviceName() {

        return "MaxBotics MB1242 Ultrasonic Range Sensor";
    }

    public void init(double milliSeconds, int gain){
        final int time = integrationByte(milliSeconds);
        deviceClient.write8(Register.ENABLE.bVal, 0x03);  // Power on and enable ADC
        deviceClient.read8(Register.ID.bVal);                   // Read device ID
        deviceClient.write8(Register.CONTROL.bVal, gain); // Set gain
        deviceClient.write8(Register.ATIME.bVal, time);
    }

    public void startRanging() {
        this.isRanging = true;
        deviceClient.read8(Register.STATUS.bVal);
    }

    public int[] getCRGB() {

        // Read color registers
        byte[] adaCache = deviceClient.read(Register.CDATAL.bVal, 8);

        // Combine high and low bytes
        int[] crgb = new int[4];
        for (int i=0; i<4; i++) {
            crgb[i] = (adaCache[2*i] & 0xFF) + (adaCache[2*i+1] & 0xFF) * 256;
        }
        return crgb;
    }

    private int integrationByte(double milliSeconds) {
        int count = (int)(milliSeconds/2.4);
        if (count<1)    count = 1;   // Clamp the time range
        if (count>256)  count = 256;
        return (256 - count);
    }
}