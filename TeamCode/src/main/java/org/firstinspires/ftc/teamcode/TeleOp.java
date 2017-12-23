/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="TeleOp")
//@Disabled
public class TeleOp extends OpMode {

    double armPos;

    //initializes all motors
    DcMotor motorRight;
    DcMotor motorLeft;

    //initializes all servos
    Servo arm;

    boolean playerJoy;
    boolean temp;

    @Override
    public void init() {
        //boolean variables to control if player has power
        playerJoy = true;
        temp = false;

        //sets left and right motors based on config file
        motorRight = hardwareMap.dcMotor.get("motorR");
        motorLeft = hardwareMap.dcMotor.get("motorL");

        //sets all motors to forward
        motorRight.setDirection(DcMotor.Direction.FORWARD);
        motorLeft.setDirection(DcMotor.Direction.FORWARD);

        //sets arm specified in config file to their name
        arm = hardwareMap.servo.get("arm");

        //sets the starting position of the arm
        armPos = 0;
    }

    @Override
    public void loop() {

        /*receives values fro them driver's joystick,
        since we are using tank drive,
        the right joystick sets the speed of the right motors,
        and the left joystick controls the motors on the left*/
        float right = -gamepad1.right_stick_y;
        float left = -gamepad1.left_stick_y;

        if (playerJoy) {
            //overrides with values from kids joy, if boolean is positive
            right = -gamepad2.right_stick_y;
            left = -gamepad1.left_stick_y;

            //sets arm elevation is operator's left joystick is up/down
            if (gamepad2.right_bumper) {
                armPos += .001;
            } else if (gamepad2.left_bumper) {
                armPos -= .001;
            }
            //clips and moves the servo to specified position.
            armPos = Range.clip(armPos, 0, 1);
            arm.setPosition(armPos);
        }

        //clips left and right to be within range: 0-1
        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        //moves motors at powers specifies by left and right
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRight.setPower(right);
        motorLeft.setPower(left);

        //toggles players joysitck control
        if (gamepad1.start || temp) {
            temp = !temp;
            if (!gamepad1.start) {
                temp = !temp;
                playerJoy = !playerJoy;
            }
        }

        //sends data back to the divers containing
        //servo positions, lift power, and drive motor powers
        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("PlayerJoy", "%b", playerJoy);
        telemetry.addData("arm", "%.2f", armPos);
        telemetry.addData("left tgt pwr", "%.2f", left);
        telemetry.addData("right tgt pwr", "%.2f", right);

    }
}