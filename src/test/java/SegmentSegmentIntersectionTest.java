package test;

import data.Point;
import data.linetypes.*;
import intersector.IntersectParser;

import java.util.ArrayList;

public class SegmentSegmentIntersectionTest {
    public static void main(String[] args) {
        /*
         * One intersection point:
         *      Point{x: 5, y: 5}
         */

        Segment s1 = new Segment(new Point(0,0), new Point(10,10));
        Segment s2 = new Segment(new Point(0,10), new Point(10,0));

        ArrayList<Point> inter = new IntersectParser(s1,s2).getIntersections();
        for (Point p : inter) {
            System.out.println(p);
        }
    }
}
