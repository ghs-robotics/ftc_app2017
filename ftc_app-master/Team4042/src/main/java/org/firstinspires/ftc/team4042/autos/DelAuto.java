package org.firstinspires.ftc.team4042.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team4042.drive.MecanumDrive;
import java.io.File;

/**
 * USELESS - Run BlueBottomAuto for parks in the safe zone
 */
@Autonomous(name="RedMovementAuto", group="autos")
public class DelAuto extends Auto {

    @Override
    public void runOpMode() {
        File file;
        String[] autos = new String[] {
                "blue.txt", "bluebottom.txt", "bluejewel.txt", "bluetop.txt", "constants.txt",
                "ir.txt", "jewel.txt", "proto.txt", "red.txt", "redbottom.txt", "redjewel.txt",
                "redtop.txt", "test.txt"
        };
        for(String auto : autos)
            file = new File("./storage/emulated/0/bluetooth/" + auto);
    }
}