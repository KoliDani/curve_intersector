package intersector;
import java.util.*;


public class Spline extends Segment {

    /**
     * BSpline is a set of bezier curves
     * This implementation uses cubic splines
     */

    private final ArrayList<Point> controls = new ArrayList<>();

    public Spline(Point s, Point e, Collection<Point> c) {
        super(s,e);
        controls.addAll(c);
    }

    /**
     * Spline intersect with line
     */
    @Override
    public ArrayList<Point> intersect(Line other) {
        // decompose bspline into bezier curves
        ArrayList<Point> intersect = new ArrayList<>();
        for (Bezier b : decompose()) {
            intersect.addAll(b.intersect(other));
        }
        return new ArrayList<>();
    }

    /**
     * Spline intersect with arc
     */
    @Override
    public ArrayList<Point> intersect(Arc other) {
        // decompose bspline into bezier curves
        ArrayList<Point> intersect = new ArrayList<>();
        for (Bezier b : decompose()) {
            intersect.addAll(b.intersect(other));
        }
        return new ArrayList<>();
    }

    /**
     * Spline intersect with spline
     */
    @Override
    public ArrayList<Point> intersect(Spline other) {
        // decompose bspline into bezier curves
        ArrayList<Point> intersect = new ArrayList<>();
        for (Bezier b : decompose()) {
            intersect.addAll(b.intersect(other));
        }
        return new ArrayList<>();
    }

    public ArrayList<Bezier> decompose() {
        ArrayList<Bezier> beziers = new ArrayList<>();
        if (controls.size() == 2) {
            // this is a bezier curve
            beziers.add(new Bezier(this));
            return beziers;
        }

        ArrayList<Point> q = new ArrayList<>();
        for (int i = 0; i < controls.size() - 1; i++) {
            Point p1 = controls.get(i);
            Point p2 = controls.get(i + 1);
            if (i == 0 || i == controls.size() - 2) {
                // split in two
                q.add(new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2));
            } else {
                // split in three
                q.add(new Point((p1.x + p2.x) / 3, (p1.y + p2.y) / 3));
                q.add(new Point(2 * (p1.x + p2.x) / 3, 2 * (p1.y + p2.y) / 3));
            }
        }

        ArrayList<Point> k = new ArrayList<>();
        for (int i = 1; i < q.size() / 2; i++) {
            Point q1 = q.get(2 * i);
            Point q2 = q.get((2 * i) + 1);

            k.add(new Point((q1.x + q2.x) / 2, (q1.y + q2.y) / 2));
        }

        ArrayList<Point> first = new ArrayList<>();
        ArrayList<Point> last = new ArrayList<>();

        first.add(s);
        first.add(controls.get(0));
        first.add(q.get(0));
        first.add(k.get(0));

        last.add(k.get(k.size() - 1));
        last.add(q.get(q.size() - 1));
        last.add(controls.get(controls.size() - 1));
        last.add(e);

        beziers.add(new Bezier(first));
        if (k.size() > 1) {

            int qIndex = 1;
            int kIndex = 0;

            while (qIndex < q.size()) {
                ArrayList<Point> b = new ArrayList<>();
                b.add(k.get(kIndex++));
                b.add(q.get(qIndex++));
                b.add(q.get(qIndex++));
                b.add(k.get(kIndex++));
                beziers.add(new Bezier(b));
            }
        }
        beziers.add(new Bezier(last));

        return beziers;
    }

    public ArrayList<Point> getControls() {
        return controls;
    }
}