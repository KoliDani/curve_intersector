package view;

import data.Point;
import data.linetypes.Bezier;
import data.linetypes.Segment;
import intersector.IntersectParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Visualizer extends Application {

    /* ------------------------- Visualizer Pane ------------------------- */
    public static class VisualizerPane extends Pane {

        private enum Mode { NONE, POINT, SEGMENT, ARC, BEZIER }
        private Mode currentMode = Mode.NONE;
        private Point tempStart, tempC1, tempC2;

        private double originX = 0, originY = 0;
        private double scale = 1.0;

        private Circle snapIndicator = new Circle(5, Color.RED);
        private boolean snapping = false;

        public VisualizerPane() {
            setPrefSize(900, 600);

            final Delta dragDelta = new Delta();

            // -------------------- Mouse click --------------------
            setOnMouseClicked(e -> {
                Point clicked = mouseToLogical(e.getX(), e.getY());

                switch (currentMode) {
                    case POINT -> {
                        drawPoint(clicked, Color.RED, null);
                        currentMode = Mode.NONE;
                    }
                    case SEGMENT -> {
                        if (tempStart == null) tempStart = clicked;
                        else {
                            drawSegment(new Segment(tempStart, clicked), Color.BLUE, 2);
                            tempStart = null;
                            currentMode = Mode.NONE;
                        }
                    }
                    case ARC -> {
                        if (tempStart == null) tempStart = clicked;
                        else if (tempC1 == null) tempC1 = clicked;
                        else {
                            Point center = new Point(snapIndicator.getCenterX(), snapIndicator.getCenterY());
                            drawArc(new data.linetypes.Arc(tempStart, center, tempC1), Color.GREEN, 2);
                            tempStart = tempC1 = null;
                            currentMode = Mode.NONE;
                        }
                    }
                    case BEZIER -> {
                        if (tempStart == null) tempStart = clicked;
                        else if (tempC1 == null) tempC1 = clicked;
                        else if (tempC2 == null) tempC2 = clicked;
                        else {
                            drawBezier(new Bezier(tempStart, tempC1, tempC2, clicked), Color.ORANGE, 3, true);
                            tempStart = tempC1 = tempC2 = null;
                            currentMode = Mode.NONE;
                        }
                    }
                }
            });

            // -------------------- Mouse drag for panning --------------------
            setOnMousePressed(e -> {
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
            });

            setOnMouseDragged(e -> {
                double dx = e.getSceneX() - dragDelta.x;
                double dy = e.getSceneY() - dragDelta.y;
                originX += dx;
                originY += dy;
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
                applyTransform();
            });

            // -------------------- Mouse move for arc snap --------------------
            setOnMouseMoved(e -> {
                if (currentMode == Mode.ARC && tempStart != null && tempC1 != null) {
                    Point mouse = mouseToLogical(e.getX(), e.getY());
                    double radius = Point.distance(tempStart, tempC1);
                    double angle = Math.atan2(mouse.getY() - tempStart.getY(), mouse.getX() - tempStart.getX());
                    double snapX = tempStart.getX() + radius * Math.cos(angle);
                    double snapY = tempStart.getY() + radius * Math.sin(angle);

                    if (!snapping) { getChildren().add(snapIndicator); snapping = true; }
                    snapIndicator.setCenterX(snapX);
                    snapIndicator.setCenterY(snapY);
                }
            });

            // -------------------- Scroll zoom --------------------
            addEventFilter(ScrollEvent.SCROLL, e -> {
                if (e.isControlDown()) {
                    double factor = Math.exp(e.getDeltaY() * 0.0015);
                    zoom(factor, e.getX(), e.getY());
                    e.consume();
                }
            });

            // -------------------- Initial transform --------------------
            applyTransform();
        }

        private Point mouseToLogical(double mouseX, double mouseY) {
            double x = (mouseX - originX) / scale;
            double y = (mouseY - originY) / -scale; // flip Y
            return new Point(x, y);
        }

        private void applyTransform() {
            setScaleX(scale);
            setScaleY(-scale);
            setTranslateX(originX);
            setTranslateY(originY);
        }

        private void zoom(double factor, double pivotX, double pivotY) {
            double oldScale = scale;
            scale *= factor;
            scale = Math.max(0.1, Math.min(scale, 10));
            originX = pivotX - (pivotX - originX) * (scale / oldScale);
            originY = pivotY - (pivotY - originY) * (scale / oldScale);
            applyTransform();
        }

        public void clearAll() {
            getChildren().clear();
            snapping = false;
        }

        /* ------------------- Drawing helpers ------------------- */
        public void drawPoint(Point p, Color color, String label) {
            Circle c = new Circle(p.getX(), p.getY(), 4);
            c.setFill(color);
            c.setStroke(Color.BLACK);
            getChildren().add(c);
            if (label != null) {
                Text t = new Text(p.getX() + 6, p.getY() - 6, label);
                t.setFont(Font.font(12));
                getChildren().add(t);
            }
        }

        public void drawSegment(Segment s, Color color, double width) {
            Line l = new Line(s.start.getX(), s.start.getY(), s.end.getX(), s.end.getY());
            l.setStroke(color);
            l.setStrokeWidth(width);
            getChildren().add(l);
        }

        public void drawArc(data.linetypes.Arc a, Color color, double width) {
            double cx = a.getCenter().getX();
            double cy = a.getCenter().getY();
            double radius = a.getRadius();
            if (radius < 1e-6) return;

            double startAngle = Math.toDegrees(Math.atan2(a.start.getY() - cy, a.start.getX() - cx));
            double endAngle = Math.toDegrees(Math.atan2(a.end.getY() - cy, a.end.getX() - cx));

            double dx1 = a.start.getX() - cx;
            double dy1 = a.start.getY() - cy;
            double dx2 = a.end.getX() - cx;
            double dy2 = a.end.getY() - cy;
            double cross = dx1 * dy2 - dy1 * dx2;

            double sweep = endAngle - startAngle;
            if (cross > 0 && sweep > 0) sweep -= 360;
            else if (cross <= 0 && sweep < 0) sweep += 360;

            Arc arc = new Arc(cx, cy, radius, radius, endAngle, sweep);
            arc.setType(ArcType.OPEN);
            arc.setStroke(color);
            arc.setStrokeWidth(width);
            arc.setFill(Color.TRANSPARENT);
            getChildren().add(arc);

            drawPoint(a.start, Color.DARKBLUE, null);
            drawPoint(a.end, Color.DARKBLUE, null);
            drawPoint(a.getCenter(), Color.DARKGREEN, "C");
        }

        public void drawBezier(Bezier b, Color color, double width, boolean showControls) {
            CubicCurve curve = new CubicCurve();
            curve.setStartX(b.start.getX());
            curve.setStartY(b.start.getY());
            curve.setControlX1(b.control1.getX());
            curve.setControlY1(b.control1.getY());
            curve.setControlX2(b.control2.getX());
            curve.setControlY2(b.control2.getY());
            curve.setEndX(b.end.getX());
            curve.setEndY(b.end.getY());
            curve.setStroke(color);
            curve.setStrokeWidth(width);
            curve.setFill(Color.TRANSPARENT);
            getChildren().add(curve);

            if (showControls) {
                drawPoint(b.start, Color.BLACK, "S");
                drawPoint(b.end, Color.BLACK, "E");
                drawPoint(b.control1, Color.GRAY, "C1");
                drawPoint(b.control2, Color.GRAY, "C2");
                drawSegment(new Segment(b.start, b.control1), Color.LIGHTGRAY, 1);
                drawSegment(new Segment(b.end, b.control2), Color.LIGHTGRAY, 1);
            }
        }

        public void drawSegment(Point a, Point b, Color color, double strokeWidth, String label) {
            drawSegment(new Segment(a, b), color, strokeWidth);
            if (label != null) {
                double mx = (a.getX() + b.getX()) / 2;
                double my = (a.getY() + b.getY()) / 2;
                Text t = new Text(mx + 6, my - 6, label);
                t.setFont(Font.font(11));
                getChildren().add(t);
            }
        }

        private static class Delta { double x, y; }
    }

    /* ------------------------- Application ------------------------- */
    @Override
    public void start(Stage stage) {
        VisualizerPane pane = new VisualizerPane();

        BorderPane root = new BorderPane();
        root.setCenter(pane);

        // Sample data
        data.linetypes.Arc a1 = new data.linetypes.Arc(new Point(-500,800), new Point(500,800), new Point(0,800));
        data.linetypes.Arc a2 = new data.linetypes.Arc(new Point(500,0), new Point(-500,0), new Point(0,0));

        Bezier b1 = new Bezier(new Point(0,0), new Point(500,0), new Point(100,800), new Point(300,800));
        data.linetypes.Arc a3 = new data.linetypes.Arc(new Point(-500,700), new Point(500,700), new Point(0,700));

        Segment s1 = new Segment(new Point(-1000,100), new Point(1000,100));
        data.linetypes.Arc a4 = new data.linetypes.Arc(new Point(500,0), new Point(-500,0), new Point(0,0));

        Bezier b2 = new Bezier(new Point(0,0), new Point(500,0), new Point(100,800), new Point(300,800));
        Segment s2 = new Segment(new Point(-500,100), new Point(500,100));

        Segment s3 = new Segment(new Point(0,0), new Point(1000,1000));
        Segment s4 = new Segment(new Point(0,1000), new Point(1000,0));

        pane.drawArc(a1, Color.BLUE, 2);
        pane.drawArc(a2, Color.BLUE, 2);

        pane.drawBezier(b1, Color.BROWN, 2, true);
        pane.drawArc(a3, Color.BROWN, 2);

        pane.drawSegment(s1, Color.RED, 2);
        pane.drawArc(a4, Color.RED, 2);

        pane.drawSegment(s3, Color.ORANGE, 2);
        pane.drawSegment(s4, Color.ORANGE, 2);

        pane.drawSegment(s2, Color.GREEN, 2);
        pane.drawBezier(b2, Color.GREEN, 2, true);

        for (Point p : new IntersectParser(a1,a2).getIntersections()) {
            pane.drawPoint(p, Color.BLACK, null);
        }

        for (Point p : new IntersectParser(b1,a3).getIntersections()) {
            pane.drawPoint(p, Color.BLACK, null);
        }
        for (Point p : new IntersectParser(s1,a4).getIntersections()) {
            pane.drawPoint(p, Color.BLACK, null);
        }
        for (Point p : new IntersectParser(b2,s2).getIntersections()) {
            pane.drawPoint(p, Color.BLACK, null);
        }
        for (Point p : new IntersectParser(s3,s4).getIntersections()) {
            pane.drawPoint(p, Color.BLACK, null);
        }

        Scene scene = new Scene(root, 1100, 600);
        stage.setTitle("JavaFX Shape Visualizer with Grid");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
