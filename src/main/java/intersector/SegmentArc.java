package intersector;

import data.Point;
import data.linetypes.Arc;
import data.linetypes.Segment;

public class SegmentArc extends Intersect{
    public SegmentArc(IntersectParser intersectParser) {
        Segment l1 = (Segment) intersectParser.l1;
        Arc l2 = (Arc) intersectParser.l2;

        double radius = l2.getRadius();

        // Parametrize the line segment
        double dx = l1.end.getX() - l1.start.getX();
        double dy = l1.end.getY() - l1.start.getY();
        double fx = l1.start.getX() - l2.getCenter().getX();
        double fy = l1.start.getY() - l2.getCenter().getY();

        double a = dx * dx + dy * dy;
        double b = 2 * (fx * dx + fy * dy);
        double c = fx * fx + fy * fy - radius * radius;

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return; // No intersection
        }

        discriminant = Math.sqrt(discriminant);

        double t1 = (-b - discriminant) / (2 * a);
        double t2 = (-b + discriminant) / (2 * a);

        for (double t : new double[]{t1, t2}) {
            if (t < 0 || t > 1) continue; // Not on the segment

            double ix = l1.start.getX() + t * dx;
            double iy = l1.start.getY() + t * dy;
            Point intersection = new Point(ix, iy);

            if (l2.isOnArc(intersection)) {
                intersections.add(intersection);
            }
        }
    }
}
