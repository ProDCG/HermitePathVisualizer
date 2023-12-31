package Visualizer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import Source.HermitePath;
import Source.HermitePose;
import Source.Pose;
import Source.Vector2D;

public class HermiteSplineEditor extends Application {
    private boolean headingMode = false;
    private final List<Point2D> controlPointPositions = new ArrayList<>();
    private final List<Point2D> tangentPointPositions = new ArrayList<>();
    private final List<Point2D> headingPointPositions = new ArrayList<>();
    private Group pathGroup;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(720);
        primaryStage.setWidth(820);
        primaryStage.setMinHeight(720);
        primaryStage.setMaxHeight(720);
        Pane pane = new Pane();

        // Button toggleButton = new Button("Toggle Mode");
        // toggleButton.setOnAction(event -> toggleMode(pane, toggleButton));
        // pane.getChildren().add(toggleButton);

        Button addButton = new Button("Add Point");
        addButton.setOnAction(event -> addPoints(primaryStage, pane, addButton));
        pane.getChildren().add(addButton);

        addPoints(50, 50, 50, 75, 50, 50);
        addPoints(100, 100, 50, 0, 50, 0);
        addPoints(300, 300, 0, 35, 0, 25);

        toggleMode(pane, addButton);
        toggleMode(pane, addButton);

        Line borderLine = new Line(primaryStage.getWidth() - 100, primaryStage.getHeight(), primaryStage.getWidth() - 100, 0);
        borderLine.setFill(Color.BLACK);
        borderLine.setStrokeWidth(5);
        Line borderLine2 = new Line(primaryStage.getWidth() - 100, primaryStage.getHeight(), 0, primaryStage.getHeight());
        borderLine2.setFill(Color.BLACK);
        borderLine2.setStrokeWidth(5);
        pane.getChildren().addAll(borderLine, borderLine2);

        BorderPane right = new BorderPane();
        VBox rightSide = new VBox();
        rightSide.setPrefWidth(75);
        rightSide.getChildren().addAll(addButton);
        right.setRight(rightSide);

        right.setCenter(pane);

        Scene scene = new Scene(right, 820, 720);
        primaryStage.setTitle("Hermite Spline Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addPoints(Stage primaryStage, Pane pane, Button addButton) {
        controlPointPositions.add(new Point2D(360 + 5 * controlPointPositions.size(), 360));
        tangentPointPositions.add(new Point2D(410 + 5 * controlPointPositions.size(), 410));
        headingPointPositions.add(new Point2D(410 + 5 * controlPointPositions.size(), 410));

        toggleMode(pane, addButton);
        toggleMode(pane, addButton);

        Line borderLine = new Line(primaryStage.getWidth() - 100, primaryStage.getHeight(), primaryStage.getWidth() - 100, 0);
        borderLine.setFill(Color.BLACK);
        borderLine.setStrokeWidth(5);
        Line borderLine2 = new Line(primaryStage.getWidth() - 100, primaryStage.getHeight(), 0, primaryStage.getHeight());
        borderLine2.setFill(Color.BLACK);
        borderLine2.setStrokeWidth(5);

        pane.getChildren().addAll(borderLine, borderLine2);
    }

    private void toggleMode(Pane pane, Button toggleButton) {
        headingMode = !headingMode;
        pane.getChildren().clear();
        pane.getChildren().add(toggleButton);
        
        for (int i = 0; i < controlPointPositions.size(); i++) {
            createPoints(pane, i);
        }
        createPath(pane);
    }

    private void addPoints(double x, double y, double ttx, double tty, double htx, double hty) {
        controlPointPositions.add(new Point2D(x, y));
        tangentPointPositions.add(new Point2D(x + ttx, y + tty));
        headingPointPositions.add(new Point2D(x + htx, y + hty));
    }

    private void createPoints(Pane pane, int index) {
        Point2D controlPointPosition = controlPointPositions.get(index);
        double x = controlPointPosition.getX();
        double y = controlPointPosition.getY();

        Circle controlPoint = new Circle(x, y, 5);
        controlPoint.setFill(Color.BLUE);

        Line tangentLine = new Line(x, y, x, y);
        tangentLine.getStrokeDashArray().addAll(5.0, 5.0);

        if (!headingMode) {
            Circle tangentPoint = new Circle();
    
            controlPoint.setOnMousePressed(event -> controlPoint.setUserData(new Point2D(event.getX(), event.getY())));
            controlPoint.setOnMouseDragged(event -> {
                Point2D initialClick = (Point2D) controlPoint.getUserData();
                double deltaX = event.getX() - initialClick.getX();
                double deltaY = event.getY() - initialClick.getY();
    
                double newX = controlPoint.getCenterX() + deltaX;
                double newY = controlPoint.getCenterY() + deltaY;
    
                controlPoint.setCenterX(newX);
                controlPoint.setCenterY(newY);
                controlPointPositions.set(index, new Point2D(newX, newY));
    
                // Update tangent line start point
                tangentLine.setStartX(newX);
                tangentLine.setStartY(newY);
    
                // Update tangent point's position
                double newTx = tangentPoint.getCenterX() + deltaX;
                double newTy = tangentPoint.getCenterY() + deltaY;
    
                tangentPoint.setCenterX(newTx);
                tangentPoint.setCenterY(newTy);
    
                // Update tangent line end point
                tangentLine.setEndX(newTx);
                tangentLine.setEndY(newTy);
    
                controlPoint.setUserData(new Point2D(event.getX(), event.getY()));
                createPath(pane);
            });
    
            pane.getChildren().add(controlPoint);
            createTangentPoint(pane, index, x, y, tangentPoint, tangentLine); // This will set the initial position
        } else {
            pane.getChildren().add(controlPoint);
            createHeadingPoint(pane, index, x, y);
        }

        System.out.println(controlPointPositions.get(0).getX());
    }

    private void createHeadingPoint(Pane pane, int index, double x, double y) {
        Point2D headingPointPosition = headingPointPositions.get(index);
        double hx = headingPointPosition.getX() - x;
        double hy = headingPointPosition.getY() - y;

        Circle headingPoint = new Circle(x + hx, y + hy, 5, Color.TRANSPARENT); // Heading points are red
        headingPoint.setStroke(Color.RED);

        Line headingLine = new Line(x, y, x + hx, y + hy);
        headingLine.setStroke(Color.RED);
        headingLine.getStrokeDashArray().addAll(5.0, 5.0); // Dashed red line for heading

        // Logic for dragging heading points
        headingPoint.setOnMousePressed(event -> headingPoint.setUserData(new Point2D(event.getX(), event.getY())));
        headingPoint.setOnMouseDragged(event -> {
            Point2D initialClick = (Point2D) headingPoint.getUserData();
            double deltaX = event.getX() - initialClick.getX();
            double deltaY = event.getY() - initialClick.getY();

            double newX = headingPoint.getCenterX() + deltaX;
            double newY = headingPoint.getCenterY() + deltaY;

            headingPoint.setCenterX(newX);
            headingPoint.setCenterY(newY);

            headingPointPositions.set(index, new Point2D(newX, newY));

            headingLine.setEndX(headingLine.getEndX() + deltaX);
            headingLine.setEndY(headingLine.getEndY() + deltaY);

            headingPoint.setUserData(new Point2D(event.getX(), event.getY()));
        });

        pane.getChildren().addAll(headingPoint, headingLine);
    }

    private void createTangentPoint(Pane pane, int index, double x, double y, Circle tangentPoint, Line tangentLine) {
        Point2D tangentPointPosition = tangentPointPositions.get(index);
        double tx = tangentPointPosition.getX() - x;
        double ty = tangentPointPosition.getY() - y;

        tangentPoint.setCenterX(x + tx);
        tangentPoint.setCenterY(y + ty);
        tangentPoint.setRadius(5);
        tangentPoint.setFill(Color.TRANSPARENT);
        tangentPoint.setStroke(Color.BLUE);
        tangentPoint.setStrokeWidth(2);

        // Set the initial position of the tangent line
        tangentLine.setStartX(x);
        tangentLine.setStartY(y);
        tangentLine.setEndX(x + tx);
        tangentLine.setEndY(y + ty);
        tangentLine.getStrokeDashArray().addAll(5.0, 5.0);
    
        // Logic for dragging tangent points
        tangentPoint.setOnMousePressed(event -> tangentPoint.setUserData(new Point2D(event.getX(), event.getY())));
        tangentPoint.setOnMouseDragged(event -> {
            Point2D initialClick = (Point2D) tangentPoint.getUserData();
            double deltaX = event.getX() - initialClick.getX();
            double deltaY = event.getY() - initialClick.getY();
    
            double newX = tangentPoint.getCenterX() + deltaX;
            double newY = tangentPoint.getCenterY() + deltaY;
    
            tangentPoint.setCenterX(newX);
            tangentPoint.setCenterY(newY);
            tangentLine.setEndX(tangentLine.getEndX() + deltaX);
            tangentLine.setEndY(tangentLine.getEndY() + deltaY);
    
            tangentPointPositions.set(index, new Point2D(newX, newY));
    
            tangentPoint.setUserData(new Point2D(event.getX(), event.getY()));
            createPath(pane);
        });
    
        pane.getChildren().addAll(tangentPoint, tangentLine);
    }

    private void createPath(Pane pane) {
        HermitePath path = new HermitePath();
        for (int i = 0; i < controlPointPositions.size(); i++) {
            Point2D currentControlPoint = controlPointPositions.get(i);
            Point2D currentTangentPoint = tangentPointPositions.get(i);
            Vector2D currentTangentVector = new Vector2D(currentTangentPoint.getX() - currentControlPoint.getX(), currentTangentPoint.getY() - currentControlPoint.getY());
            currentTangentVector = currentTangentVector.mult(200);
            HermitePose currentPose = new HermitePose(currentControlPoint.getX(), currentControlPoint.getY(), currentTangentVector);
            path = path.addPose(currentPose);
        }
        path = path.construct();

        int numIntermediatePoints = (path.length() - 1) * 100;
        int nPoints = numIntermediatePoints + 1;

        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];

        for (int i = 0; i <= numIntermediatePoints; i++) {
            double t = ((double) i) / ((double) numIntermediatePoints / ((double) path.length() - 1));
            Pose pose = path.get(t, 0);
            xPoints[i] = (int) (pose.x);
            yPoints[i] = (int) (pose.y);
        }

        if (pathGroup != null) {
            pane.getChildren().remove(pathGroup); // Remove the old path group if it exists
        }
    
        pathGroup = new Group(); // a group to hold the lines
        for (int i = 0; i < xPoints.length - 2; i++) {
            // create a line from each pair of points
            Line line = new Line(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            pathGroup.getChildren().add(line);
        }
    
        pane.getChildren().addAll(pathGroup);
    }
}
