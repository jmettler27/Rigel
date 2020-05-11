package ch.epfl.rigel.gui;

// Java Program to create a arc and specify its fill and arc type
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.DrawMode;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.shape.Arc;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.shape.ArcType;
import javafx.scene.paint.Color;


public class DrawArcs extends Application {

    // launch the application
    public void start(Stage stage)
    {

        // set title for the stage
        stage.setTitle("creating arc");

        // create a arc
        Arc arc = new Arc(100.0, 100.0, 100.0, 100.0, 142.5, 75);


        // Start angle : 90 face gauche, -90 face droite

        Group group = new Group(arc);
        arc.setTranslateX(100);
        arc.setTranslateY(50);

        // set fill for the arc
        arc.setFill(Color.BLUE);
        //arc.setType(ArcType.ROUND);
        arc.setType(ArcType.CHORD);

        Scene scene = new Scene(group, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args[])
    {
        // launch the application
        launch(args);
    }
}