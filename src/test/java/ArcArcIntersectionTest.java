package test;

import data.Point;
import data.linetypes.Arc;
import intersector.IntersectParser;

import java.util.ArrayList;

public class ArcArcIntersectionTest {
    public static void main(String[] args) {
        /*
         * Two intersection points:
         *      Point{x: 3, y: 4}
         *      Point{x: -3, y: 4}
         */

        Arc a1 = new Arc(new Point(-5,8), new Point(5,8), new Point(0,8));
        Arc a2 = new Arc(new Point(5,0), new Point(-5,0), new Point(0,0));

        ArrayList<Point> inter = new IntersectParser(a1,a2).getIntersections();
        for (Point p : inter) {
            System.out.println(p);
        }
    }
}
