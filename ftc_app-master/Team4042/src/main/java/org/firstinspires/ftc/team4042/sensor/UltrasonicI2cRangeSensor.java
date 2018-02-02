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
@I2cSensor(name = "MB1242 Ultrasonic Sensor", description = "Range Sensor from Maxbotics", xmlTag = "MB1242-0")
public class UltrasonicI2cRangeSensor extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    Telemetry telemetry;

    public enum Register {
        DEFAULT_ADDRESS(0x70),
        READ_ADDRESS(235),
        WRITE_ADDRESS(234),
        RANGE_COMMAND(81);

        public int bVal;

        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    public UltrasonicI2cRangeSensor(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        //this.deviceClient.setI2cAddress(I2cAddr.create8bit(Register.RANGE_COMMAND.bVal));

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
        int found = 0;
        this.telemetry = telemetry;
        ElapsedTime timer = new ElapsedTime();
        for (int i = 257; i > 0; i--){
            this.deviceClient.setI2cAddress(I2cAddr.create8bit(i));
            this.write();
            timer.reset();
            while(timer.milliseconds() < 100) {}
            double n = this.read();
            if (!(Math.round(n) == 0 || Math.round(n) == -2 || Math.round(n) == -1)){
                telemetry.addData("n", n);
                telemetry.addData("found?", i);
                telemetry.update();
            }
        }

        //deviceClient.write8(Register.WRITE_ADDRESS.bVal, Register.RANGE_COMMAND.bVal);
    }

    public void write() {
        for (int j = 1; j < 257; j++) {
            deviceClient.write8(j, Register.RANGE_COMMAND.bVal);
        }
        //deviceClient.write8(Register.WRITE_ADDRESS.bVal, Register.RANGE_COMMAND.bVal);
    }

    public double read() {
        for (int j = 1; j < 257; j++) {
            byte[] val = deviceClient.read(j,2);
            int n = val[0] - val[1];
            if (!(Math.round(n) == 0 || Math.round(n) == -2 || Math.round(n) == -1)){

                telemetry.addData("read", j);
                return n;
            }
        }
        return 0;
        //byte[] val = deviceClient.read(Register.READ_ADDRESS.bVal,2);
        //return val[0] + val[1];
    }
}