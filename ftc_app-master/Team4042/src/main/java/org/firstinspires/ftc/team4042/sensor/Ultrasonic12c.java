package org.firstinspires.ftc.team4042.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Bren on 9/22/2017.
 */

public class Ultrasonic12c {

    final static int READ = 224;
    final static int REPORT = 225;

    private ElapsedTime timer;

    static final I2cAddr ULTRA_ADDRESS = new I2cAddr(224);
    private I2cDeviceSynch ultraReader;

    public Ultrasonic12c(I2cDevice ultra) {
        timer = new ElapsedTime();
        ultraReader = new I2cDeviceSynchImpl(ultra, ULTRA_ADDRESS, false);
        ultraReader.engage();
        ultraReader.write8(READ, 81); //intitiates the ultrasonic sensor
    }

    public int read() {

        int range = 0;
        byte range_highbyte = 0;
        byte range_lowbyte = 0;
        ultraReader.write8(READ, 81);

        range_highbyte = ultraReader.read8(REPORT); //Read a byte and send an ACK (acknowledge)
        range_lowbyte = ultraReader.read8(REPORT);  //Read a byte and send a NACK to terminate the transmission
        range = (range_highbyte * 256) + range_lowbyte; //compile the range integer from the two bytes received.

        return range;

    }

    public void close() {
        ultraReader.close();
    }

}
