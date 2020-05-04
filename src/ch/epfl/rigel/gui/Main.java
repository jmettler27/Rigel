package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class Main extends Application {

    private ObserverLocationBean observerLocationBean;
    private ViewingParametersBean viewingParametersBean;
    private SkyCanvasManager canvasManager;

    public static void main(String[] args) {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream as = resourceStream("/asterisms.txt")) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            DateTimeBean dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(when);

            observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));
            observerLocationBean.coordinatesProperty().addListener(o -> System.out.println(observerLocationBean.getCoordinates()));

            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
            viewingParametersBean.setFieldOfViewDeg(70);

            canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);

            Pane skyPane = new Pane(canvasManager.canvas());

            BorderPane mainPane = new BorderPane(skyPane, controlBar(), null, infoBar(), null);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(new Scene(mainPane));
            stage.setTitle("Rigel");
            stage.show();

            skyPane.requestFocus();
        }
    }

    private HBox controlBar() {
        HBox observationInstant = new HBox();
        HBox timeElapsing = new HBox();

        Separator vertical1 = new Separator(Orientation.VERTICAL);
        Separator vertical2 = new Separator(Orientation.VERTICAL);

        HBox controlBar = new HBox(observationPosition(), vertical1, observationInstant, vertical2, timeElapsing);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    private HBox observationPosition() {
        TextField lonField = new TextField();
        lonField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextField latField = new TextField();
        latField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> t1 = textFormatter(true);
        TextFormatter<Number> t2 = textFormatter(false);

        lonField.setTextFormatter(t1);
        latField.setTextFormatter(t2);

        lonField.setText("6.57");
        latField.setText("46.52");

        // Bind directional à une double property

        t1.valueProperty().addListener(o -> {
            double lonDeg = t1.getValue().doubleValue();
            double latDeg = t2.getValue().doubleValue();

            GeographicCoordinates pos = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            observerLocationBean.setCoordinates(pos);
        });

        t2.valueProperty().addListener(o -> {
            double lonDeg = t1.getValue().doubleValue();
            double latDeg = t2.getValue().doubleValue();

            GeographicCoordinates pos = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            observerLocationBean.setCoordinates(pos);
        });

        Label longitude = new Label("Longitude (°) :");
        Label latitude = new Label("Latitude (°) :");

        HBox observationPos = new HBox(longitude, lonField, latitude, latField);
        observationPos.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");
        return observationPos;
    }

    private TextFormatter<Number> textFormatter(boolean b) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newDeg = stringConverter.fromString(newText).doubleValue();

                if (b) {
                    return GeographicCoordinates.isValidLonDeg(newDeg)
                            ? change
                            : null;
                } else {
                    return GeographicCoordinates.isValidLatDeg(newDeg)
                            ? change
                            : null;
                }

            } catch (Exception e) {
                return null;
            }
        });

        return new TextFormatter<>(stringConverter, 0, filter);
    }

    private BorderPane infoBar() {
        Text fov = new Text("Champ de vue : " + viewingParametersBean.getFieldOfViewDeg() + "°");
        viewingParametersBean.fieldOfViewDegProperty().addListener(o -> fov.setText("Champ de vue : " + viewingParametersBean.getFieldOfViewDeg() + "°"));

        Text closestObject = new Text();
        if (canvasManager.getObjectUnderMouse() != null) {
            closestObject.setText(canvasManager.getObjectUnderMouse().toString());
        }
        canvasManager.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    if (n != null) closestObject.setText(n.toString());
                    else closestObject.setText("");
                });

        Text mouseHorizontalPos = new Text();
        /*StringExpression stringBinding = Bindings.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°",
                canvasManager.getMouseAzDeg(), canvasManager.getMouseAltDeg());
        mouseHorizontalPos.setText(stringBinding.getValue());
        stringBinding.addListener(o -> System.out.println("ici"));*/

        canvasManager.mouseAzDegProperty().addListener(o -> {
            double azDeg1 = canvasManager.getMouseAzDeg();
            double altDeg1 = canvasManager.getMouseAltDeg();

            mouseHorizontalPos.setText(String.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°", azDeg1, altDeg1));
        });

        canvasManager.mouseAltDegProperty().addListener(o -> {
            double azDeg1 = canvasManager.getMouseAzDeg();
            double altDeg1 = canvasManager.getMouseAltDeg();

            mouseHorizontalPos.setText(String.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°", azDeg1, altDeg1));
        });

        BorderPane infoBar = new BorderPane(closestObject, null, mouseHorizontalPos, null, fov);
        infoBar.setStyle("-fx-padding: 4;-fx-background-color: white;");
        return infoBar;
    }

}
