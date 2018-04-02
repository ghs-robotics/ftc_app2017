package org.firstinspires.ftc.team4042.drive;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Bren on 4/1/2018.
 */

public class MyGamepad {

    public float left_stick_x;
    public float left_stick_y;
    public float right_stick_x;
    public float right_stick_y;
    public boolean dpad_up;
    public boolean dpad_down;
    public boolean dpad_left;
    public boolean dpad_right;
    public boolean a;
    public boolean b;
    public boolean x;
    public boolean y;
    public boolean start;
    public boolean back;
    public boolean left_bumper;
    public boolean right_bumper;
    public boolean left_stick_button;
    public boolean right_stick_button;
    public float left_trigger;
    public float right_trigger;

    public MyGamepad() {
        this.left_stick_x = 0.0F;
        this.left_stick_y = 0.0F;
        this.right_stick_x = 0.0F;
        this.right_stick_y = 0.0F;
        this.dpad_up = false;
        this.dpad_down = false;
        this.dpad_left = false;
        this.dpad_right = false;
        this.a = false;
        this.b = false;
        this.x = false;
        this.y = false;
        this.start = false;
        this.back = false;
        this.left_bumper = false;
        this.right_bumper = false;
        this.left_stick_button = false;
        this.right_stick_button = false;
        this.left_trigger = 0.0F;
        this.right_trigger = 0.0F;
    }

    public void update(Gamepad gamepad) {
        left_stick_x = gamepad.left_stick_x;
        left_stick_y = gamepad.left_stick_y;
        right_stick_x = gamepad.right_stick_x;
        right_stick_y = gamepad.right_stick_y;
        dpad_up = gamepad.dpad_up;
        dpad_down = gamepad.dpad_down;
        dpad_left = gamepad.dpad_left;
        dpad_right = gamepad.dpad_right;
        a = gamepad.a;
        b = gamepad.b;
        x = gamepad.x;
        y = gamepad.y;
        start = gamepad.start;
        back = gamepad.back;
        left_bumper = gamepad.left_bumper;
        right_bumper = gamepad.right_bumper;
        left_stick_button = gamepad.left_stick_button;
        right_stick_button = gamepad.right_stick_button;
    }
}
