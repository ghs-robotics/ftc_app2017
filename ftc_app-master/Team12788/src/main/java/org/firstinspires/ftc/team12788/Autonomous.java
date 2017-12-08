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

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Main Auto", group = "Linear Opmode")
public class Autonomous extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    private VuMarkIdentifier mark = new VuMarkIdentifier();

    private DcMotor lift;
    /*private DcMotor intakeRight;
    private DcMotor intakeLeft;*/

    public Servo grabLeft;
    public Servo grabRight;

    public static final int tile = 1300;
    public static final int turn = 949;
    public static final double speedy = .7;


    private RelicRecoveryVuMark vuMark;
    private ElapsedTime timer;

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        drive.resetEncoders();
        drive.runWithoutEncoders();
        lift = hardwareMap.dcMotor.get("lift");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        mark.initialize(telemetry, hardwareMap);
        boolean isRed = false;
        boolean isTop = false;

        while (!isStarted() && opModeIsActive()) {
            if (gamepad1.a) {
                isTop = false;
            } else if (gamepad1.b) {
                isRed = true;
            } else if (gamepad1.y) {
                isTop = true;
            } else if (gamepad1.x) {
                isRed = false;
            }
            telemetry.addData("Red: ", isRed);
            telemetry.addData("Top: ", isTop);
            telemetry.update();
        }
        vuMark = mark.getMark();
        lkadl(isRed, !isTop);
    }
    public void move(Direction direction, double speed, double targetTicks) {
        while (!drive.driveWithEncoders(direction, speed, targetTicks) && opModeIsActive()) {
        }
        reset();
    }
    public void rotate(Direction.Rotation rotation, double speed, double targetTicks) {
        while (!drive.rotateWithEncoders(rotation, speed, targetTicks) && opModeIsActive()) {
        }
        reset();
    }

    public void dropoff() {
        move(Direction.Backward, Autonomous.speedy, 4 * Autonomous.tile / 24);
        grabRight.setPosition(.7);
        grabLeft.setPosition(.6);
        move(Direction.Forward, Autonomous.speedy, 4 * Autonomous.tile / 24);
    }
    public void reset(){
        drive.resetEncoders();
        sleep(500);
        drive.runWithEncoders();
    }



    public void lkadl(boolean isRed, boolean isTop) {
        drive.runWithoutEncoders();
        //vuMark = mark.getMark();
        grabRight.setPosition(.9);
        grabLeft.setPosition(.4);
        lift.setPower(.3);
        sleep(200);
        lift.setPower(03
        );
        vuMark = RelicRecoveryVuMark.CENTER;
        if (isRed && !isTop) {
            if ((vuMark == RelicRecoveryVuMark.CENTER)) {
                move(Direction.Left, speedy, 3*tile/2) ;
                dropoff();
            }
            if ((vuMark == RelicRecoveryVuMark.RIGHT)) {
                move(Direction.Left, speedy, 5 * tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy - .1, 2*turn);
                dropoff();
                move(Direction.Right, speedy-.1, tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {

                move(Direction.Left, speedy, 11 * tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy - .1, 2*turn);
                dropoff();
                move(Direction.Left, speedy - .1, tile / 3);
            }
        }

        if (isRed && isTop) {
            if ((vuMark == RelicRecoveryVuMark.CENTER)) {
                move(Direction.Left, speedy, tile);
                move(Direction.Backward, speedy - .1, tile / 2);
                rotate(Direction.Rotation.Clockwise, speedy - .2, turn);
                dropoff();
            }
            if ((vuMark == RelicRecoveryVuMark.RIGHT)) {
                move(Direction.Left, speedy, tile);
                move(Direction.Backward, speedy - .2, tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy - .2, turn);
                dropoff();
                move(Direction.Right, speedy - .1, tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Left, speedy, tile);
                move(Direction.Backward, speedy, 5 * tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy - .2, turn);
                dropoff();
                move(Direction.Left, speedy - .1, tile / 3);
            }
        }
        if (!isRed && isTop) {
            if ((vuMark == RelicRecoveryVuMark.CENTER)) {
                move(Direction.Right, speedy, tile);
                move(Direction.Backward, speedy, tile / 2);
                rotate(Direction.Rotation.Counterclockwise, speedy - .2, turn);
                dropoff();
            }
            if ((vuMark == RelicRecoveryVuMark.RIGHT)) {
                move(Direction.Right, speedy, tile);
                move(Direction.Backward, speedy - .2, tile / 6);
                rotate(Direction.Rotation.Counterclockwise, speedy - .2, turn);
                dropoff();
                move(Direction.Left, speedy - .1, tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                move(Direction.Right, speedy, tile);
                move(Direction.Backward, speedy, 5 * tile / 6);
                rotate(Direction.Rotation.Counterclockwise, speedy - .1, turn);
                dropoff();
                move(Direction.Right, speedy - .1, tile / 3);
            }
        }


        if (!isRed && !isTop) {
            if ((vuMark == RelicRecoveryVuMark.CENTER)) {
                reset();
                while (!drive.driveWithEncoders(Direction.Right, speedy - .1, 3*tile/2) && opModeIsActive()) {
                }
                reset();
                while (!drive.rotateWithEncoders(Direction.Rotation.Clockwise, speedy, 2*turn) && opModeIsActive()) {
                }
                dropoff();
            }
            if ((vuMark == RelicRecoveryVuMark.RIGHT)) {
                reset();
                move(Direction.Right, speedy, 11 * tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy, 2*turn);
                dropoff();

                reset();
                move(Direction.Right, speedy - .1, tile / 3);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                reset();
                move(Direction.Right, speedy, 7 * tile / 6);
                rotate(Direction.Rotation.Clockwise, speedy, 2 * turn);
                dropoff();
                move(Direction.Left, speedy - .1, tile / 3);
                }
            }
        }



    }

