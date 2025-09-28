package data.linetypes;

import data.Line;
import data.Point;

public class Segment extends Line {

    public Segment(Point s, Point e) {
        super(s,e);
    }

    // Check if a point lies on the segment
    public boolean onSegment(Point p) {
        double TOLERANCE = 1e-10;

        double minX = Math.min(start.getX(), end.getX());
        double maxX = Math.max(start.getX(), end.getX());
        double minY = Math.min(start.getY(), end.getY());
        double maxY = Math.max(start.getY(), end.getY());

        return p.getX() >= minX - TOLERANCE && p.getX() <= maxX + TOLERANCE
                && p.getY() >= minY - TOLERANCE && p.getY() <= maxY + TOLERANCE;
    }

    public double[] toEqForm() {
        double A = end.getY() - start.getY();
        double B = start.getX() - end.getX();
        double C = -(A * start.getX() + B * start.getY());

        return new double[]{A,B,C};
    }
}
