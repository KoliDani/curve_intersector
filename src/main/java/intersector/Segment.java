package intersector;

public class Segment {

    protected Point s;
    protected Point e;

    public Segment(Point s, Point e) {
        this.s = s;
        this.e = e;
    }

    public static Line create(Point s, Point e) {
        return new Line(s,e);
    }
    public static Arc create(Point s, Point e, Point center) {
        return new Arc(s,e,center);
    }
    public static Spline create(Point s, Point e, Collection<Point> controls) {
        return new Spline(s,e,controls);
    }

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