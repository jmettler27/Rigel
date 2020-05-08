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
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
    private ZonedDateTime startDate;

    // The starting moment of observation, i.e. the current time, at the default time-zone of the computer
    private static final ZonedDateTime STARTING_OBSERVATION_TIME = ZonedDateTime.now(ZoneOffset.systemDefault());

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
            RESET_CHAR = "\uf0e2",  // The character of the reset button's image
            PLAY_CHAR = "\uf04b",   // The character of the play/pause button's image when the animation is not running
            PAUSE_CHAR = "\uf04c";  // The character of the play/pause button's image when the animation is running

    /**
     * Launches the graphical interface.
     *
     * @param args The command line argument passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hs = resourceStream(HYG_CATALOGUE_NAME);
             InputStream as = resourceStream(AST_CATALOGUE_NAME)) {

            // The catalogue of the observed stars and asterisms
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            // The date/time bean
            dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(STARTING_OBSERVATION_TIME);

            // The instant of observation at the launch of the program
            startDate = ZonedDateTime.of(
                    STARTING_OBSERVATION_TIME.toLocalDate(),
                    STARTING_OBSERVATION_TIME.toLocalTime(),
                    STARTING_OBSERVATION_TIME.getOffset());

            // The current time animator
            timeAnimator = new TimeAnimator(dateTimeBean);
            timeAnimator.setAccelerator(STARTING_ACCELERATOR.getAccelerator());

            // The observer location bean
            observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(STARTING_OBSERVER_POSITION);

            // The parameters of observation
            viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(STARTING_OBSERVER_DIRECTION);
            viewingParametersBean.setFieldOfViewDeg(STARTING_FIELD_OF_VIEW_DEG);

            // The sky canvas manager
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

            // The dimensions of the canvas are bounded to those of the sky pane
            primaryStage.widthProperty().addListener((o, p, n) -> canvas.setWidth((double) n));
            primaryStage.heightProperty().addListener((o, p, n) -> canvas.setHeight((double) n));

            // canvas.widthProperty().bind(skyPane.widthProperty());
            // canvas.heightProperty().bind(skyPane.heightProperty());

            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            skyPane.requestFocus(); // Makes the canvas the receiver of the keyboard events
        }
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
     * Returns the control bar (the top part of the graphical interface).
     *
     * @return the control bar
     */
    private HBox controlBar() throws IOException {
        // The vertical separators (children) that separate the three main children of this pane.
        Separator vertical1 = new Separator(Orientation.VERTICAL);
        Separator vertical2 = new Separator(Orientation.VERTICAL);

        // The horizontal control bar
        HBox controlBar = new HBox(observerLocationControl(), vertical1, observationTimeControl(), vertical2, timelapseControl());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    /**
     * Returns the control unit of the geographical position of observation (first sub-pane of the control bar)
     *
     * @return the control unit of the geographical position of observation
     */
    private HBox observerLocationControl() {
        String coordinatesFieldStyle = "-fx-pref-width: 60; -fx-alignment: baseline-right;";

        // Updates the longitude of the observation (in degrees) according to the one entered by the user
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonField = new TextField();
        lonField.setStyle(coordinatesFieldStyle);

        TextFormatter<Number> lonTextFormatter = coordinatesTextFormatter(true);
        lonField.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());

        // Updates the latitude of the observation (in degrees) according to the one entered by the user
        Label latLabel = new Label("Latitude (°) :");
        TextField latField = new TextField();
        latField.setStyle(coordinatesFieldStyle);

        TextFormatter<Number> latTextFormatter = coordinatesTextFormatter(false);
        latField.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());

        HBox observerLocation = new HBox(lonLabel, lonField, latLabel, latField);
        observerLocation.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observerLocation;
    }

    /**
     * Returns the control unit of the instant of observation (second sub-pane of the control bar).
     *
     * @return the control unit of the instant of observation
     */
    private HBox observationTimeControl() {
        // Updates the date of observation according to the date selected by the user
        Label dateLabel = new Label("Date :");
        DatePicker datePicker = new DatePicker(dateTimeBean.getDate());
        datePicker.setStyle("-fx-pref-width: 120;");
        dateTimeBean.dateProperty().bindBidirectional(datePicker.valueProperty());

        // Updates the hour of observation according to the hour entered by the user
        Label hourLabel = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        TextFormatter<LocalTime> hourTextFormatter = hourTextFormatter();
        hourField.setTextFormatter(hourTextFormatter);
        hourTextFormatter.setValue(dateTimeBean.getTime());
        dateTimeBean.timeProperty().bindBidirectional(hourTextFormatter.valueProperty());

        // Sorts the available time-zones using their IDs
        List<ZoneId> sortedZoneIds = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .map(ZoneId::of)
                .collect(Collectors.toList());
        ObservableList<ZoneId> observableZoneIds = FXCollections.observableList(sortedZoneIds);

        // Updates the time-zone of observation according to the one selected by the user in the menu
        ComboBox<ZoneId> zoneIdMenu = new ComboBox<>(observableZoneIds); // Dropdown menu of the time-zones
        zoneIdMenu.setStyle("-fx-pref-width: 180;");
        zoneIdMenu.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        HBox observationInstant = new HBox(dateLabel, datePicker, hourLabel, hourField, zoneIdMenu);
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
     * Returns the control unit of the timelapse (third sub-pane of the control bar).
     *
     * @return the control unit of the timelapse
     */
    private HBox timelapseControl() throws IOException {
        ObservableList<NamedTimeAccelerator> observableAccelerators = FXCollections.observableList(NamedTimeAccelerator.ALL);

        ChoiceBox<NamedTimeAccelerator> acceleratorsMenu = new ChoiceBox<>(observableAccelerators);
        acceleratorsMenu.setValue(STARTING_ACCELERATOR);
        acceleratorsMenu.valueProperty().addListener(
                (o, oV, nV) -> timeAnimator.setAccelerator(nV.getAccelerator()));

        //choiceBox.setItems(observableAccelerators);
        //choiceBox.valueProperty().addListener((o, oV, nV) -> timeAnimator.setAccelerator(nV.getAccelerator()));
        //choiceBox.valueProperty().bind(Bindings.select(timeAnimator, "accelerator"));
        //timeAnimator.setAccelerator(STARTING_ACCELERATOR.getAccelerator());

        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            Font fontAwesome = Font.loadFont(fontStream, 15);

            Button resetButton = new Button(RESET_CHAR);  // Reset button
            resetButton.setFont(fontAwesome);

            Button playPauseButton = new Button(); // Play/Pause button
            playPauseButton.setFont(fontAwesome);

            // The control unit of the timelapse
            HBox timelapse = new HBox(acceleratorsMenu, resetButton, playPauseButton);
            timelapse.setStyle("-fx-spacing: inherit;");

            // Controls the pressing of the play pause button
            playPauseButton.setOnMouseClicked(mouseEvent -> {
                if (timeAnimator.isRunning()) {
                    timeAnimator.stop();
                } else {
                    timeAnimator.start();
                    startDate = dateTimeBean.getZonedDateTime();
                }
            });

            // Controls the pressing of the reset button
            resetButton.setOnMouseClicked(mouseEvent -> {
                if (timeAnimator.isRunning()) {
                    timeAnimator.stop();
                }
                dateTimeBean.setZonedDateTime(startDate);
            });

            // Disables the graphical nodes related to the timelapse parameters when an animation is running
            acceleratorsMenu.disableProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(true)
                            .otherwise(false));

            // When an animation is running, the button's image is pause, and play otherwise
            playPauseButton.textProperty().bind(
                    when(timeAnimator.runningProperty())
                            .then(PAUSE_CHAR)
                            .otherwise(PLAY_CHAR));

            return timelapse;
        }
    }

    /**
     * Returns the information bar (bottom part of the graphical interface).
     *
     * @return the information bar
     */
    private BorderPane informationBar() {
        // Formats and displays the updated field of view (in degrees) (left zone of the information bar)
        Text fovText = new Text();
        StringExpression formattedFOV = Bindings.format(Locale.ROOT, "Champ de vue : %.1f°",
                viewingParametersBean.fieldOfViewDegProperty());
        fovText.textProperty().bind(formattedFOV);

        // Displays the updated object closest to the mouse cursor (central part of the information bar)
        Text closestObjectText = new Text();
        if (canvasManager.getObjectUnderMouse() != null) {
            closestObjectText.setText(canvasManager.getObjectUnderMouse().info());
        }
        canvasManager.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    if (n != null) {
                        closestObjectText.setText(n.info());
                    } else {
                        closestObjectText.setText("");
                    }
                });

        // Formats and displays the updated horizontal position of the mouse cursor (right zone of the information bar)
        Text mousePositionText = new Text();
        StringExpression formattedMousePosition = Bindings.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°",
                canvasManager.mouseAzDegProperty(), canvasManager.mouseAltDegProperty());
        mousePositionText.textProperty().bind(formattedMousePosition);

        BorderPane informationBar = new BorderPane(closestObjectText, null, mousePositionText, null, fovText);
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
