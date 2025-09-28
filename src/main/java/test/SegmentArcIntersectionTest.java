package test;

import data.Point;
import data.linetypes.*;
import intersector.IntersectParser;

import java.util.ArrayList;

public class SegmentArcIntersectionTest {
    public static void main(String[] args) {
        /*
         * Two intersection points:
         *      Point{x: -489897, y: 100000}
         *      Point{x: 489897, y: 100000}
         */

        Segment s1 = new Segment(new Point(-10,1), new Point(10,1));
        Arc a1 = new Arc(new Point(5,0), new Point(-5,0), new Point(0,0));

        ArrayList<Point> inter = new IntersectParser(s1,a1).getIntersections();
        for (Point p : inter) {
            System.out.println(p);
        }
    }
}
