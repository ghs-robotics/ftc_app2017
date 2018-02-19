package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Hazel on 2/18/2018.
 */
@TeleOp(name = "Mecanum CRYPTOBOX CLEAR", group="drive")
public class TeleOpMecanumCryptoboxClear extends TeleOpMecanum {
    @Override
    public void init() {
        super.init();
        super.drive.cryptobox.clear();
        super.drive.cryptobox.writeFile();
    }
}
