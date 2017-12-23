/*package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "Mecanum", group = "Iterative Opmode")
public class TeleOpMecanum extends OpMode {

    //Declare OpMode members.
    private MecanumDrive drive = new MecanumDrive();

    //private UltrasonicI2cRangeSensor sensor;

    private double adjustedSpeed;

    private DcMotor lift;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;
<<<<<<< HEAD
    private DcMotor arm;

=======
    private DcMotor relic;
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af

    private boolean overide;
    private boolean aToggle;

    private int liftPos;

    public Servo grabLeft;
    public Servo grabRight;
<<<<<<< HEAD
    public Servo relic;
=======
    public Servo pinch;
    public Servo jewel;
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af

    private boolean invert;
    private boolean pinchBool;
    private boolean a;
    private boolean x;

    private double num;

    @Override
    public void init() {
        drive.initialize(telemetry, hardwareMap);
        telemetry.update();

        adjustedSpeed = MecanumDrive.FULL_SPEED;

        num = 0;

        invert = false;
        overide = false;
<<<<<<< HEAD
        aToggle = false;
    }
    
    @Override
    public void loop() {
        liftLeft = hardwareMap.dcMotor.get("liftLeft");
        liftRight = hardwareMap.dcMotor.get("liftRight");
=======
        pinchBool = false;
        a = false;
        x = false;

        lift = hardwareMap.dcMotor.get("lift");
        relic = hardwareMap.dcMotor.get("relic");
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        pinch = hardwareMap.servo.get("pinch");
        jewel = hardwareMap.servo.get("jewel");
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void start() {
        telemetry.log().add("started tele");
    }

    @Override
    public void loop() {
        if (gamepad1.right_bumper){
            jewel.setPosition(.1);
        }
        if (gamepad1.left_bumper){
            jewel.setPosition(.86);
        }
<<<<<<< HEAD

=======
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af
        if (gamepad2.dpad_up) {
            liftPos = 12000;
        } else if (gamepad2.dpad_down) {
            liftPos = 0;
        } else if (gamepad2.dpad_left) {
            liftPos = 6000;
        } else if(gamepad2.dpad_right) {
            liftPos = 10600;
        }
        if (gamepad2.back){
            x = true;
        }
        if(x && gamepad2.dpad_up){
            lift.setPower(1);
        }
        else if(x && gamepad2.dpad_down){
            lift.setPower(-.5);
        }
        else if (x){
            lift.setPower(0);
        }


        if (0 < Math.abs(lift.getCurrentPosition() - liftPos) - 100) {
            if (liftPos < lift.getCurrentPosition()) {
                lift.setPower(-.5);
            } else if (liftPos > lift.getCurrentPosition()) {
                lift.setPower(1);
            }
        }
<<<<<<< HEAD
=======
        else {
            lift.setPower(0);
        }

        if (gamepad2.right_bumper) {
            relic.setPower(1);
        }
        else if (gamepad2.left_bumper) {
            relic.setPower(-1);
        }
        else {
            relic.setPower(0);
        }
        if (gamepad2.a && !a) {
            a = true;
            pinchBool = !pinchBool;
        } else if (!gamepad2.a & a){
            a = false;
        }
        if (pinchBool){
            pinch.setPosition(1);
        }
        else {
            pinch.setPosition(.4);












        }
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af

        if (drive.deadZone(gamepad2.right_trigger) > 0) {
            intakeLeft.setPower(1);
            intakeRight.setPower(-1);
        } else if (drive.deadZone(gamepad2.left_trigger) > 0) {
            intakeLeft.setPower(-1);
            intakeRight.setPower(1);
        } else {
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
        }
        if (gamepad2.b) {
            grabRight.setPosition(.9);
            grabLeft.setPosition(0);
        }
        if (gamepad2.x) {
            grabRight.setPosition(.1);
            grabLeft.setPosition(.6);
        }
        if (gamepad2.y) {
            grabRight.setPosition(.4);
            grabLeft.setPosition(.4);
        }
        if (gamepad1.a) {
            adjustedSpeed = .5;
        }
        if (gamepad1.b) {
            adjustedSpeed = 1;
        }
        if (gamepad1.x) {
            adjustedSpeed = .25;
        }
        /*if (gamepad1.right_bumper) {
            invert = false;
        }
        if (gamepad1.left_bumper) {
            invert = true;
<<<<<<< HEAD
        }
<<<<<<< HEAD
        if (gamepad2.right_bumper) {
            arm.setPower(.25);
        }
        else if (gamepad2.left_bumper){
            arm.setPower(-.25);
        }
        else {
            arm.setPower(0);
        }
        if (gamepad2.a) {
            aToggle = !aToggle;
        }
            /*num ++;
            if(num % 2 == 1) {
                relic.setPosition(1);
            }
            else{
                relic.setPosition(-1);
            }

        if(aToggle){
            relic.setPosition(1);
        } else {
            relic.setPosition(-1);
        }

        if (gamepad2.a || overide){
            overide = true;
            if (!drive.driveWithEncoders(Direction.Forward, .5, .2*Autonomous.tile)) {

            } else {
                grabLeft.setPosition(-1);
                grabRight.setPosition(1);
                overide = false;
            }
        } else {
            drive.drive(false, gamepad1,adjustedSpeed * MecanumDrive.FULL_SPEED, invert);
        }
=======
=======
        }
>>>>>>> 88208d81d21b3baf45a582bf828a65e656dc505f
        drive.drive(false, gamepad1, adjustedSpeed, false);
>>>>>>> aceebeb6c4c2cc2777c3c1f94295f2810883d2af
    }

    private void telemetryUpdate() {
        telemetry.addData("Speed mode", adjustedSpeed);
        telemetry.update();
    }
}*/