package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.function.UnaryOperator;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    private ObserverLocationBean observerLocationBean;
    private ViewingParametersBean viewingParametersBean;

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

            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
            viewingParametersBean.setFieldOfViewDeg(70);

            SkyCanvasManager canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);

            canvasManager.objectUnderMouseProperty().addListener(
                    (p, o, n) -> {
                        if (n != null) System.out.println(n);
                    });

            Canvas sky = canvasManager.canvas();

            Text txt1 = new Text("Test 1");
            BorderPane mainPane = new BorderPane(sky, controlBar(), null, txt1, null);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(new Scene(mainPane));
            stage.setTitle("Rigel");
            stage.show();
        }
    }

    private HBox controlBar() {


        HBox child2 = new HBox();
        HBox child3 = new HBox();

        Separator vertical1 = new Separator();
        Separator vertical2 = new Separator();

        HBox controlBar = new HBox(observationPos(), vertical1, child2, vertical2, child3);

        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    private HBox observationPos() {
        TextField lonField = new TextField("6.57");
        lonField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextField latField = new TextField("46.52");
        latField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> t1 = textFormatter(true);
        TextFormatter<Number> t2 = textFormatter(false);

        lonField.setTextFormatter(t1);
        latField.setTextFormatter(t2);

        ObjectBinding<GeographicCoordinates> latBinding = Bindings.createObjectBinding(
                () -> {
                    double lonDeg = (double) (t1.getValue());
                    double latDeg = (double) (t2.getValue());

                    GeographicCoordinates pos = GeographicCoordinates.ofDeg(lonDeg, latDeg);
                    observerLocationBean.setCoordinates(pos);
                    return  pos;
                }
        , observerLocationBean.coordinatesProperty(), t1.valueProperty(), t2.valueProperty());
        Label longitude = new Label("Longitude (°) :");
        Label latitude = new Label("Latitude (°) :");


        HBox observationPos = new HBox(longitude, lonField, latitude, latField);
        observationPos.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");
        return observationPos;
    }

    private TextFormatter<Number> textFormatter(boolean b) {
        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newDeg =
                        stringConverter.fromString(newText).doubleValue();

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

}
