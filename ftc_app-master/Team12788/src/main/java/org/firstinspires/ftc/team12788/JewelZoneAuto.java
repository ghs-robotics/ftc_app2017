package org.firstinspires.ftc.team12788;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

<<<<<<< HEAD
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
=======
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
>>>>>>> e3409f252d79ba5be186247f68afaf2e1a4a69ac


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Zone Only", group="Linear Opmode")
public class JewelZoneAuto extends LinearVisionOpMode {

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

    private RelicRecoveryVuMark vuMark;

    public String getBallColor(Mat frame){
        telemetry.addData("size", frame.height() + " x " + frame.width());
        Imgproc.resize(frame, frame, new Size(960, 720));
        telemetry.update();
        Rect left_crop = new Rect(new Point(215,585), new Point(380, 719));
        Rect right_crop = new Rect(new Point(460,585), new Point(620, 719));

        Log.d("stupid", this.getFrameSize().width + " x " + this.getFrameSize().height);
        Mat right = new Mat(frame, right_crop);
        Mat left = new Mat(frame, left_crop);


        String result = "unspecified";
        Scalar left_colors  = Core.sumElems(left);
        Scalar right_colors = Core.sumElems(right);

        if(left_colors.val[0] >= left_colors.val[2]){
            result = "red";
        } else {
            result = "blue";
        }

        if(right_colors.val[0] >= right_colors.val[2]){
            result = result.concat(", red");
        } else {
            result = result.concat(", blue");
        }

        return result;
    }

    public boolean isBallRight() {
        try {
            String balls = getBallColor(getFrameRgba());
            discardFrame();
            telemetry.addData("ball orientation", balls);
            switch (balls) {
                case "red":
                    return false;
                case "blue":
                    return true;
                case "red, blue":
                    return false;
                case "blue, red":
                    return true;
                case ", blue":
                    return false;
                case ", red":
                    return true;
            }
        } catch (CvException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            telemetry.addData("CvException", sw.toString());
        }
        return true;
    }

    public void waitMili(int time) {
        try {
            sleep(time);
        } catch (InterruptedException ex) { }
    }

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
<<<<<<< HEAD
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







=======

        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(900, 900));
        //enableExtension(Extensions.BEACON);
        enableExtension(Extensions.ROTATION);
        enableExtension(Extensions.CAMERA_CONTROL);
        rotation.setIsUsingSecondaryCamera(false);
        rotation.disableAutoRotate();
        rotation.setActivityOrientationFixed(ScreenOrientation.LANDSCAPE);
        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
        cameraControl.setAutoExposureCompensation();

        try {
            waitForStart();
        } catch (InterruptedException ex) { }
        while(!drive.driveWithEncoders(Direction.Forward, Autonomous.speedy-.1, 1.25 * Autonomous.tile) && opModeIsActive());
        waitMili(2000);
>>>>>>> e3409f252d79ba5be186247f68afaf2e1a4a69ac

    }
}
