package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
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

    private TimeAnimator timeAnimator;
    private DateTimeBean dateTimeBean;
    private ObserverLocationBean observerLocationBean;
    private ViewingParametersBean viewingParametersBean;
    private SkyCanvasManager canvasManager;
    private ZonedDateTime saveDate;

    // The starting moment of observation, i.e. the current time, at the default time-zone of the computer
    private static final ZonedDateTime STARTING_OBSERVER_TIME = ZonedDateTime.now(ZoneOffset.systemDefault());

    // The starting geographical position of observation, i.e. the position of the EPFL campus
    private static final GeographicCoordinates STARTING_OBSERVER_POSITION = GeographicCoordinates.ofDeg(6.57, 46.52);

    // The starting accelerator, i.e. 300x
    private static final NamedTimeAccelerator STARTING_ACCELERATOR = NamedTimeAccelerator.TIMES_300;

    // The starting direction of the eyes of the observer
    private static final HorizontalCoordinates STARTING_OBSERVER_DIRECTION = HorizontalCoordinates.ofDeg(180.000000000001, 15);

    // The starting field of view of the observation (in degrees)
    private static final double STARTING_FIELD_OF_VIEW_DEG = 100;

    private static final String
            HYG_CATALOGUE_NAME = "/hygdata_v3.csv",
            AST_CATALOGUE_NAME = "/asterisms.txt",
            FONT_AWESOME_NAME = "/Font Awesome 5 Free-Solid-900.otf",
            RESET_TEXT = "\uf0e2",  // The character of the reset button's image
            PLAY_TEXT = "\uf04b",   // The character of the play/pause button's image when the animation is not running
            PAUSE_TEXT = "\uf04c";  // The character of the play/pause button's image when the animation is running

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
        try (InputStream hs = resourceStream(HYG_CATALOGUE_NAME);
             InputStream as = resourceStream(AST_CATALOGUE_NAME)) {

            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            // When the program starts, sets the observation instant to the current instant and the time-zone to the
            // one of the computer
            dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(STARTING_OBSERVER_TIME);
            saveDate = ZonedDateTime.of(STARTING_OBSERVER_TIME.toLocalDate(), STARTING_OBSERVER_TIME.toLocalTime(), STARTING_OBSERVER_TIME.getOffset());

            timeAnimator = new TimeAnimator(dateTimeBean);

            observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(STARTING_OBSERVER_POSITION);

            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(STARTING_OBSERVER_DIRECTION);
            viewingParametersBean.setFieldOfViewDeg(STARTING_FIELD_OF_VIEW_DEG);

            canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);

            Canvas canvas = canvasManager.canvas();
            // The view of the sky (the center part of the graphical interface)
            Pane skyPane = new Pane(canvas);

            // The main pane, at the root of the scene graph
            BorderPane root = new BorderPane();
            root.setTop(controlBar());
            root.setCenter(skyPane);
            root.setBottom(informationBar());

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
        HBox controlBar = new HBox(observerLocation(), vertical1, observationInstant(), vertical2, timePassage());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    /**
     * Returns the control unit of the geographical position of observation (the first sub-pane of the control bar)
     *
     * @return the control unit of the geographical position of observation
     */
    private HBox observerLocation() {
        String coordinatesFieldStyle = "-fx-pref-width: 60; -fx-alignment: baseline-right;";

        // Controls the longitude of the observation (in degrees)
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonField = new TextField();
        lonField.setStyle(coordinatesFieldStyle);

        TextFormatter<Number> lonFormatter = coordinatesTextFormatter(true);
        lonField.setTextFormatter(lonFormatter);
        lonFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());


        // Controls the latitude of the observation (in degrees)
        Label latLabel = new Label("Latitude (°) :");
        TextField latField = new TextField();
        latField.setStyle(coordinatesFieldStyle);

        TextFormatter<Number> latFormatter = coordinatesTextFormatter(false);
        latField.setTextFormatter(latFormatter);
        latFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());

        HBox observerLocation = new HBox(lonLabel, lonField, latLabel, latField);
        observerLocation.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observerLocation;
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
        datePicker.setStyle("-fx-pref-width: 120;");
        //datePicker.valueProperty().addListener(o -> dateTimeBean.setDate(datePicker.getValue()));

        // Chooses the hour of observation
        Label hour = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        // Formats the hour of observation in the hour text field.
        TextFormatter<LocalTime> hourTextFormatter = hourTextFormatter();
        hourField.setTextFormatter(hourTextFormatter);
        hourTextFormatter.setValue(dateTimeBean.getTime());

        // Updates the hour of observation according to the hour entered by the user
        //hourFormatter.valueProperty().addListener(o -> dateTimeBean.setTime(hourFormatter.getValue()));


        // Chooses the time-zone of observation
        List<String> list = new ArrayList<>(ZoneId.getAvailableZoneIds());
        ObservableList<String> zoneIds = FXCollections.observableList(list);
        SortedList<String> sortedList = new SortedList<>(zoneIds.sorted());

        List<ZoneId> zones = new ArrayList<>();
        for (String id : sortedList) {
            zones.add(ZoneId.of(id));
        }
        ObservableList<ZoneId> sortedZoneIds = FXCollections.observableList(zones);

        ComboBox<ZoneId> comboBox1 = new ComboBox<>(sortedZoneIds);


        // Dropdown menu of the time-zones
        //ComboBox<String> comboBox = new ComboBox<>(sortedList);
        comboBox1.setStyle("-fx-pref-width: 180;");
        comboBox1.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        //comboBox.setValue(dateTimeBean.getZone().getId());

        // Updates the time-zone of observation according to the time-zone selected by the user in the dropdown menu
        //comboBox.valueProperty().addListener(o -> dateTimeBean.setZone(ZoneId.of(comboBox.getValue())));

        //dateTimeBean.dateProperty().addListener((p, o, n) -> datePicker.setValue(n));
        //dateTimeBean.timeProperty().addListener((p, o, n) -> hourFormatter.setValue(n));

        dateTimeBean.dateProperty().bindBidirectional(datePicker.valueProperty());
        dateTimeBean.timeProperty().bindBidirectional(hourTextFormatter.valueProperty());

        HBox observationInstant = new HBox(date, datePicker, hour, hourField, comboBox1);
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
        ObservableList<NamedTimeAccelerator> observableAccelerators = FXCollections.observableArrayList(NamedTimeAccelerator.ALL);

        ChoiceBox<NamedTimeAccelerator> choiceBox = new ChoiceBox<>(observableAccelerators);
        choiceBox.setValue(STARTING_ACCELERATOR);
        // choiceBox.setItems(observableAccelerators);
        timeAnimator.setAccelerator(STARTING_ACCELERATOR.getAccelerator());

        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            Font fontAwesome = Font.loadFont(fontStream, 15);

            // Reset button
            Button resetButton = new Button(RESET_TEXT);
            resetButton.setFont(fontAwesome);

            // Play/Pause button
            Button playPauseButton = new Button();
            playPauseButton.setFont(fontAwesome);

            // The control unit of the passage of time
            HBox timePassage = new HBox(choiceBox, resetButton, playPauseButton);
            timePassage.setStyle("-fx-spacing: inherit;");

            choiceBox.valueProperty().addListener(
                    (o, p, n) -> timeAnimator.setAccelerator(choiceBox.getValue().getAccelerator()));

            // Controls the pressing of the play pause button
            playPauseButton.setOnMouseClicked(mouseEvent -> {
                if (timeAnimator.isRunning()) {
                    timeAnimator.stop();
                } else {
                    timeAnimator.start();
                    saveDate = dateTimeBean.getZonedDateTime();
                }
            });

            // Controls the pressing of the reset button
            resetButton.setOnMouseClicked(mouseEvent -> {
                if (timeAnimator.isRunning()) {
                    timeAnimator.stop();
                }
                dateTimeBean.setZonedDateTime(saveDate);
            });

            // Disables the graphical nodes related to the time passage parameters when an animation is running
            choiceBox.disableProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(true)
                            .otherwise(false));

            // When an animation is running, the button's image is pause, and play otherwise
            playPauseButton.textProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(PAUSE_TEXT)
                            .otherwise(PLAY_TEXT));

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

        viewingParametersBean.fieldOfViewDegProperty().addListener(o -> fovText.setText(
                String.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.getFieldOfViewDeg())));

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
     * @param isTrue Selects the coordinate (longitude or longitude) using true or false, respectively
     * @return the text formatter of a geographic coordinate
     */
    private TextFormatter<Number> coordinatesTextFormatter(boolean isTrue) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newDeg = stringConverter.fromString(newText).doubleValue();

                if (isTrue) {
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
    private TextFormatter<LocalTime> hourTextFormatter() {
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);

        return new TextFormatter<>(stringConverter);
    }
}
