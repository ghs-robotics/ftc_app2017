package org.firstinspires.ftc.team4042;

/**
 * Created by Hazel on 10/18/2017.
 */

public class AutoInstruction {
    private Direction direction;
    private double speed;
    private double time;
    private char type;

    public AutoInstruction() {

    }

    public AutoInstruction(double x, double y, double speed, double time, char type) {
        this.direction = new Direction(x, y);
        this.speed = speed;
        this.time = time;
        this.type = type;
    }

    public AutoInstruction(String x, String y, String speed, String time, String type) {
        this.direction = new Direction(Integer.parseInt(x), Integer.parseInt(y));
        this.speed = Integer.parseInt(speed) * Drive.FULL_SPEED;
        this.time = Integer.parseInt(time);
        this.type = type.charAt(0);
    }

    public Direction getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }

    public double getTime() {
        return time;
    }

    public char getType() {
        return type;
    }
}
