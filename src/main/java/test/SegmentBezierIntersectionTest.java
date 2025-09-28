package test;

import data.Point;
import data.linetypes.Bezier;
import data.linetypes.Segment;
import intersector.IntersectParser;

import java.util.ArrayList;

public class SegmentBezierIntersectionTest {
    public static void main(String[] args) {
        /*
         * Two intersection points:
         *      Point{x: 0.13630453270434828, y: 1.0000000000024138}
         *      Point{x: 4.738695467295349, y: 1.000000000002416}
         */

        Bezier a1 = new Bezier(new Point(0,0), new Point(5,0),
                new Point(1,8),new Point(3,8));
        Segment a2 = new Segment(new Point(-5,1), new Point(5,1));

        ArrayList<Point> inter = new IntersectParser(a1,a2).getIntersections();
        for (Point p : inter) {
            System.out.println(p);
        }
    }
}
