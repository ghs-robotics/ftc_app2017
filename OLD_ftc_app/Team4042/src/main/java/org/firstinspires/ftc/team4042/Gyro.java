package org.firstinspires.ftc.team4042;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Gyro {
    //I2C device addresses
    final static int CTRL1 = 0x20;
    final static int CTRL4 = 0x23;
    final static int STATUS = 0xA7;

    final static int OUT_X_L = 0xA8;

    final static int OUT_Z_H = 0xAD;

    final static int LOW_ODR = 0x39;

    //instance variables
    private double angleX  = 0;
    private double angleY = 0;
    private double angleZ = 0;

    private ElapsedTime timer;
    private double oldTime = 0;
    final static double OFFSET = .1;
    final static double SCALE = 440;

    static final I2cAddr GYRO_ADDRESS = new I2cAddr(0x6B);
    private I2cDevice gyro;
    private I2cDeviceSynch gyroReader;

    /**
     * creates gyro object and initializes the I2C device and the
     * I2C device reader.
     *
     * @param hardwareMap hardware map of robot
     * @param gyroName name of gyro within hardware map
     */
    public Gyro(HardwareMap hardwareMap, String gyroName) {
        timer = new ElapsedTime();
        gyro = hardwareMap.i2cDevice.get(gyroName);
        gyroReader = new I2cDeviceSynchImpl(gyro, GYRO_ADDRESS, false);
        gyroReader.engage();
        gyroReader.write8(LOW_ODR, 0x00); // Sets return rate to default
        gyroReader.write8(CTRL4, 0x00); // enables x, y, and z
        gyroReader.write8(CTRL1, 0x6F);// enables gyro
    }

    /**
     * Starts gyro polling by reading the status from it
     *
     * @return returns the current status of the gyro
     */
    public byte startPolling() {
        return gyroReader.read8(STATUS);
    }

    /**
     * resets gyro, setting angle back to zero
     */
    public void reset(int position) {
        this.angleZ = 0 + position * SCALE;
        this.angleY = 0;
        this.angleX = 0;
    }

    /**
     * gives current angular velocity across z axis
     *
     * @return z angular velocity
     */
    public byte getZ() {
        return gyroReader.read8(OUT_Z_H);
    }

    /**
     * returns all 6 gyro values in one byte[]
     */
    public byte[] getXYZ() {
        return gyroReader.read(OUT_X_L, 6);
    }

    /**
     * Reads data from gyro, setting Z angle accordingly
     */
    public void readZ() {
        int z = this.getZ();
        double diff = (z + OFFSET) * (timer.milliseconds() - oldTime);
        angleZ += diff;
        oldTime = timer.milliseconds();
    }

    /**
     * Reads x y and z rotation values and resets all angles accordingly
     */
    public void readXYZ() {
        byte[] gyroCache = this.getXYZ();

        int[] gyroValues = new int[3];
        double[] diff = new double[3];
        for (int i = 0; i < 3; i++) {
            gyroValues[i] = (gyroCache[2 * i] & 0xFF) + (gyroCache[2 * i + 1] & 0xFF) * 256;

            diff[i] = (gyroValues[i] +  OFFSET) * (timer.milliseconds() - oldTime);
        }

        angleX += diff[0];
        angleY += diff[1];
        angleZ += diff[2];

    }

    /**
     *  Returns the angle across the x axis
     */
    public int getAngleX() {
        int returnAngle = (int)Math.round(angleX / SCALE);
        returnAngle %= 360;
        return returnAngle;
    }

    /**
     *  Returns the angle across the y axis
     */
    public int getAngleY() {
        int returnAngle = (int)Math.round(angleY / SCALE);
        returnAngle %= 360;
        return returnAngle;
    }

    /**
     *  Returns the angle across the z axis
     */
    public int getAngleZ(){
        int returnAngle = (int)Math.round(angleZ / SCALE) + OmniDriveOld.OFFSET;
        returnAngle = ((returnAngle % 360) + 360) % 360;
        return returnAngle;
    }
    /**
     * closes both I2C devices
     */
    public void close() {
        gyro.close();
        gyroReader.close();
    }
}