package org.firstinspires.ftc.team12788;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Zone Only", group="Linear Opmode")
public class JewelZoneAuto extends LinearOpMode {

    private MecanumDrive drive = new MecanumDrive();
    private VuMarkIdentifier mark = new VuMarkIdentifier();

    private DcMotor liftLeft;
    private DcMotor liftRight;
    private DcMotor intakeRight;
    private DcMotor intakeLeft;

    public Servo grabLeft;
    public Servo grabRight;
    public Servo jewel;
    private RelicRecoveryVuMark vuMark;

    DigitalSensor whisker = new DigitalSensor("whisker");

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
    waitForStart();}





        public void dropoff(){
            while (!drive.driveWithEncoders(Direction.Backward, Autonomous.speedy, 9 * Autonomous.tile / 24) && opModeIsActive()) ;
            grabLeft.setPosition(.5);
            grabRight.setPosition(-.1);
            while (!drive.driveWithEncoders(Direction.Forward , Autonomous.speedy, 9 * Autonomous.tile / 24) && opModeIsActive()) ;
        }



    public void JewelWhisker(boolean isRed, boolean isTop) {
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
        vuMark = mark.getMark();
        if (isRed && !isTop) {

            while (!whisker.getState()) {
                drive.driveXYR(.7, 0, 1, 0);
                while ((drive.rotateWithEncoders(Direction.Rotation.Clockwise,Autonomous.speedy-.2,Autonomous.turn)) && opModeIsActive());
            }

            if (vuMark == RelicRecoveryVuMark.LEFT) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,1*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,3*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,5*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }


        }
        if (!isRed && !isTop) {
            while (!whisker.getState()) {
                drive.driveXYR(.7, 0, 1, 0);
                while ((drive.rotateWithEncoders(Direction.Rotation.Counterclockwise, Autonomous.speedy - .2, Autonomous.turn)) && opModeIsActive()) ;
            }
                if (vuMark == RelicRecoveryVuMark.LEFT) {
                    while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,1*Autonomous.tile/6) && opModeIsActive());
                    dropoff();
                }
                if (vuMark == RelicRecoveryVuMark.CENTER) {
                    while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,3*Autonomous.tile/6) && opModeIsActive());
                    dropoff();
                }
                if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,5*Autonomous.tile/6) && opModeIsActive());
                    dropoff();
                }
            }

        if (isRed && isTop) {
            while (!whisker.getState()) {
                drive.driveXYR(.7, 0, 1, 0);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,5*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,3*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                while (drive.driveWithEncoders(Direction.Left, Autonomous.speedy,1*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
        }
        if (!isRed && isTop) {
            while (!whisker.getState()) {
                drive.driveXYR(.7, 0, 1, 0);
            }
            if (vuMark == RelicRecoveryVuMark.LEFT) {
                while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,1*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.CENTER) {
                while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,3*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                while (drive.driveWithEncoders(Direction.Right, Autonomous.speedy,5*Autonomous.tile/6) && opModeIsActive());
                dropoff();
            }
        }








    }
}
