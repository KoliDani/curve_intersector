package intersector;

import data.linetypes.Arc;
import data.linetypes.Bezier;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

public class ArcBezier extends Intersect {
    public ArcBezier(IntersectParser intersectParser) {
        Arc arc = (Arc) intersectParser.l1;
        Bezier bez = (Bezier) intersectParser.l2;

        BisectionSolver solver = new BisectionSolver(1e-10);
        // Define f(t) = distance squared from circle center minus radius squared
        UnivariateFunction f = t -> {
            double u = 1 - t;
            double x = u*u*u*bez.start.getX() + 3*u*u*t*bez.control1.getX() + 3*u*t*t*bez.control2.getX() + t*t*t*bez.end.getX();
            double y = u*u*u*bez.start.getY() + 3*u*u*t*bez.control1.getY() + 3*u*t*t*bez.control2.getY() + t*t*t*bez.end.getY();
            return (x - arc.getCenter().getX())*(x - arc.getCenter().getX()) + (y - arc.getCenter().getY())*(y - arc.getCenter().getY()) - arc.getRadius()*arc.getRadius();
        };

        // Subdivide [0,1] to find sign changes
        int steps = 100;
        double prevT = 0;
        double prevValue = f.value(prevT);

        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double value = f.value(t);

            if (prevValue * value <= 0) { // sign change detected
                try {
                    double rootT = solver.solve(100, f, prevT, t);
                    intersections.add(bez.evaluate(rootT));
                } catch (Exception e) {
                    // Ignore if solver fails
                }
            }

            prevT = t;
            prevValue = value;
        }
    }
}
