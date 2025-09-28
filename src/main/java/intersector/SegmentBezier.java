package intersector;

import data.Point;
import data.linetypes.Bezier;
import data.linetypes.Segment;

import java.util.ArrayList;

public class SegmentBezier extends Intersect {
    private int depth = 0;
    public SegmentBezier(IntersectParser intersectParser) {
        Segment line = (Segment) intersectParser.l1;
        Bezier bez = (Bezier) intersectParser.l2;

        // Bezier cubic coefficients
        double[][] bezierCoeff = bez.getCoefficients();
        double[] bx = bezierCoeff[0];
        double[] by = bezierCoeff[1];

        // Construct cubic polynomial: A*x(t) + B*y(t) + C = 0
        double[] coeffs = new double[4];
        double[] c = line.toEqForm();
        for(int i=0;i<4;i++){
            coeffs[i] = c[0]*bx[i] + c[1]*by[i];
        }
        coeffs[3] += c[2];

        // Solve cubic
        ArrayList<Double> ts = solveCubic(coeffs[0], coeffs[1], coeffs[2], coeffs[3]);

        for(double t: ts){
            if(t>=0 && t<=1){
                Point p = bez.evaluate(t);
                if(line.onSegment(p)){
                    intersections.add(p);
                }
            }
        }
    }

    // Solve cubic: a t^3 + b t^2 + c t + d = 0
    // Returns only real roots
    public static ArrayList<Double> solveCubic(double a, double b, double c, double d){
        ArrayList<Double> roots = new ArrayList<>();
        if(Math.abs(a) < 1e-12){
            // Quadratic case
            roots.addAll(solveQuadratic(b,c,d));
        }else{
            // Depressed cubic t^3 + pt + q = 0
            b/=a; c/=a; d/=a;
            double p = c - b*b/3;
            double q = 2*b*b*b/27 - b*c/3 + d;
            double discriminant = q*q/4 + p*p*p/27;
            if(discriminant >= 0){
                double sqrtDisc = Math.sqrt(discriminant);
                double u = Math.cbrt(-q/2 + sqrtDisc);
                double v = Math.cbrt(-q/2 - sqrtDisc);
                roots.add(u+v - b/3);
            }else{
                double r = Math.sqrt(-p*p*p/27);
                double phi = Math.acos(-q/(2*r));
                double t = 2*Math.cbrt(r);
                roots.add(t*Math.cos(phi/3) - b/3);
                roots.add(t*Math.cos((phi+2*Math.PI)/3) - b/3);
                roots.add(t*Math.cos((phi+4*Math.PI)/3) - b/3);
            }
        }
        return roots;
    }

    // Solve quadratic: b t^2 + c t + d = 0
    public static ArrayList<Double> solveQuadratic(double b, double c, double d){
        ArrayList<Double> roots = new ArrayList<>();
        if(Math.abs(b)<1e-12){
            if(Math.abs(c)>1e-12) roots.add(-d/c);
        }else{
            double disc = c*c - 4*b*d;
            if(disc >=0){
                double sqrtDisc = Math.sqrt(disc);
                roots.add((-c + sqrtDisc)/(2*b));
                roots.add((-c - sqrtDisc)/(2*b));
            }
        }
        return roots;
    }
}
