package intersector;

public class Util {
    protected static Point dir(Point p1, Point p2) {
        return new Point(p2.x - p1.x, p2.y - p1.y);
    }
    protected static Point udir(Point p1, Point p2) {
        Point d = dir(p1, p2);
        double dist = dist(d);
        d.x /= dist;
        d.y /= dist;
        return d;
    }

    protected static double dist(Point dir) {
        return Math.sqrt(dist2(dir));
    }

    protected static double dist2(Point dir) {
        return (dir.x * dir.x + dir.y * dir.y);
    }

    protected double cross(Point p1, Point p2) {
        return (p2.x * p1.y) - (p1.x * p2.y);
    }

    // Cross product for convex hull
    protected static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }
}