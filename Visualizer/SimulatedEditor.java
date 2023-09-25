package Visualizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulatedEditor extends Application {

    HermitePath trajectory = new HermitePath()
        .addPose(72, 0, new Vector2D(0.0, 500.0))
        .addPose(72, 72, new Vector2D(0.0, 750.0))
        .addPose(96, 96, new Vector2D(750.0, 0.0))
        .addPose(120, 96, new Vector2D(250.0, 0.0))
        .addPose(134, 120, new Vector2D(0.0, 500.0))
        .addPose(120, 134, new Vector2D(250.0, 0.0))
        .addPose(100, 134, new Vector2D(1000.0, 0.0))
        .addPose(60, 100, new Vector2D(250.0, 0.0))
        .construct();

    Pose currentPose = new Pose(30, 30, Math.PI);
    GVFPathFollower follower = new GVFPathFollower(trajectory, currentPose, 0.4, 1, 0.1);

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
                } catch (FileNotFoundException e1) {}
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

        Scene scene = new Scene(root, 850, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulated Editor");
        primaryStage.show();
    }

    public void simulate(Pane pathPane) throws FileNotFoundException {

        Random r = new Random();
        int x = r.nextInt(144);
        int y = r.nextInt(144);
        // int y = 0;

        currentPose = new Pose(x, y, Math.PI);
        follower.setCurrentPose(currentPose);

        follower.resetV();

        PauseTransition pause = new PauseTransition(Duration.seconds(0.005));
        pause.setOnFinished(event -> {
            pathPane.getChildren().clear();
            Pose gvf = follower.calculateGVF();

            Line line = new Line(currentPose.x * 5, currentPose.y * 5, currentPose.x * 5 + gvf.x * 5, currentPose.y * 5 + gvf.y * 5);
            line.setStrokeWidth(3);
            line.setStroke(Color.RED);
            Circle circ = new Circle(currentPose.x * 5, currentPose.y * 5, 5, Color.RED);

            Pose nearesTPose = trajectory.get(follower.nearestT, 0);
            Circle nearestTPose = new Circle(nearesTPose.x * 5, nearesTPose.y * 5, 5, Color.RED);

            try {
                graphField(pathPane);
            } catch (FileNotFoundException e) {}
            graphPath(pathPane, trajectory);
            pathPane.getChildren().addAll(line, circ, nearestTPose);

            double deltaX = gvf.x * 0.005;
            double deltaY = gvf.y * 0.005;

            currentPose.y = currentPose.y + (deltaY);
            currentPose.x = currentPose.x + (deltaX);
            follower.setCurrentPose(new Pose(currentPose.x, currentPose.y, gvf.heading));

            if (!follower.isFinished()) {
                pause.play();
            } else {
                System.out.println("Done");   
            }
        });
        pause.play();
        System.out.println("The mason gamer");
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
                line.setStroke(Color.BLACK);
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
