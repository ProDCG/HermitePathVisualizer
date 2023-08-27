package Visualizer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class OptimizedEditor2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane pathPane = new Pane();
        pathPane.setPrefSize(720, 720);
        Pane menuPane = new Pane();
        menuPane.setPrefSize(100, 720);

        Button button = new Button("Button");
        button.setLayoutX(10);
        button.setLayoutY(10);
        menuPane.getChildren().add(button);

        Pane root = new Pane();
        root.getChildren().addAll(pathPane, menuPane);

        Scene scene = new Scene(root, 820, 720);
        
        Scale scale = new Scale(1, -1, 0, scene.getHeight());
        pathPane.getTransforms().add(scale);

        Circle point = new Circle(100, 100, 5, Color.RED);
        pathPane.getChildren().add(point);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hermite Spline Editor");
        primaryStage.show();
    }
}
