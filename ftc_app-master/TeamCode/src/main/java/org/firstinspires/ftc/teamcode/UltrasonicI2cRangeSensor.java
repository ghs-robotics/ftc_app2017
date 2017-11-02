package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;

import java.util.ArrayList;

/**
 * Created by Ryan Whiting on 10/3/2017.
 */
@I2cSensor(name = "MB1242 Ultrasonic Sensor", description = "Range Sensor from Maxbotics", xmlTag = "MB1242-0")
public class UltrasonicI2cRangeSensor extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    private ArrayList<Integer> data = new ArrayList<>(2);
    private boolean isRanging = false;

    public enum Register {
        DEFAULT_ADDRESS(224),
        READ_ADDRESS(225),
        READ_ADDRESS2(226),
        RANGE_COMMAND(81);

        public int bVal;

        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    public UltrasonicI2cRangeSensor(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(I2cAddr.create8bit(Register.DEFAULT_ADDRESS.bVal));

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

    public void startRanging() {
        this.isRanging = true;
        deviceClient.write8(Register.DEFAULT_ADDRESS.bVal, Register.RANGE_COMMAND.bVal);
    }

    public ArrayList<Integer> getRange() {
        data.set(0, (int) deviceClient.read8(Register.READ_ADDRESS.bVal));
        data.set(1, (int) deviceClient.read8(Register.READ_ADDRESS2.bVal));
        data.set(2, data.get(0) * 256 + data.get(1));
        return data;
    }
}