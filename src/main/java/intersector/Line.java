package intersector;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

public class Line extends Segment {

    public Line(Point s, Point e) {
        super(s,e);
    }

    public boolean isIntersect(Line other) {
        return isIntersect(other.s, other.e);
    }

    public boolean isIntersect(Point ls, Point le) {
        double denom = (le.x - ls.x) * (e.y - s.y) -
                (e.x - s.x) * (le.y - ls.y);
        return denom > 0;
    }

    /**
     * Line intersect with line
     */
    public ArrayList<Point> intersect(Line other) {
        double denom = (other.e.x - other.s.x) * (e.y - s.y) -
                (e.x - s.x) * (other.e.y - other.s.y);

        if (Math.abs(denom) <= 0.001) {
            HashSet<Point> intersection = new HashSet<>();
            intersection.addAll(collinearLineIntersection(other));
            intersection.addAll(other.collinearLineIntersection(this));
            return new ArrayList<Point>(intersection);
        }

        return generalLineIntersection(other, denom);
    }

    /**
     * Line intersect with arc
     */
    public ArrayList<Point> intersect(Arc other) {
        return other.intersect(this);
    }

    /**
     * Line intersect with Bezier
     */
    public ArrayList<Point> intersect(Bezier other) {
        return other.intersect(this);
    }

    /**
     * Line intersect with spline
     */
    public ArrayList<Point> intersect(Spline other) {
        return other.intersect(this);
    }

    private ArrayList<Point> generalLineIntersection(Line other, double denom) {
        Point dir1 = dir(s,e);
        Point dir2 = dir(other.s,other.e);

        double c1 = cross(s,e);
        double c2 = cross(other.s,other.e);

        double x = (dir1.x * c2 - dir2.x * c1) / denom;
        double y = (dir1.y * c2 - dir2.y * c1) / denom;

        ArrayList<Point> intersection = new ArrayList<>();
        intersection.add(new Point(x,y));
        return intersection;
    }

    private ArrayList<Point> collinearLineIntersection(Line other) {
        // determine the distance between the lines
        if (distanceProjection(other.s) > 0.001) {
            return new ArrayList<Point>();
        }

        // check if any of the points on the other line's segment
        ArrayList<Point> intersection = new ArrayList<>();
        if (pointOnSegment(other.s)) {
            intersection.add(other.s);
        }
        if (pointOnSegment(other.e)) {
            intersection.add(other.e);
        }
        return new ArrayList<Point>();
    }

    protected double distanceProjection(Point p) {
        return dist(dir(p,projectPointToLine(p)));
    }

    protected Point projectPointToLine(Point p) {
        Point d = dir(s,e);
        double t = (p.x - s.x) * (d.x) + (p.y - s.y) * (d.y);
        t /= dist2(d);

        return new Point(s.x + t * d.x, s.y + t * d.y);
    }

    protected boolean pointOnSegment(Point p) {
        double l1 = dist(dir(s,e));
        double d1 = dist(dir(s, p));
        double d2 = dist(dir(e, p));
        return (d1 <= 0.001 || d1 <= l1) && (d2 <= 0.001 || d2 <= l1);
    }

    public Shape toAWT() {
        return new Line2D.Double(s.toAWT(), e.toAWT());
    }
}