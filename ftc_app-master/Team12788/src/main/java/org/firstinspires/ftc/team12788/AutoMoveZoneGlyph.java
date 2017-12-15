package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Zone Glyph", group="Linear Opmode")
public class AutoMoveZoneGlyph extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    //private VuMarkIdentifier mark = new VuMarkIdentifier();

    private DcMotor lift;
    //private DcMotor intakeRight;
    //private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;

    //private RelicRecoveryVuMark vuMark;

    public void reset(){
        drive.resetEncoders();
        sleep(500);
        drive.runWithoutEncoders();
        sleep(500);
    }

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);

        lift = hardwareMap.dcMotor.get("lift");
        /*intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");*/
        drive.runWithoutEncoders();
        grabLeft = hardwareMap.servo.get("grabLeft");
        grabRight = hardwareMap.servo.get("grabRight");
        //mark.initialize(telemetry, hardwareMap);
        waitForStart();
        grabLeft.setPosition(1);
        grabRight.setPosition(.2);
        sleep(1000);
        lift.setPower(1);
        sleep(500);
        lift.setPower(0);
        sleep(1000);
        while(!drive.driveWithEncoders(Direction.Backward, Autonomous.speedy-.2, 1.5*Autonomous.tile) && opModeIsActive());
        sleep(2000);
        reset();
        grabLeft.setPosition(.6);
        grabRight.setPosition(.7);
        sleep(1000);
        while(!drive.driveWithEncoders(Direction.Forward, Autonomous.speedy, .3*Autonomous.tile) && opModeIsActive());
        reset();


    }
}