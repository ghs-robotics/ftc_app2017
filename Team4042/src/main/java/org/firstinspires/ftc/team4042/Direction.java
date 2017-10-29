package org.firstinspires.ftc.team4042;

/**
 * Created by Hazel on 10/10/2017.
 */

public class Direction {

    private double x;
    private double y;

    public enum Rotation {Clockwise, Counterclockwise}

    public Direction() {}

    public static Direction Forward = new Direction(0, 1);
    public static Direction Backward = new Direction(0, -1);
    public static Direction Right = new Direction(1, 0);
    public static Direction Left = new Direction(-1, 0);

    public Direction(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns true if the direction is more forward than backward
     * @return Whether the direction is more forward than backward
     */
    public boolean isForward() {
        return y >= 0;
    }

    /**
     * Returns true if the direction is more backward than forward
     * @return Whether the direction is more backward than forward
     */
    public boolean isBackward() {
        return y < 0;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        Direction o = (Direction)other;
        return x == o.getX() && y == o.getY();
    }
}
