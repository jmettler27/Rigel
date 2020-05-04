package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

import static javafx.beans.binding.Bindings.when;

/**
 * The main program.
 *
 * @author Mathias Bouilloud (309979)
 * @author Julien Mettler (309999)
 */
public class Main extends Application {

    private NamedTimeAccelerator namedTimeAccelerator;
    private TimeAnimator timeAnimator;
    private DateTimeBean dateTimeBean;
    private ObserverLocationBean observerLocationBean;
    private ViewingParametersBean viewingParametersBean;
    private SkyCanvasManager canvasManager;

    /**
     * Launches the graphical interface.
     *
     * @param args The command line argument passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Finds and returns a resource using its name.
     *
     * @param resourceName The name of the resource
     * @return The resource as an input stream
     */
    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream as = resourceStream("/asterisms.txt")) {

            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(when);

            namedTimeAccelerator = NamedTimeAccelerator.TIMES_300;
            timeAnimator = new TimeAnimator(dateTimeBean);
            timeAnimator.setAccelerator(namedTimeAccelerator.getAccelerator());
            // timeAnimator.start();

            observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));

            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
            viewingParametersBean.setFieldOfViewDeg(65.8);

            canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);

            Canvas canvas = canvasManager.canvas();
            // The view of the sky (the center part of the graphical interface)
            Pane skyPane = new Pane(canvas);

            // The main pane, at the root of the scene graph
            BorderPane root = new BorderPane(skyPane, controlBar(), null, informationBar(), null);

            primaryStage.setTitle("Rigel");

            // The dimensions of the canvas - width and height - are bounded to those of the sky pane (who are contained
            // in read-only properties)
            //canvas.widthProperty().bind(skyPane.widthProperty());
            //canvas.heightProperty().bind(skyPane.heightProperty());

            //canvas.widthProperty().bind(primaryStage.widthProperty());
            // canvas.heightProperty().bind(primaryStage.heightProperty());

            primaryStage.widthProperty().addListener((o, p, n) -> canvasManager.canvas().setWidth((double) n));
            primaryStage.heightProperty().addListener((o, p, n) -> canvasManager.canvas().setHeight((double) n));

            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            skyPane.requestFocus(); // Makes the canvas the receiver of the keyboard events
        }
    }

    /**
     * Returns the control bar (the top part of the graphical interface).
     *
     * @return the control bar
     */
    private HBox controlBar() throws IOException {
        // The vertical separators (children) that separate the three main children of this pane.
        Separator vertical1 = new Separator(Orientation.VERTICAL);
        Separator vertical2 = new Separator(Orientation.VERTICAL);

        // The horizontal control bar
        HBox controlBar = new HBox(observationPosition(), vertical1, observationInstant(), vertical2, timePassage());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    /**
     * Returns the control unit of the geographical position of observation (the first sub-pane of the control bar)
     *
     * @return the control unit of the geographical position of observation
     */
    private HBox observationPosition() {
        // Controls the longitude of the observation (in degrees)
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonField = new TextField();
        lonField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> lonFormatter = geographicFormatter(true);
        lonField.setTextFormatter(lonFormatter);
        lonFormatter.setValue(observerLocationBean.getLonDeg());


        // Controls the latitude of the observation (in degrees)
        Label latLabel = new Label("Latitude (°) :");
        TextField latField = new TextField();
        latField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> latFormatter = geographicFormatter(false);
        latField.setTextFormatter(latFormatter);
        latFormatter.setValue(observerLocationBean.getLatDeg());

        // Bind directional à une double property

        /*double lonDeg = lonFormatter.getValue().doubleValue();
        double latDeg = latFormatter.getValue().doubleValue();

        DoubleProperty lonDegProperty = new SimpleDoubleProperty(lonDeg);
        DoubleProperty latDegProperty = new SimpleDoubleProperty(latDeg);

        observerLocationBean.lonDegProperty().addListener((o, oV, nV) -> observerLocationBean.setLonDeg((double) nV));
        observerLocationBean.latDegProperty().addListener((o, oV, nV) -> observerLocationBean.setLatDeg((double) nV));

        observerLocationBean.lonDegProperty().bindBidirectional(lonDegProperty);
        observerLocationBean.latDegProperty().bindBidirectional(latDegProperty);*/


        // Updates the position of observation according to the longitude and latitude entered by the user.
        lonFormatter.valueProperty().addListener(o -> {
            double lonDeg = lonFormatter.getValue().doubleValue();
            double latDeg = latFormatter.getValue().doubleValue();

            GeographicCoordinates pos = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            observerLocationBean.setCoordinates(pos);
        });

        latFormatter.valueProperty().addListener(o -> {
            double lonDeg = lonFormatter.getValue().doubleValue();
            double latDeg = latFormatter.getValue().doubleValue();

            GeographicCoordinates pos = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            observerLocationBean.setCoordinates(pos);
        });

        HBox observationPosition = new HBox(lonLabel, lonField, latLabel, latField);
        observationPosition.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observationPosition;
    }

    /**
     * Returns the control unit of the instant of observation (the second sub-pane of the control bar).
     *
     * @return the control unit of the instant of observation
     */
    private HBox observationInstant() {
        // Chooses the date of observation
        Label date = new Label("Date :");
        DatePicker datePicker = new DatePicker(dateTimeBean.getDate());
        datePicker.valueProperty().addListener(o -> dateTimeBean.setDate(datePicker.getValue()));
        datePicker.setStyle("-fx-pref-width: 120;");

        /*ObjectBinding<LocalDate> dateBinding = Bindings.createObjectBinding(
                () -> {
                    dateTimeBean.setDate(datePicker.getValue());
                    return datePicker.getValue();
                }, dateTimeBean.dateProperty(), datePicker.valueProperty());*/


        // Chooses the hour of observation
        Label hour = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        // Formats the hour of observation in the hour text field.
        TextFormatter<LocalTime> hourFormatter = hourFormatter();
        hourField.setTextFormatter(hourFormatter);
        hourFormatter.setValue(dateTimeBean.getTime());

        // Updates the hour of observation according to the hour entered by the user
        hourFormatter.valueProperty().addListener(o -> dateTimeBean.setTime(hourFormatter.getValue()));


        // Chooses the time-zone of observation
        List<String> list = new ArrayList<>(ZoneId.getAvailableZoneIds());
        ObservableList<String> zoneIds = FXCollections.observableList(list);
        SortedList<String> sortedList = new SortedList<>(zoneIds.sorted());

        // Dropdown menu of the time-zones
        ComboBox<String> comboBox = new ComboBox<>(sortedList);
        comboBox.setStyle("-fx-pref-width: 180;");
        comboBox.setValue(dateTimeBean.getZone().getId());

        // Updates the time-zone of observation according to the time-zone selected by the user in the dropdown menu
        comboBox.valueProperty().addListener(o -> dateTimeBean.setZone(ZoneId.of(comboBox.getValue())));

        HBox observationInstant = new HBox(date, datePicker, hour, hourField, comboBox);
        observationInstant.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        // Disables the graphical nodes related to the choice of the instant of observation when an animation is running
        for (Node child : observationInstant.getChildren()) {
            child.disableProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(true)
                            .otherwise(false));
        }
        return observationInstant;
    }

    /**
     * Returns the control unit of the passage of time (the third sub-pane of the control bar).
     *
     * @return the control unit of the passage of time
     */
    private HBox timePassage() throws IOException {

        List<NamedTimeAccelerator> accelerators = List.of(NamedTimeAccelerator.values());
        ObservableList<NamedTimeAccelerator> observableAccelerators = FXCollections.observableArrayList(accelerators);

        ChoiceBox<NamedTimeAccelerator> choiceBox = new ChoiceBox<>(observableAccelerators);
        choiceBox.setValue(namedTimeAccelerator);
        // choiceBox.setItems(observableAccelerators);

        try (InputStream fontStream = resourceStream("/Font Awesome 5 Free-Solid-900.otf")) {
            Font fontAwesome = Font.loadFont(fontStream, 15);

            // Reset
            String resetText = "\uf0e2";
            Button resetButton = new Button(resetText);
            resetButton.setFont(fontAwesome);

            // Play
            String playText = "\uf04b";
            String pauseText = "\uf04c";
            Button playPauseButton = new Button();
            playPauseButton.setFont(fontAwesome);

            // The control unit of the passage of time
            HBox timePassage = new HBox(choiceBox, resetButton, playPauseButton);
            timePassage.setStyle("-fx-spacing: inherit;");

            // Disables the graphical nodes related to the time passage parameters when an animation is running
            choiceBox.disableProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(true)
                            .otherwise(false));

            // When an animation is running, the button's image is pause, and play otherwise
            playPauseButton.textProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(pauseText)
                            .otherwise(playText));

            return timePassage;
        }
    }

    /**
     * Returns the information bar (the bottom part of the graphical interface).
     *
     * @return the information bar
     */
    private BorderPane informationBar() {

        // Displays the field of view (in degrees) (The left zone of the information bar)
        Text fovText = new Text(
                String.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.getFieldOfViewDeg())
        );


        viewingParametersBean.fieldOfViewDegProperty().addListener(o -> {
            fovText.setText(String.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.getFieldOfViewDeg()));
        });

        /*StringExpression fovExp = Bindings.format(
                String.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.getFieldOfViewDeg()),
                viewingParametersBean.fieldOfViewDegProperty(), fovText);*/


        // Displays the object closest to the mouse cursor (The central part of the information bar)
        Text closestObjectText = new Text();
        if (canvasManager.getObjectUnderMouse() != null) {
            closestObjectText.setText(canvasManager.getObjectUnderMouse().toString());
        }
        canvasManager.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    if (n != null) {
                        closestObjectText.setText(n.toString());
                    } else {
                        closestObjectText.setText("");
                    }
                });

        // Displays the horizontal position of the mouse cursor (The right zone of the information bar)
        Text mouseHorizontalPosText = new Text();
        /*StringExpression mouseHorizontalPosExp = Bindings.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°",
                canvasManager.getMouseAzDeg(), canvasManager.getMouseAltDeg());
        mouseHorizontalPosText.setText(mouseHorizontalPosExp.getValue());
        mouseHorizontalPosExp.addListener(o -> System.out.println("ici"));*/


        canvasManager.mouseAzDegProperty().addListener(o -> {
            double azDeg1 = canvasManager.getMouseAzDeg();
            double altDeg1 = canvasManager.getMouseAltDeg();

            mouseHorizontalPosText.setText(String.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°", azDeg1, altDeg1));
        });

        canvasManager.mouseAltDegProperty().addListener(o -> {
            double azDeg1 = canvasManager.getMouseAzDeg();
            double altDeg1 = canvasManager.getMouseAltDeg();

            mouseHorizontalPosText.setText(String.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°", azDeg1, altDeg1));
        });

        BorderPane informationBar = new BorderPane(closestObjectText, null, mouseHorizontalPosText, null, fovText);
        informationBar.setStyle("-fx-padding: 4;-fx-background-color: white;");

        return informationBar;
    }

    /**
     * Returns the text formatter of a geographic coordinate (double value).
     * Formats the geographic coordinate entered in its corresponding text field such that the latter only accepts
     * values with two decimal places and within the valid intervals.
     *
     * @param b Selects the coordinate (longitude or longitude) using true or false, respectively
     * @return the text formatter of a geographic coordinate
     */
    private TextFormatter<Number> geographicFormatter(boolean b) {
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

    /**
     * Returns the text formatter of an hour (LocalTime value).
     * Formats the hour entered in its corresponding text field such that the latter only accepts valid hours.
     *
     * @return the text formatter of an hour
     */
    private TextFormatter<LocalTime> hourFormatter() {
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);

        return new TextFormatter<>(stringConverter);
    }
}
