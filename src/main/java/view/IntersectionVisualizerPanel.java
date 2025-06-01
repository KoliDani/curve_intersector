package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class IntersectionVisualizerPanel extends JPanel {
    private final List<Shape> shapes = new ArrayList<>();
    private final List<Point2D> intersections = new ArrayList<>();
    private static final double POINT_SIZE = 5.0;

    public IntersectionVisualizerPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    public void addLine(Line2D line) {
        shapes.add(line);
        repaint();
    }

    public void addArc(Arc2D arc) {
        shapes.add(arc);
        repaint();
    }

    public void addCubicBezier(Point2D p1, Point2D ctrl1, Point2D ctrl2, Point2D p2) {
        shapes.add(new CubicCurve2D.Double(
                p1.getX(), p1.getY(),
                ctrl1.getX(), ctrl1.getY(),
                ctrl2.getX(), ctrl2.getY(),
                p2.getX(), p2.getY()
        ));
        repaint();
    }

    public void addBSpline(List<CubicCurve2D> segments) {
        shapes.addAll(segments);
        repaint();
    }

    public void addIntersection(Point2D point) {
        intersections.add(point);
        repaint();
    }

    public void clear() {
        shapes.clear();
        intersections.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set thinner stroke (default is 1.0f, we'll use 0.5f)
        g2.setStroke(new BasicStroke(0.25f));  // Adjust this value as needed

        // Skip if empty
        if (shapes.isEmpty() && intersections.isEmpty()) return;

        // Calculate bounds including all shapes and points
        Rectangle2D bounds = calculateTotalBounds();
        if (bounds.isEmpty()) return;

        // Apply transformation to fit content
        applyViewportTransform(g2, bounds);

        // Draw all geometric objects in black
        g2.setColor(Color.BLACK);
        for (Shape shape : shapes) {
            g2.draw(shape);
        }

        // Draw intersection points in red
        drawIntersections(g2);
    }

    private Rectangle2D calculateTotalBounds() {
        Rectangle2D bounds = null;

        // Include shapes
        for (Shape shape : shapes) {
            Rectangle2D shapeBounds = shape.getBounds2D();
            if (bounds == null) bounds = shapeBounds;
            else bounds.add(shapeBounds);
        }

        // Include intersection points
        for (Point2D p : intersections) {
            Rectangle2D pointBounds = new Rectangle2D.Double(p.getX(), p.getY(), 0, 0);
            if (bounds == null) bounds = pointBounds;
            else bounds.add(pointBounds);
        }

        return bounds != null ? bounds : new Rectangle2D.Double(0, 0, 0, 0);
    }

    private void applyViewportTransform(Graphics2D g2, Rectangle2D contentBounds) {
        Insets insets = getInsets();
        int contentWidth = getWidth() - insets.left - insets.right;
        int contentHeight = getHeight() - insets.top - insets.bottom;

        double padding = 10;
        double scale = calculateScale(contentBounds, contentWidth, contentHeight, padding);

        // Calculate translation with centering and padding
        double tx = insets.left - contentBounds.getX() * scale + padding * scale;
        double ty = insets.top - contentBounds.getY() * scale + padding * scale;

        // Apply transformations
        g2.translate(tx, ty);
        g2.scale(scale, scale);
    }

    private double calculateScale(Rectangle2D bounds, int width, int height, double padding) {
        double contentWidth = bounds.getWidth() + 2 * padding;
        double contentHeight = bounds.getHeight() + 2 * padding;
        double scaleX = width / contentWidth;
        double scaleY = height / contentHeight;
        return Math.min(scaleX, scaleY);
    }

    private void drawIntersections(Graphics2D g2) {
        // Save current transform for later restoration
        AffineTransform originalTransform = g2.getTransform();

        // Draw points in screen coordinates (identity transform)
        g2.setTransform(new AffineTransform());
        g2.setColor(Color.RED);

        for (Point2D p : intersections) {
            Point2D screenPoint = originalTransform.transform(p, null);
            double x = screenPoint.getX() - POINT_SIZE / 2;
            double y = screenPoint.getY() - POINT_SIZE / 2;
            g2.fill(new Ellipse2D.Double(x, y, POINT_SIZE, POINT_SIZE));
        }

        // Restore original transform
        g2.setTransform(originalTransform);
    }
}
