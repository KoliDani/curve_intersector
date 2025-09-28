package data.linetypes;

import data.Line;
import data.Point;

public class Bezier extends Line {
    public Point control1;
    public Point control2;


    public Bezier(Point s, Point e, Point c1, Point c2) {
        super(s,e);
        control1 = c1;
        control2 = c2;
    }

    // Evaluate point at parameter t
    public Point evaluate(double t) {
        double u = 1 - t;
        double x = u*u*u*start.getX() + 3*u*u*t*control1.getX() + 3*u*t*t*control2.getX() + t*t*t*end.getX();
        double y = u*u*u*start.getY() + 3*u*u*t*control1.getY() + 3*u*t*t*control2.getY() + t*t*t*end.getY();
        return new Point(x, y);
    }

    public double[][] getCoefficients() {
        double[][] coeff = new double[2][4];
        double[] bx = new double[]{-start.getX() + 3*control1.getX() - 3*control2.getX() + end.getX(),
                3*start.getX() - 6*control1.getX() + 3*control2.getX(),
                -3*start.getX() + 3*control1.getX(),
                start.getX()};
        double[] by = new double[]{-start.getY() + 3*control1.getY() - 3*control2.getY() + end.getY(),
                3*start.getY() - 6*control1.getY() + 3*control2.getY(),
                -3*start.getY() + 3*control1.getY(),
                start.getY()};

        coeff[0] = bx;
        coeff[1] = by;

        return coeff;
    }
}
