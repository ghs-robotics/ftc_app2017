package org.firstinspires.ftc.team4042.drive;

import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team4042.autos.C;

@TeleOp(name = "CryptoboxTest", group="drive")
public class CryptoboxTest extends OpMode {

    private Cryptobox cryptobox;
    private boolean bA;
    private boolean bX;

    @Override
    public void init() {
        cryptobox = new Cryptobox(telemetry);
    }

    @Override
    public void start() {
    }
    
    @Override
    public void loop() {
        try {
            if (!bA && gamepad2.a) {
                cryptobox.placeGlyph(Cryptobox.GlyphColor.BROWN);
            }
            if (!bX && gamepad2.x) {
                cryptobox.placeGlyph(Cryptobox.GlyphColor.GREY);
            }
            bA = gamepad2.a;
            bX = gamepad2.x;

            telemetry.addData("Cryptobox", cryptobox.toString());
            telemetry.addData("Num Glyphs Placed", cryptobox.getNumGlyphsPlaced());
            telemetry.update();
        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
            telemetry.update();
        }
    }

    @Override
    public void stop() {
    }
}