package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

/**
 * Created by Gautham on 11/8/2017.
 */

public class ColorSensor {
    HardwareMap hardwareMap;
    String name;
    NormalizedColorSensor colorSensor;

    /**
     * Creates the color sensor
     * @param name name of Sensor in config file
     */
    public ColorSensor(String name ){
        this.name = name;
    }

    /**
     * returns RGBA of sensors reading
     * @return  the RGBA of sensors reading
     */
    public NormalizedRGBA JewelColor(){
        return colorSensor.getNormalizedColors();

        }



    /**
     * sets the colorSensor variable equal to the NormalizedColorSensor
     * @param hardwareMap Hardwaremap
     */
    public void initialize(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, name);
    }

    /**
     * checks whether red reading of the colorsensor is larger than the blue
     * @return true if red is larger and false if not
     */
    public boolean SenseRed(){
        NormalizedRGBA colors = this.JewelColor();
        if (colors.red > colors.blue){
            return true;
        }
        return false;
    }

    /**
     * checks whether red reading of the colorsensor is larger than the blue
     * @return true if blue is larger and false if not
     */
    public boolean SenseBlue(){
        NormalizedRGBA colors = this.JewelColor();
        return colors.blue > colors.red;
    }
}
