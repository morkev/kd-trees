package a05;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Vector;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Implementation of a hawk for the Boid simulator.
 * 
 * @author Kevin Mora
 */
public class Hawk {
    private Point2D position;
    private Vector velocity;

    /**
     * Create a hawk at (x, y) with zero velocity.
     * @param x –––> double
     * @param y –––> double
     */
    public Hawk(double x, double y) {
        position = new Point2D(x, y);
        velocity = new Vector(2);
    }

    public Point2D position() {
        return position;
    }

    public double x() {
        return position.x();
    }

    public double y() {
        return position.y();
    }

    /**
     * Compare by y-coordinate, breaking ties by x-coordinate.
     */
    public int compareTo(Hawk that) {
        if (this.position.y() < that.position.y()) return -1;
        if (this.position.y() > that.position.y()) return +1;
        if (this.position.x() < that.position.x()) return -1;
        if (this.position.x() > that.position.x()) return +1;
        return 0;
    }

    /**
     * Compare by y-coordinate, breaking ties by x-coordinate.
     */
    public double distanceSquaredTo(Hawk that) {
        return position.distanceSquaredTo(that.position);
    }

    public Vector returnToWorld() {
        Vector requestedVector = new Vector(2);
        Vector center = new Vector(0.5, 0.5);
        Vector myPosition = new Vector(x(), y());
        requestedVector = center.minus(myPosition);
        return requestedVector;     
    }

    public Vector eatBoid(Boid boid) {
        Vector requestedVector = new Vector(2);
        Vector boidPosition = new Vector(boid.x(), boid.y());
        Vector myPosition = new Vector(x(), y());
        requestedVector = boidPosition.minus(myPosition);
        return requestedVector;     
    }           

    public Vector updatePositionAndVelocity(Boid nearest) {
        double x = x() + velocity.cartesian(0);
        double y = y() + velocity.cartesian(1);
        position = new Point2D(x, y);
        Vector desire = eatBoid(nearest).direction().scale(0.0003);
        velocity = velocity.plus(desire);
        return desire;
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.point(x(), y());
    }
    
}