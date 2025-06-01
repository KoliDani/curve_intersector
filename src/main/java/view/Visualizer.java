package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.*;
import intersector.*;
import intersector.Point;

public class Visualizer extends JFrame {
    private Visualizer() {}
    public static Visualizer plot(ArrayList<Line> objects, ArrayList<Point> intersections) {
        Visualizer visu = new Visualizer();
        visu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        IntersectionVisualizerPanel panel = new IntersectionVisualizerPanel();
        for (Line obj : objects) {
            if (obj instanceof Arc) {
                panel.addArc((Arc2D) obj.toAWT());
                continue;
            }
            if (obj instanceof Bezier) {
                continue;
            }
            if (obj instanceof Spline) {
                continue;
            }
            if (obj instanceof Line) {
                panel.addLine((Line2D) obj.toAWT());
                continue;
            }
        }

        for (Point p : intersections) {
            panel.addIntersection(p.toAWT());
        }

        visu.setContentPane(panel);
        visu.pack();
        visu.setLocationRelativeTo(null); // Center on screen
        visu.setVisible(true);
        visu.setSize(800, 600);
        panel.setVisible(true);

        panel.repaint();
        return visu;
    }
}
