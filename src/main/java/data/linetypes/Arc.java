package data.linetypes;

import data.Line;
import data.Point;

public class Arc extends Line {
    private Point center;
    public Arc(Point s, Point e, Point c) {
        super(s,e);
        center = c;
    }

    public Point getCenter() {
        return center;
    }

    public double getRadius() {
        return Point.distance(getCenter(), start);
    }

    public boolean isOnArc(Point p) {
        double startAngle = Math.atan2(start.getY() - center.getY(), start.getX() - center.getX());
        double endAngle = Math.atan2(end.getY() - center.getY(), end.getX() - center.getX());
        double pointAngle = Math.atan2(p.getY() - center.getY(), p.getX() - center.getX());

        return isBetweenAngles(startAngle, pointAngle, endAngle);
    }

    /**
     * Returns true if angle b is between angle a and angle c in CCW direction
     */
    private static boolean isBetweenAngles(double a, double b, double c) {
        a = normalizeAngle(a);
        b = normalizeAngle(b);
        c = normalizeAngle(c);

        double diff = normalizeAngle(c - a);
        double diffPoint = normalizeAngle(b - a);

        return diffPoint <= diff + 1e-9; // small tolerance
    }

    private static double normalizeAngle(double angle) {
        while (angle <= 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
