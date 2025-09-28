package intersector;

import data.Line;
import data.Point;
import data.linetypes.*;

import java.util.ArrayList;

public class IntersectParser {

    private ArrayList<Point> intersections;
    public Line l1;
    public Line l2;

    public IntersectParser(Line l1, Line l2) {
        this.l1 = l1;
        this.l2 = l2;
        computeIntersection();
    };

    /**
     * Swap the lines for reusable methods
     */
    private void swapLines() {
        Line temp = l1;
        l1 = l2;
        l2 = temp;
    }

    /**
     * Getter for the results
     */
    public ArrayList<Point> getIntersections() {
        return intersections;
    }

    /**
     * Compute intersection based on the type of the input lines
     */
    private void computeIntersection() {
        if (l1 instanceof Segment && l2 instanceof Segment) {
            // Segment - segment intersection
            intersections = new SegmentSegment(this).getResults();
        } else if (l1 instanceof Segment && l2 instanceof Arc) {
            // Segment - arc intersection
            intersections = new SegmentArc(this).getResults();
        } else if (l1 instanceof Arc && l2 instanceof Segment) {
            // Arc - segment intersection
            swapLines();
            intersections = new SegmentArc(this).getResults();
        } else if (l1 instanceof Segment && l2 instanceof Bezier) {
            // Segment - bezier intersection
            intersections = new SegmentBezier(this).getResults();
        } else if (l1 instanceof Bezier && l2 instanceof Segment) {
            // Bezier - segment intersection
            swapLines();
            intersections = new SegmentBezier(this).getResults();
        } else if (l1 instanceof Arc && l2 instanceof Arc) {
            // Arc - arc intersection
            intersections = new ArcArc(this).getResults();
        } else if (l1 instanceof Arc && l2 instanceof Bezier) {
            // Arc - bezier intersection
            intersections = new ArcBezier(this).getResults();
        } else if (l1 instanceof Bezier && l2 instanceof Arc) {
            // Bezier - arc intersection
            swapLines();
            intersections = new ArcBezier(this).getResults();
        } else if (l1 instanceof Bezier && l2 instanceof Bezier) {
            // Bezier - bezier intersection
            intersections = new BezierBezier(this).getResults();
        } else {
            throw new IllegalArgumentException("Unsupported line types: " + l1.getClass() + ", " + l2.getClass());
        }
    }
}
