/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all iterative OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Template: Iterative OpMode", group = "Iterative Opmode")
// @Autonomous(...) is the other common choice
@Disabled
public class TeleOpOmni extends OpMode {
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    // private DcMotor leftMotor = null;
    // private DcMotor rightMotor = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */

    final float ROT_RATIO = 1;

    //45, 135, 225, and 315 are the angles the motors are set at.
    final float SINS[] = {(float) (-1 * Math.sin(Math.toRadians(45))),
            (float) (-1 * Math.sin(Math.toRadians(135))),
            (float) (-1 * Math.sin(Math.toRadians(225))),
            (float) (-1 * Math.sin(Math.toRadians(315)))};

    final float COSS[] = {(float) (-1 * Math.cos(Math.toRadians(45))),
            (float) (-1 * Math.cos(Math.toRadians(135))),
            (float) (-1 * Math.cos(Math.toRadians(225))),
            (float) (-1 * Math.cos(Math.toRadians(315)))};

    //The bot's wheels are a diamond like this: <>
    DcMotor motorLeftFront;
    DcMotor motorRightFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    //finds the maximum of four values

    public float max(float a, float b, float c, float d) {
        float max = Float.MIN_VALUE;
        float[] vals = new float[4];
        vals[0] = Math.abs(a);
        vals[1] = Math.abs(b);
        vals[2] = Math.abs(c);
        vals[3] = Math.abs(d);

        for (float val : vals) {
            if (val > max) {
                max = val;
            }
        }
        return max;

        //We can't use a long loop because it has to take less time than a phone tick, so if that ^ code doesn't
        //run fast enough, we can just use this \/ code instead.
        /*
        a = Math.abs(a);
        b = Math.abs(b);
        c = Math.abs(c);
        d = Math.abs(d);

        if (d > a && d > b && d > c) {
            return d;
        }
        if (c > a && c > b && c > d) {
            return c;
        }
        if (b > a && b > c && b > d) {
            return b;
        }
        return a;
        */
    }

    /*
    This moves the robot in a certain direction with a certain speed

    @param xComp Specifies the direction the robot should move in relative to the robot (left/right)
    @param yComp Same as xComp but fwd/bkwd
    @param rot How much the robot should rotate
    @param speed How fast the robot should go (duh)
     */
    public void drive(float xComp, float yComp, float rot, float speed) {
        float[] speedWheel = new float[4];
        for (int n = 0; n < 4; n++) {
            //This \/ rotates the control input to make it work on each motor
            speedWheel[n] = (xComp * SINS[n] + yComp * COSS[n] + ROT_RATIO * rot);
        }

        /*
        Indexes:
        0 is the right front motor
        1 is the left front motor
        2 is the left back motor
        3 is the right back motor
         */
        float[] speedFinal = new float[4];

        //In order to handle the problem if the values in speedWheel[] are greater than 1,
        //this scales them so the ratio between the values stays the same, but makes sure they're
        //less than 1
        float scaler = Math.abs(1 / max(speedWheel[0], speedWheel[1], speedWheel[2], speedWheel[3]));

        for (int n = 0; n < 4; n++) {
            speedFinal[n] = speed * scaler * speedWheel[n];
        }

        motorRightFront.setPower(speedFinal[0]);
        motorLeftFront.setPower(speedFinal[1]);
        motorLeftBack.setPower(speedFinal[2]);
        motorRightBack.setPower(speedFinal[3]);
    }

    //Gyro code
    //Offset is what the calibrated gyroscope thinks of as "forward"
    public int useGyro(float xComp, float yComp, float rot, int oldGyro, int curGyro, int offset) {
        float xCompOut = 0;
        float yCompOut = 0;
        float rotOut = 0;

        int r;
        if (rot == 0) {
            if (2 < 300) {
                oldGyro = curGyro;
            }
            int change = curGyro - oldGyro;
            if (Math.abs(change) < 180) {
                r = (int) (1.5 * ((-127.0/180.0) * change));
            }
            else {
                r = (int) (1.5 * ((-127.0/180.0) * ( (360 - change) % 360) ));
            }
        }
        else {
            oldGyro = curGyro;
            r = (int) rot;
            //ClearTimer(T1);
        }

        if (r < -127) {
            rotOut = -127;
        }
        if (r > 127) {
            rotOut = 127;
        }

        float c = (float) Math.cos(Math.toDegrees(curGyro - offset));
        float s = (float) Math.sin(Math.toDegrees(curGyro - offset));

        xCompOut = xComp * c + yComp * s;
        yCompOut = xComp * -1 * s + yComp * c;

        xComp = xCompOut;
        yComp = yCompOut;
        rot = rotOut;
        return 0;
    }

    //End of Drive.h file, start of TeleOp.c

    public void loop() {
        float xComp = gamepad1.left_stick_x;
        float yComp = -1 * gamepad1.left_stick_y;
        float rot = gamepad1.right_stick_x;

        //This \/ is the controller dead zone
        if (rot < .05 && rot > -.05) {
            rot = 0;
        }
        if (xComp < .05 && xComp > -.05) {
            xComp = 0;
        }
        if (yComp < .05 && yComp > -.05) {
            yComp = 0;
        }

        //This \/ sets a maximum speed (of 1) because a joystick tipped diagonally could max out the motors.
        int speed = (int) (Math.sqrt(xComp * xComp + yComp * yComp) + Math.abs(rot));
        if (speed > 1) {
            speed = 1;
        }

        //If you push both bumpers, you get the really slow mode
        //If you push only one bumper, you get the slow mode
        if (gamepad1.left_bumper == true && gamepad1.right_bumper == true) {
            speed = speed / 6;
        } else if (gamepad1.left_bumper == true || gamepad1.right_bumper == true) {
            speed = speed / 2;
        }

        drive(xComp, yComp, rot, (float) speed);
    }

    /*
    This \/ only applies for autonomous
    void driveToPos(float xComp, float yComp, float rot, float speed, long ticks, int offset) {
        long mot1 =
    }
    */

    @Override
    public void init() {
        motorLeftBack = hardwareMap.dcMotor.get("left back");
        motorLeftFront = hardwareMap.dcMotor.get("left front");
        motorRightBack = hardwareMap.dcMotor.get("right back");
        motorRightFront = hardwareMap.dcMotor.get("right front");
    }

}
