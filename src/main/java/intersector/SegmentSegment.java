package intersector;

import data.Point;
import data.linetypes.Segment;

public class SegmentSegment extends Intersect{
    public SegmentSegment(IntersectParser intersectParser) {
        Segment l1 = (Segment) intersectParser.l1;
        Segment l2 = (Segment) intersectParser.l2;

        double x1 = l1.start.getX(), y1 = l1.start.getY();
        double x2 = l1.end.getX(), y2 = l1.end.getY();
        double x3 = l2.start.getX(), y3 = l2.start.getY();
        double x4 = l2.end.getX(), y4 = l2.end.getY();

        // Compute the denominators
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denom == 0) {
            // Lines are parallel (or collinear)
            // Later logic can be implemented to return the points
            return;
        }

        // Compute the intersection point
        double px = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / denom;
        double py = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / denom;

        Point intersection = new Point(px, py);

        // Check if the intersection is within both segments
        if (Point.isBetween(l1.start, l1.end, intersection) && Point.isBetween(l2.start, l2.end, intersection)) {
            intersections.add(intersection);
        }
    }


}
