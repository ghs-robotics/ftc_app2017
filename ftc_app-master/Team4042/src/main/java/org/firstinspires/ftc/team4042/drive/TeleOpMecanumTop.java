package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Hazel on 2/18/2018.
 */
@TeleOp(name = "Mecanum TOP", group="drive")
public class TeleOpMecanumTop extends TeleOpMecanum {
    @Override
    public void init() {
        super.init();
        Drive.useSideLimits = false;
        Drive.top = true;
    }
}
