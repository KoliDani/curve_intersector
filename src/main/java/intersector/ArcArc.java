package intersector;

import data.Point;
import data.linetypes.Arc;

import java.util.ArrayList;

public class ArcArc extends Intersect {
    public ArcArc(IntersectParser intersectParser) {
        Arc l1 = (Arc) intersectParser.l1;
        Arc l2 = (Arc) intersectParser.l2;

        Point cDir = Point.dir(l1.getCenter(), l2.getCenter());
        double d = Point.dist(cDir);
        double r1 = l1.getRadius();
        double r2 = l2.getRadius();

        if (Math.abs((r1 + r2) - d) <= 0.001) {
            // one point intersection
            // dir from center to center
            Point dir = Point.dir(l1.getCenter(), l2.getCenter());
            double dist = Point.dist(dir);
            dir.setX((long) (dir.getX() / dist));
            dir.setY((long) (dir.getY() / dist));

            // center + radius * unity dir -> intersection
            Point inter = new Point(
                    (long) (l1.getCenter().getX() + (r1 * dir.getX())),
                    (long) (l1.getCenter().getY() + (r1 * dir.getY())));

            if (l1.isOnArc(inter) && l2.isOnArc(inter)) {
                intersections.add(inter);
            }
        } else if ((r1 + r2) < d) {
            // the distance between the centers larger than the sum of radii
            return;
        } else if (d < Math.abs(r1 - r2)) {
            // one of the circles inside the other one
            return;
        } else if (Math.abs(d) <= 0.001 && Math.abs(r1 - r2) <= 0.001) {
            // the two arc is on the same circle
            if (l1.isOnArc(l2.start)) {
                intersections.add(l2.start);
            }
            if (l1.isOnArc(l2.end)) {
                intersections.add(l2.end);
            }
            if (l2.isOnArc(l1.start)) {
                intersections.add(l1.start);
            }
            if (l2.isOnArc(l1.end)) {
                intersections.add(l1.end);
            }
        } else {
            // general case, searh the two circle intersection points
            double a = ((r1 * r1) - (r2 * r2) + (d * d)) / (2 * d);
            double h = (r1 * r1) - (a * a);

            if (h < 0) {
                // no intersection
                return;
            }

            h = Math.sqrt(h);
            double mx = l1.getCenter().getX() + (a * cDir.getX() / d);
            double my = l1.getCenter().getY() + (a * cDir.getY() / d);

            double vx = -cDir.getY() / d;
            double vy =  cDir.getX() / d;

            Point inter1 = new Point(
                    mx + h * vx,
                    my + h * vy);

            Point inter2 = new Point(
                    mx - h * vx,
                    my - h * vy);

            if (l1.isOnArc(inter1) && l2.isOnArc(inter1)) {
                intersections.add(inter1);
            }
            if (l1.isOnArc(inter2) && l2.isOnArc(inter2)) {
                intersections.add(inter2);
            }
        }
    }
}
