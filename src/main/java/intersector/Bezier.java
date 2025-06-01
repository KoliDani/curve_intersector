package intersector;
import java.util.*;


public class Bezier extends Line {
    private final ArrayList<Point> controls = new ArrayList<>();
    private static final double TOL = 0.00001;

    public Bezier(Spline spline) {
        super(spline.s, spline.e);
        controls.addAll(spline.getControls());
    }

    public Bezier(ArrayList<Point> c) {
        super(c.get(0), c.get(c.size() - 1));
        controls.addAll(c);
        controls.remove(0);
        controls.remove(controls.size() - 1);
    }

    /**
     * Bezier intersect with line
     */
    @Override
    public ArrayList<Point> intersect(Line other) {
        ArrayList<Point> intersect = new ArrayList<>();
        if (!isIntersectWithLine(other)) {
            return intersect;
        }

        double[] d = signedDistanceFromLine(lineCoeff(other.s, other.e));

        // initialize interval
        double t_min = 0;
        double t_max = 1;

        int iterationLimit = 500;
        while (t_max - t_min > TOL) {
            // Get control points for D(t) in [t_min, t_max]
            double[] dSeg = getBezierSegment(d, t_min, t_max);
            double width = (t_max - t_min) / 2;

            ArrayList<Point> hullPoints = new ArrayList<>();
            hullPoints.add(new Point(t_min, dSeg[0]));
            hullPoints.add(new Point(t_min + width/3, dSeg[1]));
            hullPoints.add(new Point(t_min + 2*width/3, dSeg[2]));
            hullPoints.add(new Point(t_max, dSeg[3]));

            ArrayList<Point> convexHull = convexHull(hullPoints);
            double tMin = Double.POSITIVE_INFINITY;
            double tMax = Double.NEGATIVE_INFINITY;
            boolean found = false;

            for (int i = 0; i < convexHull.size(); i++) {
                Point p1 = convexHull.get(i);
                Point p2 = convexHull.get((i + 1) % convexHull.size());

                // Check for zero distance
                if (Math.abs(p1.y) < TOL) {
                    tMin = Math.min(tMin, p1.x);
                    tMax = Math.max(tMax, p1.x);
                    found = true;
                }

                // Check edge crossing
                if (p1.y * p2.y <= 0 && p1.y != p2.y) {
                    double tCross = p1.x - p1.y * (p2.x - p1.x) / (p2.y - p1.y);
                    tMin = Math.min(tMin, tCross);
                    tMax = Math.max(tMax, tCross);
                    found = true;
                }
            }

            if (!found) break;  // No intersection

            if (tMax - tMin < TOL) {
                // converged solution
                intersect.add(evaluate((tMin + tMax) / 2));
                break;
            }

            // Update interval
            t_min = tMin;
            t_max = tMax;

            if (--iterationLimit < 0) {
                // solution not converged, split bezier and try again
                for (Bezier b : split(0.5)) {
                    intersect.addAll(b.intersect(other));
                }
                return intersect;
            }
        }

        return intersect;
    }

    // Helper: Compute convex hull and intersect with d=0
    private static ArrayList<Double[]> getConvexHullXIntersection(Point[] points) {
        // Actual implementation would:
        // 1. Compute convex hull (using Jarvis march or Graham scan)
        // 2. Find edges crossing d=0
        // 3. Return min/max t-values of crossings
//        Double t_min = 0;
//        Double t_max = 0;
        return new ArrayList<Double[]>(); // Simplified
    }

    private boolean isIntersectWithLine(Line other) {
        for (Line l : polyLines()) {
            if (l.isIntersect(other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bezier intersect with arc
     */
    @Override
    public ArrayList<Point> intersect(Arc other) {
        return new ArrayList<>();
    }

    /**
     * Bezier intersect with bezier
     */
    @Override
    public ArrayList<Point> intersect(Bezier other) {
        return new ArrayList<>();
    }

    /**
     * Bezier intersect with spline
     */
    @Override
    public ArrayList<Point> intersect(Spline other) {
        ArrayList<Point> intersect = new ArrayList<>();
        for (Bezier b : other.decompose()) {
            intersect.addAll(intersect(b));
        }
        return intersect;
    }


    private ArrayList<Bezier> split(double t) {
        ArrayList<ArrayList<Point>> newControls = new ArrayList<>();
        ArrayList<Point> newBezier1 = new ArrayList<>();
        newBezier1.add(s);
        ArrayList<Point> newBezier2 = new ArrayList<>();
        newBezier2.add(e);

        // initialize newControls
        int nrControl = 2 + controls.size();
        for (int j = 0; j < nrControl; j++) {
            newControls.add(new ArrayList<>());
        }

        newControls.get(0).addAll(convexHull());
        for (int j = 1; j < nrControl; j++) {
            ArrayList<Point> tmp = newControls.get(j-1);
            ArrayList<Point> fill = newControls.get(j);

            for (int i = 0; i < tmp.size() - 1; i++) {
                Point p1 = tmp.get(i);
                Point p2 = tmp.get(i + 1);

                fill.add(Point.interp(p1, p2, t));
            }

            newBezier1.add(fill.get(0));
            newBezier2.add(fill.get(fill.size() - 1));
        }

        // reverse newBezier
        Collections.reverse(newBezier2);

        // create the new splines
        ArrayList<Bezier> beziers = new ArrayList<>();
        beziers.add(new Bezier(newBezier1));
        beziers.add(new Bezier(newBezier2));

        return beziers;
    }

    private ArrayList<Point> convexHull(Collection<Point> hullPoints) {
        ArrayList<Point> all = new ArrayList<>(hullPoints);
        ArrayList<Point> ch = new ArrayList<>();
        int nrPoint = all.size()-1;
        int i = 0;     // current point in convex hull;
        ch.add(all.get(i));
        // search right for point with highest angle
        while(i < nrPoint) {
            double max = Double.MIN_VALUE;
            int imax = i;

            for (int j = i + 1; j <= nrPoint; j++) {
                double a = (all.get(j).y - all.get(i).y) / (all.get(j).x - all.get(i).x);
                if (max < a) {
                    max = a;
                    imax = j;
                }
            }

            ch.add(all.get(imax));
            i = imax;
        }
        while(i > 0) {
            double max = Double.MIN_VALUE;
            int imax = i;

            for (int j = 0; j < i; j++) {
                double a = (all.get(i).y - all.get(j).y) / (all.get(i).x - all.get(j).x);
                if (max < a) {
                    max = a;
                    imax = j;
                }
            }

            ch.add(all.get(imax));
            i = imax;
        }

        return ch;
    }

    private ArrayList<Point> convexHull() {
        // Return the convex hull of the Bezier coefficients calculated using
        // Jarvis's march.
        return convexHull(getAll());
    }

    private ArrayList<Point> getAll() {
        ArrayList<Point> all = new ArrayList<>();
        all.add(s);
        all.addAll(controls);
        all.add(e);

        return all;
    }

    private ArrayList<Line> polyLines() {
        ArrayList<Line> poly = new ArrayList<>();
        poly.add(new Line(s, controls.get(0)));
        for (int i = 0; i < controls.size() - 1; i++) {
            poly.add(new Line(controls.get(i), controls.get(i + 1)));
        }
        poly.add(new Line(controls.get(controls.size() - 1), e));
        return poly;
    }

    private double[] lineCoeff(Point ls, Point le) {
        Point d = dir(ls,le);
        double length = dist(d);

        double[] coeff = new double[3];
        // ax + bx + c = 0 , a^2 + b^2 = 1
        coeff[0] = -d.y / length;
        coeff[1] = d.x / length;
        coeff[2] = -((coeff[0] * s.x) + (coeff[1] * s.y));
        return coeff;
    }

    private double[] signedDistanceFromLine(double[] coeff) {
        double[] d = new double[4];
        d[0] = coeff[0] * s.x + coeff[1] * s.y + coeff[2];
        d[1] = coeff[0] * controls.get(0).x + coeff[1] * controls.get(1).y + coeff[2];
        d[2] = coeff[0] * controls.get(0).x + coeff[1] * controls.get(1).y + coeff[2];
        d[3] = coeff[0] * e.x + coeff[1] * e.y + coeff[2];
        return d;
    }

    /**
     * Evaluates a cubic Bézier curve at parameter t.
     *
     * @param controls Array of 4 control points (P0, P1, P2, P3)
     * @param t Parameter value in [0, 1]
     * @return Point on the curve at t
     */
    public Point evaluate(double t) {
        // Level 1 interpolations
        Point q0 = Point.interp(s, controls.get(0), t);
        Point q1 = Point.interp(controls.get(0), controls.get(1), t);
        Point q2 = Point.interp(controls.get(1), e, t);

        // Level 2 interpolations
        Point r0 = Point.interp(q0, q1, t);
        Point r1 = Point.interp(q1, q2, t);

        // Level 3 (final point)
        return Point.interp(r0, r1, t);
    }

    // Extracts Bézier segment for [a, b] using de Casteljau
    private static double[] getBezierSegment(double[] d, double a, double b) {
        if (a == 0 && b == 1) return d.clone();
        double[] right = splitBezierRight(d, a);
        double s = (b - a) / (1 - a);
        return splitBezierLeft(right, s);
    }

    // Splits Bézier curve at t and returns left segment
    private static double[] splitBezierLeft(double[] d, double t) {
        double[] left = new double[4];
        double b01 = (1 - t) * d[0] + t * d[1];
        double b12 = (1 - t) * d[1] + t * d[2];
        double b23 = (1 - t) * d[2] + t * d[3];
        double b012 = (1 - t) * b01 + t * b12;
        double b123 = (1 - t) * b12 + t * b23;
        double b0123 = (1 - t) * b012 + t * b123;

        left[0] = d[0];
        left[1] = b01;
        left[2] = b012;
        left[3] = b0123;
        return left;
    }

    // Splits Bézier curve at t and returns right segment
    private static double[] splitBezierRight(double[] d, double t) {
        double[] right = new double[4];
        double b01 = (1 - t) * d[0] + t * d[1];
        double b12 = (1 - t) * d[1] + t * d[2];
        double b23 = (1 - t) * d[2] + t * d[3];
        double b012 = (1 - t) * b01 + t * b12;
        double b123 = (1 - t) * b12 + t * b23;
        double b0123 = (1 - t) * b012 + t * b123;

        right[0] = b0123;
        right[1] = b123;
        right[2] = b23;
        right[3] = d[3];
        return right;
    }
}
