package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Zone Only", group="Linear Opmode")
public class AutoMoveZone extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    private VuMarkIdentifier mark = new VuMarkIdentifier();

    private DcMotor liftLeft;
    private DcMotor liftRight;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;

    //private RelicRecoveryVuMark vuMark;

    @Override
    public void runOpMode() {
        drive.initialize(telemetry, hardwareMap);
        //liftLeft = hardwareMap.dcMotor.get("liftLeft");
        //liftRight = hardwareMap.dcMotor.get("liftRight");
        //intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        //intakeRight = hardwareMap.dcMotor.get("intakeRight");
        drive.runWithoutEncoders();
        //grabLeft = hardwareMap.servo.get("grabLeft");
        //grabRight = hardwareMap.servo.get("grabRight");
        //mark.initialize(telemetry, hardwareMap);
        waitForStart();
        while(!drive.driveWithEncoders(Direction.Forward, Autonomous.speedy-.1, Autonomous.tile) && opModeIsActive());
        sleep(2000);
        while(!drive.rotateWithEncoders(Direction.Rotation.Clockwise, Autonomous.speedy-.2, Autonomous.turn) && opModeIsActive());
    }
}