package org.firstinspires.ftc.team4042.drive;

import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.team4042.autos.C;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;
import org.opencv.core.Mat;

@TeleOp(name = "Mecanum", group="drive")
public class TeleOpMecanum extends OpMode {

    private double adjustedSpeed;


    private boolean aiPlacer = true;
    private boolean manual = false;
    private boolean onBalancingStone = false;

    private boolean intakeBackstop = true;
    private boolean backstopEngaged = false;
    private boolean ignoreInput = false;

    private double oops = 1; //switch to -1 if runs in wrong direction when going for balance

    //CONTROL BOOLEANS START
    // We have these booleans so we only register a button press once.
    // You have to let go of the button and push it again to register a new event.
    private boolean aRight = false;
    private double aBackTime = 0;
    private boolean bLeftStick = false;
    private boolean bRightStick = false;
    private boolean aDown = false;

    private boolean aA = false;
    private boolean aY = false;
    private boolean aX = false;
    private boolean aB = false;

    private boolean aUp = false;

    private boolean aLeftStick = false;
    private boolean aRightStick = false;

    private boolean bUp;
    private boolean bDown;
    private boolean bLeft;
    private boolean bRight;

    private boolean bA;
    private boolean bX;
    private boolean bB;
    private boolean bY;
    //CONTROL BOOLEANS END

    public Drive drive = new MecanumDrive(true);
    public ElapsedTime jewelTimer = new ElapsedTime();
    public ElapsedTime nanoTime = new ElapsedTime();
    public ElapsedTime banfTime = new ElapsedTime();

    private double startRoll;
    private double startPitch;

    private Camera cam;
    private Camera.Parameters onParams;
    private Camera.Parameters offParams;
    private boolean flashOn;
    private boolean flashBlink;
    private ElapsedTime blinkTimer = new ElapsedTime();
    private boolean runDown = false;
    private boolean movingUp = false;

    private int[] greyBrown = new int[] {0, 0};

    private int error = C.get().getInt("PlaceError");

    private double cursorCount = 1;
    
    private MyGamepad gamepadA = new MyGamepad();
    private MyGamepad gamepadB = new MyGamepad();

    /**
    GAMEPAD 1:
      Joystick 1 X & Y      movement
      Joystick 1 button     verbose
      Joystick 2 X          rotation
      Joystick 2 button     jewel stowed
      Bumpers               (extendo) external intakes backwards    (normal) both intakes backwards
      Triggers              (extendo) external intakes forwards     (normal) both intakes forwards
      Dpad up               turn off auto intake
      A
      B                     toggle tank
      X                     toggle crawl
      Y                     toggle extendo
      Start
      Back                  balance

    GAMEPAD 2:
     Stick 1 (manual)       controls placer in manual
     Stick 1 btn            toggle AI v manual target uTrack
     Stick 2 X (ai target)  glyph color designation
     Stick 2 btn            toggle automated v manual drive uTrack
     Bumpers (extendo)      internal intakes backwards
     Triggers (extendo)     halt placer
     A                      manual hand toggle
     B,X,Y (manual target)  target x uTrack
     Dpad (manual target)   target y uTrack
     Y (manual drive)       resets the glyph placer
     Y and dpad (ai target) sets the ui target to the selected glyph color
     X (ai target)          inverse target snake
     Dpad (ai target)       targets the ui for the ai
     Back                   toggle glyph reject/cipher break
     */

    @Override
    public void init() {
        try {
            drive.initialize(telemetry, hardwareMap);
            drive.runWithEncoders();
            drive.initializeGyro(telemetry, hardwareMap);
            drive.cryptobox.loadFile();

            telemetry.update();

            drive.targetY = GlyphPlacementSystem.Position.TOP;
            drive.targetX = GlyphPlacementSystem.HorizPos.LEFT;
            drive.stage = GlyphPlacementSystem.Stage.RESET;
            drive.glyph.setHomeTarget();

            adjustedSpeed = MecanumDrive.FULL_SPEED;

            gyro();
        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
        }
        cam = Camera.open();

        onParams = cam.getParameters();
        onParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        offParams = cam.getParameters();
        offParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        jewelTimer.reset();
    }

    @Override
    public void start() {
        try {
            //Raises the brakes
            drive.raiseBrakes();
            //Locks the catches
            drive.lockCatches();

            blinkTimer.reset();

            drive.jewelOut();
            drive.jewelCenter();

            drive.readSensorsSetUp();

            Drive.isExtendo = false;
            Drive.crawl = false;
            Drive.tank = false;
            Drive.ivan = true;
            runDown = false;

            drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());

        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
        }
    }
    
    @Override
    public void loop() {
        try {
            gamepadA.update(gamepad1);
            gamepadB.update(gamepad2);
            if (gamepadA.dpad_up && !aUp) {
                intakeBackstop = !intakeBackstop;
            }
            aUp = gamepadA.dpad_up;

            if (gamepadA.left_stick_button && !aLeftStick) {
                drive.toggleWinch();
            }
            aLeftStick = gamepadA.left_stick_button;

            telemetry.addData("winch", drive.winchOpen);

            //The first time you hit back, it establishes how long you've been pushing it for

            //If you're pushing back and have been for longer than "nano", then run the full balance code
            if (gamepadA.dpad_right && !gamepadA.a) {
                balance();
            } else {
                setUpDrive();
            }

            if (gamepadA.dpad_right && gamepadA.a) {
                onBalancingStone = false;
            }
            //If you've released back and did so for a shorter time than "nano", then toggle whether you're on the stone

            aRight = gamepadA.dpad_right;
            telemetry.addData("back", gamepad1.back);

            //Adjust drive modes, speeds, etc

            if (gamepadA.right_stick_button && !aRightStick) {
                drive.toggleVerbose();
            }
            aRightStick = gamepadA.right_stick_button;

            //Runs the intakes
            intakes();

            //Targets the AI's ui and (possibly) sets a glyph color override
            aiUi();

            //Runs the glyph placer
            glyphPlacer();

            //Blinks the flashlight, if needed
            blink();

            /*if (gamepadB.dpad_up && gamepadB.b && !bUp) {
                error += 20;
            } else if (gamepadB.dpad_down && gamepadB.b && !bDown) {
                error -= 20;
            }*/
            error = Range.clip(error, 0, 200);
            telemetry.addData("error", error);

            if ((gamepadB.dpad_left && gamepadB.a && (!bLeft || !bA)) || (gamepadB.dpad_right && gamepadB.a && (!bRight || !bA))) {
                runDown = !runDown;
                if (!runDown) {
                    drive.jewelOut();
                    jewelTimer.reset();
                    movingUp = true;
                }
            } if (movingUp && jewelTimer.seconds() > .45) {
                drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());
                drive.glyph.runToPosition(0);
                movingUp = false;
            }
            if (runDown) {
                drive.jewelStowed();
                drive.setVerticalDrivePos(GlyphPlacementSystem.Position.HOME.getEncoderVal());
                drive.glyph.runToPosition(0);
            }

            aA = gamepadA.a;

            bUp = gamepadB.dpad_up;
            bLeft = gamepadB.dpad_left;
            bDown = gamepadB.dpad_down;
            bRight = gamepadB.dpad_right;
            bB = gamepadB.b;
            bA = gamepadB.a;
            bY = gamepadB.y;
            bX = gamepadB.x;

            //Updates the telemetry output
            telemetryUpdate();
        } catch (Exception ex) {
            telemetry.log().add("Exception: " +  Drive.getStackTrace(ex));
        }

    }

    private void blink() {
        flashBlink = !aiPlacer && !manual;
        if (flashBlink) {
            if (Math.round(blinkTimer.seconds() * 2) % 2 == 0) {
                flashOn();
            } else {
                flashOff();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        drive.cryptobox.writeFile();
        cam.release();
    }

    public void gyro() {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        do {
            drive.gyro.updateAngles();
            startRoll = drive.gyro.getRoll();
            startPitch = drive.gyro.getPitch();
        } while (startRoll == 0 && startPitch == 0 && timer.seconds() < .5);
    }

    private boolean on = false;

    private void balance() {
        drive.gyro.updateAngles();
        double currRoll = drive.gyro.getRoll();
        double currPitch = drive.gyro.getPitch();
        telemetry.addData("currRoll", currRoll);
        telemetry.addData("currPitch", currPitch);

        boolean flat = Math.abs(currRoll - startRoll) < 9 && Math.abs(currPitch - startPitch) < 9;
        boolean veryFlat = Math.abs(currRoll - startRoll) < 7 && Math.abs(currPitch - startPitch) < 7;
        //veryFlat = true;

        telemetry.addData("flat", flat);
        telemetry.addData("startPitch", startPitch);
        telemetry.addData("startRoll", startRoll);

        if (!onBalancingStone && !flat) {
            //If you get tipped, you must be on the balancing stone and we flag you as such
            onBalancingStone = true;
            on = false;
            banfTime.reset();
        } else if (!onBalancingStone && flat) {
            //If you're just getting on or you're on the ground, run back hard
            drive.driveXYR(1, 0, -1, 0, false);
        } else if (false && banfTime.seconds() > 1) {
            //Move away from which way you're tipped (should go towards the center)
            double x = Math.abs(startPitch - currPitch) > Math.abs(startRoll - currRoll) ? (startPitch - currPitch) > 0 ? .1 : -.1 : 0;
            double y = Math.abs(startPitch - currPitch) < Math.abs(startRoll - currRoll) ? (startRoll - currRoll) > 0 ? .75 : -.75 : 0;
            telemetry.addData("y", y);
            telemetry.addData("x", x);
            drive.driveXYR(1, x, y, 0, false);
            banfTime.reset();

        } else {
            //adjust
            //double degreeP = .05;
            //If you're on the balancing stone and not quite flat, then adjust
            double degreeP = C.get().getDouble("degree");
            double x = 2 * degreeP * (startPitch - currPitch);
            double y = degreeP * (startRoll - currRoll);
            veryFlat = !on && veryFlat;
            on = on || veryFlat;
            if (on && veryFlat) {
                banfTime.reset();
            }
            if (on && banfTime.seconds() < .2){
                x = 0;
                y = 0;
            }
            telemetry.addData("y", y);
            telemetry.addData("x", x);
            drive.driveXYR(1, x, y, 0, false);
        }
    }

    private void setUpDrive() {
        drive.uTrackUpdate();
        //drive.updateRates();

        if (gamepadA.dpad_down && !aDown) {
            Drive.ivan = !Drive.ivan;
        }
        aDown = gamepadA.dpad_down;

        //First controller pushing Y - toggle extendo
        if ((gamepadA.a && !aA && gamepadA.y) || (gamepadA.y && !aY && gamepadA.a)) {
            drive.toggleServoExtendo();
        } else if (gamepadA.y && !aY && !gamepadA.a) {
            drive.toggleExtendo();
        } else if (gamepadA.y) {
            drive.extendoStep();
        } else {
            //Drives the robot
            drive.drive(false, gamepadA, gamepadB, adjustedSpeed * MecanumDrive.FULL_SPEED);
        }
        aY = gamepadA.y;

        //The X button on the first controller - toggle crawling to let us adjust the back of the robot too
        if (gamepadA.x && !aX) {
            Drive.crawl = !Drive.crawl;
            if (Drive.crawl) {
                drive.freezeBack();
            } else {
                drive.runBackWithEncoders();
            }
        }
        aX = gamepadA.x;

        if (gamepadA.b && !aB) {
            Drive.tank = !Drive.tank;
        }
        aB = gamepadA.b;

        if (Drive.useGyro) {
            drive.useGyro(0);
        }
    }

    private void aiUi() {
        if (gamepadB.dpad_up && !bUp) {drive.cryptobox.uiUp();}
        if (gamepadB.dpad_left && !bLeft) {drive.cryptobox.uiLeft();}
        if (gamepadB.dpad_down && !bDown) {drive.cryptobox.uiDown();}
        if (gamepadB.dpad_right && !bRight) {drive.cryptobox.uiRight();}

        if (aiPlacer && !gamepadB.y && !gamepadB.b && gamepadB.x) {
            if (gamepadB.left_stick_y > .7 && Math.abs(gamepadB.left_stick_x) < .7) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.BROWN);
            }
            if (gamepadB.left_stick_y < -.7 && Math.abs(gamepadB.left_stick_x) < .7) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.GREY);
            }
            if (Math.abs(gamepadB.left_stick_x) > .7 && Math.abs(gamepadB.left_stick_y) < .7) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.NONE);
                if (!drive.uTrackAtBottom) {
                    drive.abort = true;
                }
            }
        }
    }

    private void glyphPlacer() {
        //If you're at the bottom, haven't been pushing a, and now are pushing a

        if (gamepadB.a && !bA) {
            drive.toggleHand();
        }
        if (gamepadB.right_stick_button && !bRightStick) {
            toggleManual();
        }

        if (gamepadB.left_stick_button && !bLeftStick) {
            aiPlacer = !aiPlacer;
            if (aiPlacer) {
                flashOff();
                flashOn = false;
                flashBlink = false;
            }
            telemetry.log().add("right!");
        }
        bRightStick = gamepadB.right_stick_button;
        bLeftStick = gamepadB.left_stick_button;

        if (manual) {
            runManualPlacer();
        }
        else {
            //Glyph locate
            if (aiPlacer) {
                runAiPlacer();
            } else {
                glyphTarget();
            }
            if (!drive.stage.equals(GlyphPlacementSystem.Stage.RESET)){
                drive.glyph.runToPosition(0, error);
            }
        }
    }

    /**
     * Toggles manual mode, incl. the flashlight and the placer mode
     */
    private void toggleManual() {
        manual = !manual;

        //Switching out of manual
        if (!manual) {
            drive.setVerticalDrive(0);
            drive.setHorizontalDrive(0);
            drive.setVerticalDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.glyph.setAboveHomeTarget();
            drive.stage = GlyphPlacementSystem.Stage.RESET;
            drive.uTrackAtBottom = true;

            // Turn off flashlight
            if (flashOn) {
                flashOff();
                flashOn = false;
            }
        } else {
            // Turn on flashlight
            if (!flashOn) {
                flashOn();
                flashOn = true;
            }

            telemetry.log().add("switching into manual");

            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            targetPos = drive.verticalDriveCurrPos();
        }
    }

    private void flashOff() {
        cam.setParameters(offParams);
        cam.stopPreview();
    }

    private void flashOn() {
        cam.setParameters(onParams);
        cam.startPreview();
    }

    private int targetPos = 0;

    /**
     * Handles the placer in manual mode
     */
    private void runManualPlacer() {
        if (bY && !gamepadB.y) { //When you release Y, reset the utrack
            drive.resetUTrack();
            drive.glyph.setHomeTarget();
            targetPos = 0;
        } else if (gamepadB.y) { //If you're holding y, run the utrack downwards
            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.setVerticalDrive(-0.5);
        } else {
            double horiz = Math.pow(gamepadB.left_stick_x, 3);
            double vertical = Math.pow(gamepadB.right_stick_y, 3);
            targetPos = drive.verticalDriveCurrPos();
            if (Math.abs(vertical) > drive.DEADZONE_SIZE) {
                drive.setVerticalDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
                drive.setVerticalDrive(vertical);
            } else {
                drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                drive.setVerticalDrivePos(targetPos);
                drive.glyph.runToPosition(0);
            }
            if (Math.abs(horiz) > drive.DEADZONE_SIZE) {
                drive.setHorizontalDrive(horiz);
            } else {
                drive.setHorizontalDrive(0);
            }
        }
        bY = gamepadB.y;
    }

    /**
     * Handles the ai glyph placement
     */
    private void runAiPlacer() {
        if (gamepadB.x && gamepadB.b) {
            drive.cryptobox.setCipherTarget(Cryptobox.Cipher.NONE);
        }

        greyBrown = drive.uTrackAutoTarget(gamepadB);

        if (aiPlacer && gamepadB.y && !gamepadB.b && !gamepadB.x) {
            if (gamepadB.left_stick_y > .7 && Math.abs(gamepadB.left_stick_x) < .7) {
                drive.cryptobox.wrongLastGlyph(Cryptobox.GlyphColor.BROWN);
                drive.switchColor = drive.color.equals(Cryptobox.GlyphColor.GREY);
                drive.abort = false;
            }
            if (gamepadB.left_stick_y < -.7 && Math.abs(gamepadB.left_stick_x) < .7) {
                drive.cryptobox.wrongLastGlyph(Cryptobox.GlyphColor.GREY);
                drive.switchColor = drive.color.equals(Cryptobox.GlyphColor.BROWN);
                drive.abort = false;
            }
            if (Math.abs(gamepadB.left_stick_x) > .7 && Math.abs(gamepadB.left_stick_y) < .7) {
                drive.cryptobox.wrongLastGlyph(Cryptobox.GlyphColor.NONE);
                if (!drive.uTrackAtBottom) {
                    drive.abort = true;
                    drive.openHand();
                }
            }
        }
    }

    /**
     * Handles the first frame of placing a glyph using the ai
     * @param nextGlyph The predictions from the glyph placer
     * @param numPlaces The number of glyphs placed thus far
     */

    //Deprecated: see runAiPlacer
    private void aiGlyphPlace(int[] nextGlyph, int numPlaces) {
        this.greyBrown = nextGlyph;
        telemetry.log().add("greybrown: [" + nextGlyph[0] + ", " + nextGlyph[1] + "]");
        if (nextGlyph[0] == -1 && nextGlyph[1] == -1) {
            return;
        }
        if(!(((nextGlyph[0] + nextGlyph[1]) == 0) && !(numPlaces == 11))) {
            drive.uTrack();
        }
    }
    private void glyphTarget() {
        int targetX = gamepadB.x ? 0 : (gamepadB.y ? 1 : (gamepadB.b ? 2 : -1));
        int targetY = gamepadB.dpad_up ? 0 : (gamepadB.dpad_left || gamepadB.dpad_right ? 1 : (gamepadB.dpad_down ? 2 : -1));
        if (targetX != -1 && targetY != -1 && drive.uTrackAtBottom && ((!bRight & !bLeft & !bUp & !bDown) | (!bB & !bX & !bY))) {
            drive.glyph.uiTarget(targetX, targetY);
            drive.glyphLocate();
            drive.uTrack();
        } else if (targetX != -1 && targetY != -1  && !drive.uTrackAtBottom) {
            drive.uTrack();
        } else if (drive.uTrackAtBottom && drive.verticalDriveTargetPos() != GlyphPlacementSystem.Position.HOME.getEncoderVal()) {
            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());
            drive.glyph.runToPosition(0);
        }
    }

    private void glyphUI() {
        if (gamepadB.dpad_up && !bUp) { drive.glyph.uiUp(); }
        if (gamepadB.dpad_down && !bDown) { drive.glyph.uiDown(); }
        if (gamepadB.dpad_left && !bLeft) { drive.glyph.uiLeft(); }
        if (gamepadB.dpad_right && !bRight) { drive.glyph.uiRight(); }
    }

    private void intakes() {
        double aRightTrigger = drive.deadZone(gamepadA.right_trigger);
        boolean aRightBumper = gamepadA.right_bumper;
        double aLeftTrigger = drive.deadZone(gamepadA.left_trigger);
        boolean aLeftBumper = gamepadA.left_bumper;

        double bRightTrigger = drive.deadZone(gamepadB.right_trigger);
        boolean bRightBumper = gamepadB.right_bumper;
        double bLeftTrigger = drive.deadZone(gamepadB.left_trigger);
        boolean bLeftBumper = gamepadB.left_bumper;

        drive.shortIr[1].addReading();
        double backDistance = drive.shortIr[1].getCmAvg();
        boolean isGlyphBack = backDistance <= C.get().getDouble("glyphBackThreshold");

        for (AnalogSensor lineFollow : drive.lineFollow){
            lineFollow.addReading();
        }

        /*
         * Follow driver input -> If the backstop is off || (the backstop has been engaged && the driver has released the inputs && the driver is inputting things)
         * Engage the backstop, ignore immediate input, stop the intakes -> If a glyph is seen && the backstop is on && the backstop isn't engaged
         * Allow inputs again -> If the driver isn't inputting anything
         * Stop the intakes -> If a glyph is seen && the backstop is on && the backstop is engaged && (no driver inputs || ignoring immediate inputs)
         * Disengage the backstop -> If there's no cube in the intakes
         */
        boolean isDriverInput = Drive.isExtendo && (aLeftBumper || aRightBumper || aRightTrigger > .9 || aLeftTrigger > .9);

        //Allows inputs again
        if (ignoreInput && !isDriverInput) {
            ignoreInput = false;
        }
        //Disengage the backstop
        if (!isGlyphBack) {
            backstopEngaged = false;
        }
        //Engage the backstop, ignore immediate input, stop the intakes
        if (intakeBackstop && isGlyphBack && !backstopEngaged) {
            drive.intakeLeft(0);
            drive.intakeRight(0);
            backstopEngaged = true;
            ignoreInput = true;
        }
        //Stop the intakes
        if (intakeBackstop && isGlyphBack && backstopEngaged && (ignoreInput || !isDriverInput)) {
            drive.intakeLeft(0);
            drive.intakeRight(0);
        }
        //Follow driver inputs
        if (!intakeBackstop || !ignoreInput) {

            if (aRightTrigger > Drive.DEADZONE_SIZE) {
                drive.intakeRight(aRightTrigger);
                if (!Drive.isExtendo) { drive.internalIntakeRight(aRightTrigger); }
            } else if (aRightBumper) {
                drive.intakeRight(-1);
                if (!Drive.isExtendo) { drive.internalIntakeRight(-1); }
            } else {
                drive.intakeRight(0);
                if (!Drive.isExtendo && (drive.stage.equals(GlyphPlacementSystem.Stage.HOME) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.GRAB) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1))) {
                    drive.internalIntakeRight(.5);
                }else if (!Drive.isExtendo) {
                    drive.internalIntakeRight(0);
                }
            }

            if (aLeftTrigger > Drive.DEADZONE_SIZE) {
                drive.intakeLeft(aLeftTrigger);
                if (!Drive.isExtendo) { drive.internalIntakeLeft(aLeftTrigger); }
            } else if (aLeftBumper) {
                drive.intakeLeft(-1);
                if (!Drive.isExtendo) { drive.internalIntakeLeft(-1); }
            } else {
                drive.intakeLeft(0);
                if (!Drive.isExtendo && (drive.stage.equals(GlyphPlacementSystem.Stage.HOME) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.GRAB) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1))) {
                    drive.internalIntakeLeft(.5);
                }else if (!Drive.isExtendo) {
                    drive.internalIntakeLeft(0);
                }
            }
        }

        if (Drive.isExtendo) {
            if (bRightTrigger > Drive.DEADZONE_SIZE) { drive.internalIntakeRight(bRightTrigger); }
            //Right bumper of the b controller runs the right intake backwards
            else if (bRightBumper) { drive.internalIntakeRight(-1); }
            else if (drive.stage.equals(GlyphPlacementSystem.Stage.HOME) ||
                    drive.stage.equals(GlyphPlacementSystem.Stage.GRAB) ||
                    drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1)) {
                drive.internalIntakeRight(.5);
            } else { drive.internalIntakeRight(1); }

            //Left trigger of the b controller runs the left intake forward
            if (bLeftTrigger > Drive.DEADZONE_SIZE) { drive.internalIntakeLeft(bLeftTrigger); }
            //Left bumper of the b controller runs the left intake backwards
            else if (bLeftBumper) { drive.internalIntakeLeft(-1); }
            else if (drive.stage.equals(GlyphPlacementSystem.Stage.HOME) ||
                    drive.stage.equals(GlyphPlacementSystem.Stage.GRAB) ||
                    drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1)) {
                drive.internalIntakeLeft(.5);
            } else { drive.internalIntakeLeft(1); }
        }
    }

    private void telemetryUpdate() {
        cursorCount += .2;
        telemetry.addData("Manual", manual);
        telemetry.addData("Crawl", Drive.crawl);
        telemetry.addData("AI", aiPlacer);
        telemetry.addData("Ivan Extendo Drive", Drive.ivan);
        telemetry.addData("Cryptobox", drive.cryptobox == null ? "" : drive.cryptobox.uiToString((int) cursorCount % 2 == 0));
        printNextGlyph();
        //telemetry.addData("Reject glyph", drive.cryptobox.getRejectGlyph());
        Cryptobox.Cipher snakeTarget = drive.cryptobox.getCipherTarget();
        telemetry.addData("Snake target", snakeTarget == null ? "null" : snakeTarget.name());
        //telemetry.addData("line follower", drive.smallVoltage);
        telemetry.addData("vert Pos", drive.verticalDriveCurrPos());
        if (drive.verbose) {
            telemetry.addData("gamepadA.dpad_up", gamepadA.dpad_up);
            telemetry.addData("bottom", drive.getBottomState());
            telemetry.addData("center", drive.getCenterState());
            telemetry.addData("side", drive.getSideState());
            telemetry.addData("collected", drive.getCollectedState());
            telemetry.addData("cursorCount", (int) cursorCount);
            telemetry.addData("bLeftStick", bLeftStick);
            telemetry.addData("gamepadB.left_stick_button", gamepadB.left_stick_button);
            telemetry.addData("!bLeftStick && gamepadB.left_stick_button", !bLeftStick && gamepadB.left_stick_button);
            telemetry.addData("!bRightStick && gamepadB.right_stick_button", !bRightStick && gamepadB.right_stick_button);
            telemetry.addData("extendoTimer", drive.extendoTimer.seconds());
            telemetry.addData("numGlyphsCollected", drive.cryptobox.getNumGlyphsPlaced());
            telemetry.addData("onStone", onBalancingStone);
        }
        if (Drive.useGyro) {
            telemetry.addData("gyro", drive.gyro.updateHeading());
        }
        telemetry.update();
    }

    private void printNextGlyph() {
        if (greyBrown.length == 2) {
            int grey = greyBrown[0];
            int brown = greyBrown[1];
            if (grey == 0 && brown == 0) {
                telemetry.addData("Next glyph", "NO GLYPH");
            } else if (grey == 0) {
                telemetry.addData("Next glyph", "NEED BROWN");
            } else if (brown == 0) {
                telemetry.addData("Next glyph", "NEED GREY");
            } else if (grey == brown) {
                telemetry.addData("Next glyph", "Either");
            } else if (grey > brown) {
                telemetry.addData("Next glyph", "Want grey");
            } else if (grey < brown) {
                telemetry.addData("Next glyph", "Want brown");
            }
        }
    }
}