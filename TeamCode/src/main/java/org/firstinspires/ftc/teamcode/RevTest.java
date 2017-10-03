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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Tries to run four motors with names "motor1", "motor2", "motor3", and "motor4".
 *
 * If they're found, it prints their current speed to the telemetry and allows the user
 * to control them with the gamepad.
 *
 * If they're not found, it prints the failure to the telemetry.
 */

@TeleOp(name="RevTest", group="test")
public class RevTest extends OpMode{

    DcMotor[] motors;

    @Override
    public void init() {
        motors = new DcMotor[4];

        //Initialize a reference to each motor
        for (int i = 0; i < motors.length; i++) {
            try {
                DcMotor motor = hardwareMap.dcMotor.get("motor" + (i + 1)); //0-indexed
                motor.setDirection(DcMotorSimple.Direction.FORWARD);
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motors[i] = motor;
            } catch (IllegalArgumentException ex) { telemetry.addData("Cannot find motor", i); }
        }

    }

    @Override
    public void loop() {

        //Can't for-loop over them b/c they each take different input values from the gamepad
        if (motors[0] != null) { motors[0].setPower(Range.clip(gamepad1.left_stick_x, -1, 1)); }
        if (motors[1] != null) { motors[1].setPower(Range.clip(gamepad1.left_stick_y, -1, 1)); }
        if (motors[2] != null) { motors[2].setPower(Range.clip(gamepad1.right_stick_x, -1, 1)); }
        if (motors[3] != null) { motors[3].setPower(Range.clip(gamepad1.right_stick_y, -1, 1)); }

        //Print the power level of each motor, if it can be found
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            if (motor != null) {
                telemetry.addData(Integer.toString(i), motor.getCurrentPosition());
            } else {
                telemetry.addData(Integer.toString(i), "Cannot find motor " + i);
            }
        }
    }

}
