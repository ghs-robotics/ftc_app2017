
package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;




@TeleOp(name="color stuff[", group="Linear Opmode")
public class ColorSensorTest extends LinearOpMode {
    ColorSensor color = new ColorSensor("color");


    @Override
    public void runOpMode() {
        color.initialize(hardwareMap);
        waitForStart();

        while(opModeIsActive()){
            NormalizedRGBA colors = color.JewelColor();
            telemetry.addData("isRed", color.SenseRed());

            telemetry.update();
        }


        }
    }

