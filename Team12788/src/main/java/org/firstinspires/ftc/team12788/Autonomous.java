/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Basic: Linear OpMode", group="Linear Opmode")
@Disabled
public class Autonomous extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();

    private DcMotor liftLeft;
    private DcMotor liftRight;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;

    private String vuMark;




    @Override
    public void runOpMode() {
        liftLeft = hardwareMap.dcMotor.get("liftLeft");
        liftRight = hardwareMap.dcMotor.get("liftRight");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        drive.runWithEncoders();
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        waitForStart();
        vuMark = "CENTER";
        if ((vuMark = "CENTER")) {
            while (!drive.driveWithEncoders(Direction.Left, Drive.FULL_SPEED, 4000)) {
            }
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
            wait(2000);
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
            while (!drive.rotateWithEncoders(Direction.Rotation.Counterclockwise, Drive.FULL_SPEED, 1000)) {
            }
            while (!drive.driveWithEncoders(Direction.Forward, Drive.FULL_SPEED, 200)){} ;
        }
        if ((vuMark = "RIGHT")) {
            while (!drive.driveWithEncoders(Direction.Left, Drive.FULL_SPEED, 3800)) {}
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
            wait(2000);
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
            while (!drive.rotateWithEncoders(Direction.Rotation.Counterclockwise, Drive.FULL_SPEED, 1000)) {}
            while (!drive.driveWithEncoders(Direction.(1, Math.sqrt(3)), Drive.FULL_SPEED, 4000/Math.sqrt(3))) {};

        }
        if (vuMark =="LEFT") {
            while (!drive.driveWithEncoders(Direction.Left, Drive.FULL_SPEED, 4200)) {}
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
            sleep(2000);
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
            while (!drive.rotateWithEncoders(Direction.Rotation.Counterclockwise, Drive.FULL_SPEED, 1000)) {}
            while (!drive.driveWithEncoders(Direction.(-1, Math.sqrt(3)), Drive.FULL_SPEED, 4000/Math.sqrt(3))) {};

        }





        }

// hi //

    }
}