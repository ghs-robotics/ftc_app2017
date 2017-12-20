package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.Range;

public class Drive {
    double xComp;
    double yComp;
    double rot;
    int oldGyro = 45;

    static final int ROT_RATIO = 100;

    //changeX and changeY are in terms of ticks
    public void driveXY(int targetX, int targetY) {
        double xSpeed;
        double ySpeed;
        int actualX = 0;
        int actualY = 0;
        boolean xAchieved = false;
        boolean yAchieved = false;
        int acceptableTickError = 200;
        int acceptableRotationError = 1; //in degrees
        int rotationSpeed;
        int currentRotation;
        /*
            Indexes:
            0 is the right front motor
            1 is the left front motor
            2 is the left back motor
            3 is the right back motor
            speedWheel[n]
            */

        while (xAchieved == false || yAchieved == false) {

            //corrects x if needed
            if ((Math.abs(targetX - actualX)) < acceptableTickError) {
                xAchieved = true;
            }
            else {
                //needs to correct it
                //looking from back of motor, they spin counterclockwise with positive values
                //for moving right: RB positive, LF negative
                xAchieved = false;

                //this needs a better speed calculation algorithm
                xSpeed = 1;

                if (targetX > actualX) {
                    //targetX to right of actual
                    setLF(-1 * xSpeed);
                    setRB(xSpeed);
                }
                else {
                    //the other way around
                    setLF(xSpeed);
                    setRB(-1 * xSpeed);
                }

            }

            //handles y correction
            if ((Math.abs(targetY - actualY)) < acceptableTickError) {
                yAchieved = true;
            }
            else {
                //needs to correct it
                //looking from back of motor, they spin counterclockwise with positive values
                //for moving up: RF positive, LB negative
                yAchieved = false;

                //this needs a better speed calculation algorithm
                ySpeed = 1;

                if (targetY > actualY) {
                    //targetY to up of actual
                    setLB(-1 * ySpeed);
                    setRF(ySpeed);
                }
                else {
                    //the other way around
                    setLB(ySpeed);
                    setRF(-1 * ySpeed);
                }

            }

            currentRotation = getGyro();
            //corrects direction
            //assumes that 90 degrees is up and that counterclockwise is left
            //if rotation is counterclockwise of forward
            if (currentRotation < (90 - acceptableRotationError)) {

            }
            //if rotation is clockwise of forward
            else if (currentRotation > (90 + acceptableRotationError)) {

            }



        }
    }

    public void driveToPosition(int targetTicks) {
        int totalTicks;
        boolean completed = false;

        while (completed = false) {
            int speed = 1;
            float avgTicks;


            if (avgTicks >= targetTicks) {
                completed = true;
                moveData = 0;
            }

            drive(speed);

        }

        resetEncoders();


    }

    public void useGyro(int curGyro){
        double r;
        if (rot == 0) {
            double gyroDiff = curGyro - oldGyro;
            //If you're moving forwards and you drift, this should correct it.
            //Accounts for if you go from 1 degree to 360 degrees which is only a difference of one degree,
            //but the bot thinks that's 359 degree difference
            //Scales -180 to 180 -> -1 to 1
            if (gyroDiff < -180) {
                r = (180 + gyroDiff) / 180; //replaced (1.5 * (gyroDiff/180)) because function of 1.5 is unknown
            } if (gyroDiff > 180) {
                r = (180 - gyroDiff) / 180; //replaced (1.5 * (gyroDiff/180)) because function of 1.5 is unknown
            } else {
                r = (gyroDiff - 180) / 180; //replaced (1.5 * (gyroDiff/180)) because function of 1.5 is unknown
            }
        }
        else {
            oldGyro = curGyro;
            r = rot;
        }

        rot = Range.clip(r, -1, 1);

        double temp = xComp;
        xComp = xComp * Math.cos(curGyro) - yComp * Math.sin(curGyro);
        yComp = temp * Math.sin(curGyro) + yComp * Math.cos(curGyro);
    }

    public double[] drive(double speed) {
        double[] speedWheel = new double[4];

        for (int n = 0; n < 4; n++) {
            //This \/ rotates the control input to make it work on each motor
            speedWheel[n] = (xComp * Math.sin(n) + yComp * Math.cos(n) + ROT_RATIO * rot);
        }

        /*
        Indexes:
        0 is the right front motor
        1 is the left front motor
        2 is the left back motor
        3 is the right back motor
         */

        //In order to handle the problem if the values in speedWheel[] are greater than 1,
        //this scales them so the ratio between the values stays the same, but makes sure they're
        //less than 1
        double scaler = Math.abs(1 / max(speedWheel[0], speedWheel[1], speedWheel[2], speedWheel[3]));

        for (int n = 0; n < 4; n++) {
            speedWheel[n] = speed * scaler * speedWheel[n];
        }

        return speedWheel;
    }

    public double max(double a, double b, double c, double d) {
        double max = a;
        double[] vals = {b, c, d};

        for (int i = 0; i < 3; i++) {
            if (vals[i] > max) {
                max = vals[i];
            }
        }
        return max;

        //We can't use a long loop because it has to take less time than a phone tick, so if that ^ code doesn't
        //run fast enough, we can just use this \/ code instead.
        /*
        a = Math.abs(a);
        b = Math.abs(b);
        c = Math.abs(c);
        d = Math.abs(d);

        if (d > a && d > b && d > c) {
            return d;
        }
        if (c > a && c > b && c > d) {
            return c;
        }
        if (b > a && b > c && b > d) {
            return b;
        }
        return a;
        */
    }
}
