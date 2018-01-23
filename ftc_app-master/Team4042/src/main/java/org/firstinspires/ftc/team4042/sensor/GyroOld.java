package org.firstinspires.ftc.team4042.sensor;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.TypeConversion;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by Ryan Whiting on 10/3/2017.
 */
@I2cSensor(name = "Brenda's Color Sensor", description = "Adafruit RGB Sensor", xmlTag = "TCS34725")
public class GyroOld extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    public enum Register {
        DEFAULT_ADDRESS(0x6B),
        CTRL1(0x20),
        CTRL4(0x23),
        STATUS(0xA7),
        OUT_X_L(0xA8),
        OUT_Z_H(0xAD),
        LOW_ODR(0x39);

        public int bVal;

        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    public GyroOld(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(I2cAddr.create7bit(Register.DEFAULT_ADDRESS.bVal));

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    @Override
    public Manufacturer getManufacturer() {

        return Manufacturer.Other;
    }

    @Override
    protected synchronized boolean doInitialize() {return true;}

    @Override
    public String getDeviceName() {

        return "MaxBotics MB1242 Ultrasonic Range Sensor";
    }

    public void startRanging(Telemetry telemetry) {
        deviceClient.write8(Register.LOW_ODR.bVal, 0x00); // Sets return rate to default
        deviceClient.write8(Register.CTRL4.bVal, 0x00); // enables x, y, and z
        deviceClient.write8(Register.CTRL1.bVal, 0x6F);// enables gyro
    }

    public byte getZ() {
        return deviceClient.read8(Register.OUT_Z_H.bVal);
    }

    /**
     * returns all 6 gyro values in one byte[]
     */
    public byte[] getXYZ() {
        return deviceClient.read(Register.OUT_X_L.bVal, 6);
    }

}