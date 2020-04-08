package ch.epfl.rigel.gui;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Classe d'exemple de canvas avec paths.
 */
public class CanvasExample extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        // Create the Canvas
        Canvas canvas = new Canvas(400, 200);

        // Get the graphics context of the canvas
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        // Set line width
        ctx.setLineWidth(2.0);

        // Set the Color
        ctx.setStroke(Color.GREEN);

        // Set fill color
        ctx.setFill(Color.LIGHTCYAN);

        // Start the Path
        ctx.beginPath();

        // Make different Paths
        ctx.moveTo(50, 50);
        //gc.quadraticCurveTo(30, 150, 300, 200);
        ctx.lineTo(300,150);
        ctx.fill();

        ctx.moveTo(300,150);
        ctx.lineTo(350,150);
        ctx.fill();

        ctx.moveTo(350,150);
        ctx.lineTo(350,20);
        ctx.fill();

        // End the Path
        ctx.closePath();

        // Draw the Path
        ctx.stroke();

        // Create the Pane
        Pane root = new Pane();
        // Set the Style-properties of the Pane
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        // Add the Canvas to the Pane
        root.getChildren().add(canvas);
        // Create the Scene
        Scene scene = new Scene(root);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("Drawing Paths on a Canvas");
        // Display the Stage
        stage.show();
    }
}