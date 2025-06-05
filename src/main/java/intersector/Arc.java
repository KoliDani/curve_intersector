package intersector;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Arc extends Segment {
    private Point center = null;

    public Arc(Point s, Point e, Point c) {
        super(s,e);
        center = c;
    }

    /**
     * Arc intersect with line
     */
    @Override
    public ArrayList<Point> intersect(Line other) {
        // find the infinite line and circle intersections
        Point dir = dir(other.s,other.e);
        double a = dist2(dir);

        Point dirSC = dir(center,other.s);
        double b = 2*((dirSC.x * dir.x) + (dirSC.y * dir.y));
        double c = dist2(dirSC) - dist2(dir(s,center));

        ArrayList<Point> intersection = new ArrayList<>();
        double d = (b * b) - (4 * a * c);
        if (Math.abs(d) <= 0.001) {
            // line and arc tangent
            double t = -b / (2 * a);
            Point inter = new Point(other.s.x + (t * dir.x), other.s.y + (t * dir.y));

            if (other.pointOnSegment(inter) && pointOnArc(inter)) {
                intersection.add(inter);
            }
        } else if (d < 0) {
            // no intersection
            return intersection;
        } else {
            // two intersection
            double t1 = (-b + Math.sqrt(d)) / (2 * a);
            double t2 = (-b - Math.sqrt(d)) / (2 * a);

            Point inter1 = new Point(other.s.x + (t1 * dir.x), other.s.y + (t1 * dir.y));
            Point inter2 = new Point(other.s.x + (t2 * dir.x), other.s.y + (t2 * dir.y));

            if (other.pointOnSegment(inter1) && pointOnArc(inter1)) {
                intersection.add(inter1);
            }
            if (other.pointOnSegment(inter2) && pointOnArc(inter2)) {
                intersection.add(inter2);
            }
        }

        return intersection;
    }

    /**
     * Arc intersect with arc
     */
    @Override
    public ArrayList<Point> intersect(Arc other) {
        Point cDir = dir(center, other.center);
        double d = dist(cDir);
        double r1 = dist(dir(center, s));
        double r2 = dist(dir(other.center, other.s));

        ArrayList<Point> intersection = new ArrayList<>();
        if (Math.abs((r1 + r2) - d) <= 0.001) {
            // one point intersection
            // dir from center to center
            Point dir = dir(center, other.center);
            double dist = dist(dir);
            dir.x /= dist;
            dir.y /= dist;
            // center + radius * unity dir -> intersection
            Point inter = new Point(center.x + (r1 * dir.x), center.y + (r1 * dir.y));

            if (pointOnArc(inter) && other.pointOnArc(inter)) {
                intersection.add(inter);
            }
        } else if ((r1 + r2) < d) {
            // the distance between the centers larger than the sum of radii
            return intersection;
        } else if (d < Math.abs(r1 - r2)) {
            // one of the circles inside the other one
            return intersection;
        } else if (Math.abs(d) <= 0.001 && Math.abs(r1 - r2) <= 0.001) {
            // the two arc is on the same circle
            if (pointOnArc(other.s)) {
                intersection.add(other.s);
            }
            if (pointOnArc(other.e)) {
                intersection.add(other.e);
            }
            if (other.pointOnArc(s)) {
                intersection.add(s);
            }
            if (other.pointOnArc(e)) {
                intersection.add(e);
            }
        } else {
            // general case, searh the two circle intersection points
            double a = ((r1 * r1) - (r2 * r2) + (d * d)) / (2 * d);
            double h = (r1 * r1) - (a * a);

            if (h < 0) {
                // no intersection
                return intersection;
            }

            h = Math.sqrt(h);
            Point m = new Point(center.x + (a * cDir.x / d), center.y + (a * cDir.y / d));
            Point norm = new Point(-cDir.y/d, cDir.x/d);

            Point inter1 = new Point(m.x + (h * norm.x), m.y + (h * norm.y));
            Point inter2 = new Point(m.x - (h * norm.x), m.y - (h * norm.y));

            if (pointOnArc(inter1) && other.pointOnArc(inter1)) {
                intersection.add(inter1);
            }
            if (pointOnArc(inter2) && other.pointOnArc(inter2)) {
                intersection.add(inter2);
            }
        }

        return intersection;
    }

    /**
     * Arc intersect with Bezier
     */
    @Override
    public ArrayList<Point> intersect(Bezier other) {
        return other.intersect(this);
    }

    /**
     * Arc intersect with spline
     */
    @Override
    public ArrayList<Point> intersect(Spline other) {
        return other.intersect(this);
    }

    private boolean pointOnArc(Point p) {
        double tolerance = 0.001;
        Point cs = dir(center,s);
        Point ce = dir(center,e);
        Point cp = dir(center,p);

        // Calculate angles in radians
        double startAngle = Math.atan2(s.y - center.y, s.x - center.x);
        double endAngle = Math.atan2(e.y - center.y, e.x - center.x);
        double pointAngle = Math.atan2(p.y - center.y, p.x - center.x);

        // Normalize angles to [0, 2π)
        startAngle = normalizeAngle(startAngle);
        endAngle = normalizeAngle(endAngle);
        pointAngle = normalizeAngle(pointAngle);

        // Handle different arc cases
        if (Math.abs(endAngle - startAngle) < tolerance) {
            // Full circle case (start and end angles are effectively the same)
            return true;
        }

        if (startAngle < endAngle) {
            // Normal case - arc doesn't cross 0°
            return pointAngle >= startAngle - tolerance &&
                    pointAngle <= endAngle + tolerance;
        } else {
            // Arc crosses 0° - check both segments
            return pointAngle >= startAngle - tolerance ||
                    pointAngle <= endAngle + tolerance;
        }
    }

    /**
     * Normalizes an angle to the range [0, 2π)
     */
    private static double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        return angle < 0 ? angle + 2 * Math.PI : angle;
    }

    @Override
    public Shape toAWT() {
        double radius = dist(dir(s,center));
        // Calculate bounding rectangle (square for circle)
        double diameter = 2 * radius;
        double x = center.x - radius;
        double y = center.y - radius;

        // Calculate start angle (0° is 3 o'clock position, increases counter-clockwise)
        double startAngle = Math.toDegrees(Math.atan2(s.y - center.y, s.x - center.x));

        // Calculate end angle
        double endAngle = Math.toDegrees(Math.atan2(e.y - center.y, e.x - center.x));

        // Calculate angular extent (normalized to 0-360)
        double extent = endAngle - startAngle;
        if (extent < 0) {
            extent += 360;
        }

        // Create the arc (using PIE type as example)
        return new Arc2D.Double(x, y, diameter, diameter, startAngle, extent, Arc2D.OPEN);
    }
}
