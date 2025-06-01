package intersector;

import java.awt.geom.Point2D;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Linear interpolation between two points
    public static Point interp(Point a, Point b, double t) {
        return new Point(
                (1 - t) * a.x + t * b.x,
                (1 - t) * a.y + t * b.y);
    }

    public Point2D toAWT() {
        return new Point2D.Double(x, y);
    }
}
