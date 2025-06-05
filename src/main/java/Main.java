import view.IntersectionVisualizerPanel;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import intersector.*;
import view.Visualizer;

public class Main {
    public static void main(String[] args) {
        System.out.println("We are running!");

        Line l1 = Segment.create(new Point(-2,0), new Point(12,0));
        Line l2 = Segment.create(new Point(5,10), new Point(5,-10));
        Arc a1 = Segment.create(new Point(0,0), new Point(5,5), new Point(5,0));

        ArrayList<Point> inter = l1.intersect(l2);
        inter.addAll(l1.intersect(a1));
        inter.addAll(l2.intersect(a1));

        ArrayList<Line> obj = new ArrayList<>();
        obj.add(l1);
        obj.add(l2);
        obj.add(a1);

        Visualizer visu = Visualizer.plot(obj,inter);
    }
    public static void linexline() {}
    public static void linexarc() {}
    public static void linexspline() {}
    public static void arcxarc() {}
    public static void arcxspline() {}
    public static void splinexspline() {}
}
