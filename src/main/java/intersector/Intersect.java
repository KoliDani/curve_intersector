package intersector;

import data.Point;

import java.util.ArrayList;

public class Intersect {
    protected final ArrayList<Point> intersections = new ArrayList<>();

    public Intersect() {}

    public ArrayList<Point> getResults() {
        return intersections;
    }
}
