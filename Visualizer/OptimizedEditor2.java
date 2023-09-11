package Visualizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import Source.GVFPathFollower;
import Source.HermitePath;
import Source.Pose;
import Source.Vector2D;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OptimizedEditor2 extends Application {
    HermitePath trajectory = new HermitePath()
        .addPose(84.0, 0.0, new Vector2D(0.0, 250.0))
        .addPose(84.0, 48.0, new Vector2D(0.0, 250.0))
        .addPose(96.0, 60.0, new Vector2D(250.0, 0.0))
        .addPose(118, 72.0, new Vector2D(0.0, 500.0))
        .addPose(80.0, 90.0, new Vector2D(250.0, 0.0))
        .construct();
    GVFPathFollower follower = new GVFPathFollower(trajectory, trajectory.get(0, 0), 0.1, 0.5);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
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

        simulateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    simulate(pathPane);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        nearestPointButton.setPrefWidth(vBox.getPrefWidth());
        addPointButton.setPrefWidth(vBox.getPrefWidth());
        headingModeButton.setPrefWidth(vBox.getPrefWidth());
        simulateButton.setPrefWidth(vBox.getPrefWidth());

        vBox.getChildren().addAll(nearestPointButton, addPointButton, headingModeButton, simulateButton);

        graphField(pathPane);
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
                Vector2D gvf = follower.calculateGVF();
                Line line = new Line(mousePosition.x * 5, mousePosition.y * 5, mousePosition.x * 5 + gvf.x * 5, mousePosition.y * 5 + gvf.y * 5);
                line.setStrokeWidth(3);
                
                pathPane.getChildren().addAll(line);
                try {
                    graphField(pathPane);
                } catch (FileNotFoundException e) {}
                graphPath(pathPane, trajectory);
                
                
            }
        });

        Scene scene = new Scene(root, 850, 720);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hermite Spline Editor (OPTIMIZED)");
        primaryStage.show();
    }

    public void simulate(Pane pathPane) throws FileNotFoundException {
        AtomicInteger i = new AtomicInteger(0);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.05));
        pause.setOnFinished(event -> {

                System.out.println(i.get() / 100.0);
                pathPane.getChildren().clear();
                Pose currentPose = trajectory.get(i.get() / 100.0, 0);
                follower.setCurrentPose(currentPose);
                Vector2D gvf = follower.calculateGVF();

                Circle circ = new Circle(currentPose.x, currentPose.y, 10, Color.RED);

                
                System.out.println("here");

                Line line = new Line(currentPose.x * 5, currentPose.y * 5, currentPose.x * 5 + gvf.x * 5, currentPose.y * 5 + gvf.y * 5);
                line.setStrokeWidth(3);
                
                try {
                    graphField(pathPane);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                graphPath(pathPane, trajectory);
                pathPane.getChildren().addAll(line, circ);

                if (i.get() < (trajectory.length() * 100)) {
                    i.set(i.get() + 1);
                    pause.play();
                }
            }
        );
        pause.play();
    }

    public void graphField(Pane pathPane) throws FileNotFoundException {
        final ImageView currentImage = new ImageView();
        Image fieldImage = new Image(new FileInputStream("C:\\Users\\Mason\\OneDrive\\Coding\\HermitePathVisualizer\\Images\\cs_field.png"));
        currentImage.setImage(fieldImage);
        currentImage.setOpacity(0.5);

        pathPane.getChildren().add(currentImage);
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
                line.setStroke(Color.WHITE);
                line.setStrokeWidth(10);
                pathGroup.getChildren().add(line);
                pastPose = currentPose;
            } catch(Exception e) {
                ;;
            }
        }

        pathPane.getChildren().addAll(pathGroup);
    }
}
