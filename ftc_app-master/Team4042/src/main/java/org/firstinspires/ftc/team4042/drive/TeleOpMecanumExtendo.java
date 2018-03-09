package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Hazel on 2/18/2018.
 */
@TeleOp(name = "Mecanum Extendo", group="drive")
public class TeleOpMecanumExtendo extends TeleOpMecanum {
    @Override
    public void start() {
        super.start();
        Drive.isExtendo = false;
        super.drive.toggleServoExtendo();
    }
}
