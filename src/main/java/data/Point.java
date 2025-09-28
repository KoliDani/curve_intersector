package data;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public void setX(double value) {
        x = value;
    }
    public void setY(double value) {
        y = value;
    }

    public static boolean isBetween(Point a, Point b, Point c) {
        // Return true if c is between a and b (inclusive)
        return (Math.min(a.x, b.x) - 1e-10 <= c.x && c.x <= Math.max(a.x, b.x) + 1e-10) &&
                (Math.min(a.y, b.y) - 1e-10 <= c.y && c.y <= Math.max(a.y, b.y) + 1e-10);
    }

    public static double distance(Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "Point{x: " + x + ", y: " + y +"}";
    }

    public static Point dir(Point p1, Point p2) {
        return new Point(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }

    public static double dist(Point dir) {
        return Math.sqrt(dist2(dir));
    }

    public static double dist2(Point dir) {
        return (dir.getX() * dir.getX() + dir.getY() * dir.getY());
    }

}
