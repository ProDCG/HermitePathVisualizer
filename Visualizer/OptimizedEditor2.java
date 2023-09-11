package Visualizer;

import org.w3c.dom.events.Event;

import Source.GVFPathFollower;
import Source.HermiteInterpolator;
import Source.HermitePath;
import Source.Pose;
import Source.Vector2D;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class OptimizedEditor2 extends Application {
    HermitePath trajectory = new HermitePath()
        .addPose(84.0, 0.0, new Vector2D(0.0, 250.0))
        .addPose(84.0, 48.0, new Vector2D(0.0, 250.0))
        .addPose(96.0, 60.0, new Vector2D(250.0, 0.0))
        .addPose(118, 72.0, new Vector2D(0.0, 250.0))
        .construct();
    GVFPathFollower follower = new GVFPathFollower(trajectory, trajectory.get(0, 0), 0, 0);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setPrefSize(850, 720);

        Pane pathPane = new Pane();
        pathPane.setPrefSize(720, 720);

        VBox vBox = new VBox();
        vBox.setTranslateX(720);
        vBox.setPrefWidth(130);

        Button nearestPointButton = new Button("Nearest Point");
        Button addPointButton = new Button("Add Point");
        Button headingModeButton = new Button("Heading Mode");
        Button simulateButton = new Button("Simulate Button");

        nearestPointButton.setPrefWidth(vBox.getPrefWidth());
        addPointButton.setPrefWidth(vBox.getPrefWidth());
        headingModeButton.setPrefWidth(vBox.getPrefWidth());
        simulateButton.setPrefWidth(vBox.getPrefWidth());

        vBox.getChildren().addAll(nearestPointButton, addPointButton, headingModeButton, simulateButton);

        graphPath(pathPane, trajectory);

        Line line = new Line(720, 0, 720, 720);
        Scale scale = new Scale(1, -1, 0, pathPane.getHeight());
        pathPane.setTranslateY(720);
        pathPane.getTransforms().add(scale);
        pathPane.getChildren().addAll(line);

        root.getChildren().addAll(pathPane, vBox);
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                pathPane.getChildren().clear();
                Vector2D mousePosition = new Vector2D(event.getSceneX() / 5, (-event.getSceneY() + 720) / 5);
                follower.setCurrentPose(new Pose(mousePosition, 0));
                double t = follower.getNearestT();
                
                Pose tPose = trajectory.get(t, 0);

                Circle circle = new Circle(tPose.x * 5, tPose.y * 5, 5);
                circle.setStroke(Color.RED);
                

                pathPane.getChildren().addAll(circle);
                graphPath(pathPane, trajectory);
            }
        });

        Scene scene = new Scene(root, 850, 720);
        

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hermite Spline Editor (OPTIMIZED)");
        primaryStage.show();
    }

    public void graphPath(Pane pathPane, HermitePath path) {
        for (int i = 0; i < path.length(); i++) {
            path = path.construct();
        }

        int numIntermediatePoints = (path.length()) * 100;
        Group pathGroup = new Group();
        Pose pastPose = path.get(0, 0);
        
        for (int i = 1; i <= numIntermediatePoints; i++) {
            double t = ((double) i) / ((double) numIntermediatePoints / ((double) path.length()));
            Line line = new Line();
            try {
                Pose currentPose = path.get(t, 0);
                line.setStartX(pastPose.x * 5);
                line.setStartY(pastPose.y * 5);
                line.setEndX(currentPose.x * 5);
                line.setEndY(currentPose.y * 5);
                pathGroup.getChildren().add(line);
                pastPose = currentPose;
            } catch(Exception e) {
                ;;
            }
        }

        pathPane.getChildren().addAll(pathGroup);
    }
}
