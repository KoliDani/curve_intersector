package test;

import data.Point;
import data.linetypes.Arc;
import data.linetypes.Bezier;
import data.linetypes.Segment;
import intersector.IntersectParser;

import java.util.ArrayList;

public class ArcBezierIntersectionTest {
    public static void main(String[] args) {
        /*
         * Two intersection points:
         *      Point{x: 0.30137481106623476, y: 2.0090909416968645}
         *      Point{x: 3.830002729452831, y: 3.785800395898131}
         */

        Bezier bezier = new Bezier(new Point(0,0), new Point(5,0),
                new Point(1,8),new Point(3,8));
        Arc arc = new Arc(new Point(-5,7), new Point(5,7), new Point(0,7));

        ArrayList<Point> inter = new IntersectParser(bezier,arc).getIntersections();
        for (Point p : inter) {
            System.out.println(p);
        }
    }
}

