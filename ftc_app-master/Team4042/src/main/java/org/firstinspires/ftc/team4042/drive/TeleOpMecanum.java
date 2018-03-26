package org.firstinspires.ftc.team4042.drive;

import android.hardware.Camera;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.team4042.autos.C;
import org.firstinspires.ftc.team4042.sensor.AnalogSensor;

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
    private boolean aBack = false;
    private boolean bBack = false;
    private double aBackTime = 0;
    private boolean bLeftStick = false;
    private boolean bRightStick = false;

    private boolean aA = false;
    private boolean aY = false;
    private boolean aX = false;
    private boolean aB = false;

    private boolean aUp = false;
    private boolean aDown = false;
    private boolean aLeft = false;
    private boolean aRight = false;

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

    private boolean bRightStickY;
    private boolean bBrown;
    //CONTROL BOOLEANS END

    public Drive drive = new MecanumDrive(true);
    public ElapsedTime jewelTimer = new ElapsedTime();

    private double startRoll;
    private double startPitch;

    private Camera cam;
    private Camera.Parameters onParams;
    private Camera.Parameters offParams;
    private boolean flashOn;
    private boolean runDown = false;
    private boolean movingUp = false;

    private int[] greyBrown = new int[] {0, 0};

    private int error = C.get().getInt("PlaceError");

    private double cursorCount = 1;

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

            drive.jewelOut();
            drive.jewelCenter();

            drive.readSensorsSetUp();

            Drive.isExtendo = false;
            Drive.crawl = false;
            Drive.tank = false;
            runDown = false;

            drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());

        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
        }
    }
    
    @Override
    public void loop() {
        try {
            if (gamepad1.dpad_up && !aUp) {
                intakeBackstop = !intakeBackstop;
            }

            if (gamepad1.dpad_down && !aDown) {
                drive.toggleWinch();
            }
            aDown = gamepad1.dpad_down;
            telemetry.addData("winch", drive.winchOpen);

            //The first time you hit back, it establishes how long you've been pushing it for
            if (gamepad1.back && !aBack) {
                aBackTime = System.nanoTime();
            }
            //If you're pushing back and have been for longer than "nano", then run the full balance code
            if (gamepad1.back && aBack) {
                if (System.nanoTime() - aBackTime > C.get().getDouble("nano")) {
                    balance();
                }
            }
            //If you've released back and did so for a shorter time than "nano", then toggle whether you're on the stone
            if (!gamepad1.back && aBack) {
                if (System.nanoTime() - aBackTime < C.get().getDouble("nano")) {
                    onBalancingStone = !onBalancingStone;
                }
            }

            //Adjust drive modes, speeds, etc
            setUpDrive();

            if (gamepad1.left_stick_button && !aLeftStick) {
                drive.toggleVerbose();
            }

            /*
            if (gamepad2.back && !bBack) {
                drive.cryptobox.toggleRejectGlyph();
            }*/

            //Runs the intakes
            intakes();

            //Targets the AI's ui and (possibly) sets a glyph color override
            aiUi();

            //Runs the glyph placer
            glyphPlacer();

            //telemetry.addData("runDown", runDown);
            //telemetry.addData("jewelTimer", jewelTimer.seconds());
            if (gamepad2.dpad_up && gamepad2.b && !bUp) {
                error += 20;
            } else if (gamepad2.dpad_down && gamepad2.b && !bDown) {
                error -= 20;
            }
            error = Range.clip(error, 0, 200);
            telemetry.addData("error", error);

            if ((gamepad1.dpad_left && gamepad1.a && (!aLeft || !aA)) || (gamepad1.dpad_right && gamepad1.a && (!aRight || !aA))) {
                runDown = !runDown;
                if (!runDown) {
                    drive.jewelOut();
                    jewelTimer.reset();
                    movingUp = true;
                }
            } if (movingUp && jewelTimer.seconds() > .45) {
                drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());
                drive.glyph.runToPosition(0);
                movingUp = false;
            }
            if (runDown) {
                drive.jewelStowed();
                drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                drive.setVerticalDrivePos(GlyphPlacementSystem.Position.HOME.getEncoderVal());
                drive.glyph.runToPosition(0);
            }

            aLeft = gamepad1.dpad_left;
            aRight = gamepad1.dpad_right;
            aA = gamepad1.a;

            updateControlBooleans();

            //Updates the telemetry output
            telemetryUpdate();
        } catch (Exception ex) {
            telemetry.addData("Exception", Drive.getStackTrace(ex));
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

    private void balance() {
        drive.gyro.updateAngles();
        double currRoll = drive.gyro.getRoll();
        double currPitch = drive.gyro.getPitch();
        telemetry.addData("currRoll", currRoll);
        telemetry.addData("currPitch", currPitch);

        boolean flat = Math.abs(currRoll - startRoll) < 2 && Math.abs(currPitch - startPitch) < 2;
        boolean veryTipped = Math.abs(currRoll - startRoll) > 8 || Math.abs(currPitch - startPitch) > 8;
        telemetry.addData("flat", flat);
        if (!onBalancingStone && !flat) {
            //If you get tipped, you must be on the balancing stone and we flag you as such
            onBalancingStone = true;
        } else if (!onBalancingStone && flat) {
            //If you're just getting on or you're on the ground, run back hard
            drive.driveXYR(1, 0, -1, 0, true);
        } else if (veryTipped) {
            //Move away from which way you're tipped (should go towards the center)
            double y = oops*2*(Math.ceil((currRoll-startRoll)/100)-.5);
            telemetry.addData("y", y);
            drive.driveXYR(1, 0, y, 0, true);
        } else {
            //adjust
            //double degreeP = .05;
            //If you're on the balancing stone and not quite flat, then adjust
            double degreeP = C.get().getDouble("degree");
            double x = degreeP * (startPitch - currPitch);
            double y = degreeP * (startRoll - currRoll);
            drive.driveXYR(1, x, y, 0, true);
        }
    }

    private void setUpDrive() {
        drive.uTrackUpdate();
        //drive.updateRates();

        //First controller pushing Y - toggle extendo
        if ((gamepad1.a && !aA && gamepad1.y) || (gamepad1.y && !aY && gamepad1.a)) {
            drive.toggleServoExtendo();
        } else if (gamepad1.y && !aY && !gamepad1.a) {
            drive.toggleExtendo();
        } else if (gamepad1.y) {
            drive.extendoStep();
        } else {
            //Drives the robot
            drive.drive(false, gamepad1, gamepad2, adjustedSpeed * MecanumDrive.FULL_SPEED);
        }
        aY = gamepad1.y;

        //The X button on the first controller - toggle crawling to let us adjust the back of the robot too
        if (gamepad1.x && !aX) {
            Drive.crawl = !Drive.crawl;
            if (Drive.crawl) {
                drive.freezeBack();
            } else {
                drive.runBackWithEncoders();
            }
        }
        aX = gamepad1.x;

        if (gamepad1.b && !aB) {
            Drive.tank = !Drive.tank;
        }
        aB = gamepad1.b;

        if (Drive.useGyro) {
            drive.useGyro(0);
        }
    }

    private void aiUi() {
        if (gamepad2.dpad_up && !bUp) {
            if (gamepad2.y) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.GREY);
            } else {
                drive.cryptobox.uiUp();
            }
        }
        if (gamepad2.dpad_left && !bLeft) {
            if (gamepad2.y) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.NONE);
            } else {
                drive.cryptobox.uiLeft();
            }
        }
        if (gamepad2.dpad_down && !bDown) {
            if (gamepad2.y) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.BROWN);
            } else {
                drive.cryptobox.uiDown();
            }
        }
        if (gamepad2.dpad_right && !bRight) {
            if (gamepad2.y) {
                drive.cryptobox.setGlyphAtUi(Cryptobox.GlyphColor.NONE);
            } else {
                drive.cryptobox.uiRight();
            }
        }
    }

    private void glyphPlacer() {
        //If you're at the bottom, haven't been pushing a, and now are pushing a

        if (gamepad2.a && !bA) {
            drive.toggleHand();
        }
        if (gamepad2.left_stick_button && !bLeftStick) {
            toggleManual();
        }

        if (gamepad2.right_stick_button && !bRightStick) {
            aiPlacer = !aiPlacer;
            telemetry.log().add("right!");
        }
        bRightStick = gamepad2.right_stick_button;

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
            //drive.glyph.setAboveHomeTarget();
            drive.stage = GlyphPlacementSystem.Stage.RESET;
            done = true;
            drive.uTrackAtBottom = true;

            // Turn off flashlight
            if (flashOn) {
                cam.setParameters(offParams);
                cam.stopPreview();
                flashOn = false;
            }
        } else {
            // Turn on flashlight
            if (!flashOn) {
                cam.setParameters(onParams);
                cam.startPreview();
                flashOn = true;
            }

            telemetry.log().add("switching into manual");

            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /**
     * Handles the placer in manual mode
     */
    private void runManualPlacer() {
        if (bY && !gamepad2.y) { //When you release Y, reset the utrack
            drive.resetUTrack();
            drive.glyph.setHomeTarget();
        } else if (gamepad2.y) { //If you're holding y, run the utrack downwards
            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
            drive.setVerticalDrive(-0.5);
        } else {
            double horiz = gamepad2.left_stick_x;
            double vertical = gamepad2.left_stick_y;
            if (Math.abs(vertical) > Math.abs(horiz)) {
                drive.setVerticalDrive(vertical);
                drive.setHorizontalDrive(0);
            } else if (Math.abs(horiz) > Math.abs(vertical)) {
                drive.setHorizontalDrive(horiz);
                drive.setVerticalDrive(0);
            } else {
                drive.setVerticalDrive(vertical);
                drive.setHorizontalDrive(horiz);
            }
        }
    }

    /**
     * Handles the ai glyph placement
     */
    private void runAiPlacer() {
        if (gamepad2.x && gamepad2.b) {
            drive.cryptobox.setSnakeTarget(Cryptobox.Snake.NONE);
        }
        bX = gamepad2.x;

        //Triggers as override
        if (gamepad2.right_trigger < Drive.DEADZONE_SIZE && gamepad2.left_trigger < Drive.DEADZONE_SIZE) {
            greyBrown = drive.uTrackAutoTarget(gamepad2);
        }
        //Right stick up to switch glyph color
        if (!bRightStickY && -gamepad2.right_stick_y >= .5) {
            greyBrown = drive.cryptobox.wrongColor();
        }
        bRightStickY = -gamepad2.right_stick_y >= .5;
    }

    /**
     * Handles the first frame of placing a glyph using the ai
     * @param nextGlyph The predictions from the glyph placer
     * @param numPlaces The number of glyphs placed thus far
     */
    private void aiGlyphPlace(int[] nextGlyph, int numPlaces) {
        this.greyBrown = nextGlyph;
        telemetry.log().add("greybrown: [" + nextGlyph[0] + ", " + nextGlyph[1] + "]");
        if (nextGlyph[0] == -1 && nextGlyph[1] == -1) {
            rejectGlyph();
        }
        if(!(((nextGlyph[0] + nextGlyph[1]) == 0) && !(numPlaces == 11))) {
            drive.uTrack();
        }
    }

    /**
     * Rejects the glyph
     */
    private void rejectGlyph() {
        //TODO: WRITE THIS FUNCTION
    }

    private void speedModes() {
        //Left and right stick together = super slow
        if (gamepad1.left_stick_button && !aLeftStick && gamepad1.right_stick_button && !aRightStick) {
            adjustedSpeed = .5;
        }
        //Left stick = fast
        if (gamepad1.left_stick_button && !aLeftStick && !gamepad1.right_stick_button) {
            adjustedSpeed = 1;
        }
        //Right stick = med
        if (gamepad1.right_stick_button && !aRightStick && !gamepad1.left_stick_button) {
            adjustedSpeed = .75;
        }
    }

    private boolean done = true;

    private void glyphTarget() {
        int targetX = gamepad2.x ? 0 : (gamepad2.y ? 1 : (gamepad2.b ? 2 : -1));
        int targetY = gamepad2.dpad_up ? 0 : (gamepad2.dpad_left || gamepad2.dpad_right ? 1 : (gamepad2.dpad_down ? 2 : -1));
        if (targetX != -1 && targetY != -1 &&
                (!done || ((done && !bRight && !bLeft && !bUp && !bDown) || (done && !bB && !bX && !bY)))) {
            drive.glyph.uiTarget(targetX, targetY);
            drive.glyphLocate();
            done = drive.uTrack();
            telemetry.addData("done", done);
        } else if (done && drive.verticalDriveTargetPos() != GlyphPlacementSystem.Position.HOME.getEncoderVal()) {
            drive.setVerticalDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            drive.setVerticalDrivePos(GlyphPlacementSystem.Position.ABOVEHOME.getEncoderVal());
            drive.glyph.runToPosition(0);
        }
    }

    private void glyphUI() {
        if (gamepad2.dpad_up && !bUp) { drive.glyph.uiUp(); }
        if (gamepad2.dpad_down && !bDown) { drive.glyph.uiDown(); }
        if (gamepad2.dpad_left && !bLeft) { drive.glyph.uiLeft(); }
        if (gamepad2.dpad_right && !bRight) { drive.glyph.uiRight(); }
    }

    private void intakes() {
        double aRightTrigger = drive.deadZone(gamepad1.right_trigger);
        boolean aRightBumper = gamepad1.right_bumper;
        double aLeftTrigger = drive.deadZone(gamepad1.left_trigger);
        boolean aLeftBumper = gamepad1.left_bumper;

        double bRightTrigger = drive.deadZone(gamepad2.right_trigger);
        boolean bRightBumper = gamepad2.right_bumper;
        double bLeftTrigger = drive.deadZone(gamepad2.left_trigger);
        boolean bLeftBumper = gamepad2.left_bumper;

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
        boolean isDriverInput = Drive.isExtendo && (aLeftBumper || aRightBumper || aRightTrigger > Drive.DEADZONE_SIZE || aLeftTrigger > Drive.DEADZONE_SIZE);

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
        if (!intakeBackstop || (backstopEngaged && !ignoreInput && isDriverInput)) {
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
                        drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.RESET))) {
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
                        drive.stage.equals(GlyphPlacementSystem.Stage.PLACE1) ||
                        drive.stage.equals(GlyphPlacementSystem.Stage.RESET))) {
                    drive.internalIntakeLeft(.5);
                }else if (!Drive.isExtendo) {
                    drive.internalIntakeLeft(0);
                }
            }
        }
    }

    private void updateControlBooleans() {
        aBack = gamepad1.back;
        bBack = gamepad2.back;

        aLeftStick = gamepad1.left_stick_button;
        aRightStick = gamepad1.right_stick_button;
        bLeftStick = gamepad2.left_stick_button;
        bRightStick = gamepad2.right_stick_button;

        //aY = gamepad1.y;
        aB = gamepad1.b;

        bY = gamepad2.y;
        bA = gamepad2.a;
        bB = gamepad2.b;

        aUp = gamepad1.dpad_up;
        //aDown = gamepad1.dpad_down;

        bUp = gamepad2.dpad_up;
        bLeft = gamepad2.dpad_left;
        bDown = gamepad2.dpad_down;
        bRight = gamepad2.dpad_right;
    }

    private void telemetryUpdate() {
        cursorCount += .1;
        telemetry.addData("Manual", manual);
        telemetry.addData("Crawl", Drive.crawl);
        //telemetry.addData("Glyph", drive.glyph.getTargetPositionAsString());
        telemetry.addData("AI", aiPlacer);
        telemetry.addData("Cryptobox", drive.cryptobox == null ? "" : drive.cryptobox.uiToString((int) cursorCount % 2 == 0));
        printNextGlyph();
        //telemetry.addData("Reject glyph", drive.cryptobox.getRejectGlyph());
        Cryptobox.Snake snakeTarget = drive.cryptobox.getSnakeTarget();
        telemetry.addData("Snake target", snakeTarget == null ? "null" : snakeTarget.name());
        //telemetry.addData("line follower", drive.smallVoltage);
        telemetry.addData("vert Pos", drive.verticalDriveCurrPos());
        if (drive.verbose) {
            telemetry.addData("gamepad1.dpad_up", gamepad1.dpad_up);
            telemetry.addData("bottom", drive.getBottomState());
            telemetry.addData("center", drive.getCenterState());
            telemetry.addData("collected", drive.getCollectedState());
            telemetry.addData("cursorCount", (int) cursorCount);
            telemetry.addData("bLeftStick", bLeftStick);
            telemetry.addData("gamepad2.left_stick_button", gamepad2.left_stick_button);
            telemetry.addData("!bLeftStick && gamepad2.left_stick_button", !bLeftStick && gamepad2.left_stick_button);
            telemetry.addData("!bRightStick && gamepad2.right_stick_button", !bRightStick && gamepad2.right_stick_button);
            telemetry.addData("extendoTimer", drive.extendoTimer.seconds());
            telemetry.addData("numGlyphsCollected", drive.cryptobox.getNumGlyphsPlaced());
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