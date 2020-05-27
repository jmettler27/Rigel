package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
    private static final HorizontalCoordinates STARTING_OBSERVER_DIRECTION = HorizontalCoordinates.ofDeg(
            180.000000000001, 15);

    // The starting field of view of the observation (in degrees)
    private static final double STARTING_FIELD_OF_VIEW_DEG = 100;

    // The available time-zones sorted by their IDs
    private static final List<ZoneId> SORTED_ZONE_IDS = ZoneId.getAvailableZoneIds()
            .stream()
            .sorted() // Sorts the strings alphabetically
            .map(ZoneId::of) // Creates a zone ID for each string
            .collect(Collectors.toList());

    // The observable lists of the available zone IDs and time accelerators
    private static final ObservableList<ZoneId> OBSERVABLE_ZONE_IDS = FXCollections.observableList(SORTED_ZONE_IDS);
    private static final ObservableList<NamedTimeAccelerator> OBSERVABLE_ACCELERATORS =
            FXCollections.observableList(NamedTimeAccelerator.ALL);

    private static final String
            HYG_CATALOGUE_NAME = "/hygdata_v3.csv",
            AST_CATALOGUE_NAME = "/asterisms.txt",
            SAT_CATALOGUE_NAME = "/active_satellites.csv",
            FONT_AWESOME_NAME = "/Font Awesome 5 Free-Solid-900.otf",
            RESET_CHAR = "\uf0e2",  // The character of the reset button's image
            PLAY_CHAR = "\uf04b",   // The character of the play/pause button's image when the animation is not running
            PAUSE_CHAR = "\uf04c",  // The character of the play/pause button's image when the animation is running
            ASTERISM_CHAR = "\uf005", // The character of the asterism enabling's image
            SAT_CHAR = "\uf09e", // The character of the satellite enabling's image
            OPTIONS_CHAR = "\uf013",
            CAMERA_CHAR = "\uf083"; // The character of the options menu

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
             InputStream as = resourceStream(AST_CATALOGUE_NAME);
             InputStream sat = resourceStream(SAT_CATALOGUE_NAME)) {

            // The catalogue of the observed stars and asterisms
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            // The catalogue of the observed satellites
            SatelliteCatalogue satCatalogue = new SatelliteCatalogue.Builder()
                    .loadFrom(sat, SatelliteDatabaseLoader.INSTANCE)
                    .build();

            // The date/time bean
            dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(STARTING_OBSERVATION_TIME);

            // The instant of observation at the launch of the program
            startDate = ZonedDateTime.of(STARTING_OBSERVATION_TIME.toLocalDate(), STARTING_OBSERVATION_TIME.toLocalTime(),
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
            canvasManager = new SkyCanvasManager(catalogue, satCatalogue, dateTimeBean, observerLocationBean,
                    viewingParametersBean);

            Canvas canvas = canvasManager.canvas();
            Pane skyPane = new Pane(canvas);  // The view of the sky (the center part of the graphical interface)

            // The dimensions of the canvas are bounded to those of the sky pane
            canvas.widthProperty().bind(skyPane.widthProperty());
            canvas.heightProperty().bind(skyPane.heightProperty());
            //primaryStage.widthProperty().addListener((o, p, n) -> canvas.setWidth((double) n));
            //primaryStage.heightProperty().addListener((o, p, n) -> canvas.setHeight((double) n));

            primaryStage.setTitle("Rigel");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // The main pane
            BorderPane root = new BorderPane(skyPane, controlBar(), null, informationBar(), null);
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
        // The horizontal control bar
        HBox controlBar = new HBox(observerLocationControl(), new Separator(Orientation.VERTICAL),
                observationTimeControl(), new Separator(Orientation.VERTICAL),
                timelapseControl(), new Separator(Orientation.VERTICAL), bonusInterface());

        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    /**
     * Returns the control unit of the geographical position of observation (first sub-pane of the control bar)
     *
     * @return the control unit of the geographical position of observation
     */
    private HBox observerLocationControl() {
        // Updates the longitude of the observation (in degrees) according to the one entered by the user
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonField = new TextField();
        lonField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> lonTextFormatter = coordinatesTextFormatter(true);
        lonField.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());

        // Updates the latitude of the observation (in degrees) according to the one entered by the user
        Label latLabel = new Label("Latitude (°) :");
        TextField latField = new TextField();
        latField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> latTextFormatter = coordinatesTextFormatter(false);
        latField.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());

        HBox observerLocationControl = new HBox(lonLabel, lonField, latLabel, latField);
        observerLocationControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observerLocationControl;
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


        HBox observationTimeControl = new HBox(dateLabel, datePicker, hourLabel, hourField, zoneIdMenu());
        observationTimeControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        // Disables the graphical nodes related to the choice of the instant of observation when an animation is running
        for (Node child : observationTimeControl.getChildren()) {
            child.disableProperty().bind(
                    when(timeAnimator.runningProperty()).then(true).otherwise(false));
        }

        return observationTimeControl;
    }

    /**
     * Returns a dropdown menu of the time-zones of observation.
     *
     * @return a dropdown menu of the time-zones of observation
     */
    private ComboBox<ZoneId> zoneIdMenu() {
        // Updates the time-zone of observation according to the one selected by the user in the menu
        ComboBox<ZoneId> zoneIdMenu = new ComboBox<>(OBSERVABLE_ZONE_IDS); // Dropdown menu of the time-zones
        zoneIdMenu.setStyle("-fx-pref-width: 180;");
        zoneIdMenu.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        return zoneIdMenu;
    }

    /**
     * Returns the control unit of the timelapse (third sub-pane of the control bar).
     *
     * @return the control unit of the timelapse
     */
    private HBox timelapseControl() throws IOException {
        // The control unit of the timelapse
        HBox timelapseControl = new HBox(acceleratorsMenu(), resetButton(), playPauseButton());
        timelapseControl.setStyle("-fx-spacing: inherit;");

        return timelapseControl;
    }

    /**
     * Returns a button that resets the accelerator animation.
     *
     * @return a button that resets the accelerator animation
     * @throws IOException in case of input/output error
     */
    private Button resetButton() throws IOException {
        Button resetButton = new Button(RESET_CHAR);
        resetButton.setFont(fontAwesome());

        // Controls the pressing of the reset button
        resetButton.setOnMouseClicked(mouseEvent -> {
            if (timeAnimator.isRunning()) {
                timeAnimator.stop();
            }
            dateTimeBean.setZonedDateTime(ZonedDateTime.now(ZoneOffset.systemDefault()));
        });

        return resetButton;
    }

    /**
     * Returns a button that starts or pauses the accelerator animation.
     *
     * @return a button that starts or pauses the accelerator animation
     * @throws IOException in case of input/output error
     */
    private Button playPauseButton() throws IOException {
        Button playPauseButton = new Button();
        playPauseButton.setFont(fontAwesome());

        // Controls the pressing of the Play/Pause button
        playPauseButton.setOnMouseClicked(mouseEvent -> {
            if (timeAnimator.isRunning()) {
                timeAnimator.stop();
            } else {
                timeAnimator.start();
                startDate = dateTimeBean.getZonedDateTime();
            }
        });

        // When an animation is running, the button's image is pause, and play otherwise
        playPauseButton.textProperty().bind(
                when(timeAnimator.runningProperty()).then(PAUSE_CHAR).otherwise(PLAY_CHAR));

        return playPauseButton;
    }

    /**
     * Returns a menu of the time accelerators.
     *
     * @return a menu of the time accelerators
     */
    private ChoiceBox<NamedTimeAccelerator> acceleratorsMenu() {
        // Accelerators menu
        ChoiceBox<NamedTimeAccelerator> acceleratorsMenu = new ChoiceBox<>();
        acceleratorsMenu.setItems(OBSERVABLE_ACCELERATORS);
        acceleratorsMenu.setValue(STARTING_ACCELERATOR);
        timeAnimator.acceleratorProperty().bind(
                Bindings.select(acceleratorsMenu.valueProperty(), "accelerator"));

        // Disables the graphical nodes related to the timelapse parameters when an animation is running
        acceleratorsMenu.disableProperty().bind(
                when(timeAnimator.runningProperty()).then(true).otherwise(false));

        return acceleratorsMenu;
    }

    /**
     * Returns the information bar (bottom part of the graphical interface).
     *
     * @return the information bar
     */
    private BorderPane informationBar() {
        // Formats and displays the updated field of view (in degrees) (left zone of the information bar)
        Text fovText = new Text();
        fovText.textProperty().bind(
                Bindings.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.fieldOfViewDegProperty()));

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
        mousePositionText.textProperty().bind(
                Bindings.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°",
                        canvasManager.mouseAzDegProperty(), canvasManager.mouseAltDegProperty()));

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
                    return GeographicCoordinates.isValidLonDeg(newDeg) ? change : null;
                } else {
                    return GeographicCoordinates.isValidLatDeg(newDeg) ? change : null;
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

    /**
     * Additional method.
     * Returns the font "Font Awesome 5" of size 15.
     *
     * @return the font "Font Awesome 5" of size 15.
     * @throws IOException in case of input/output error.
     */
    private Font fontAwesome() throws IOException {
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            return Font.loadFont(fontStream, 15);
        }
    }

    /**
     * Additional method (bonus).
     * Returns the interface containing the bonuses.
     *
     * @return the interface containing the bonuses
     * @throws IOException in case of input/output error
     */
    private HBox bonusInterface() throws IOException {
        HBox bonusButtons = new HBox(optionsMenu(), photoButton(), planetsMenu());
        bonusButtons.setStyle("-fx-spacing: inherit");

        return bonusButtons;
    }

    /**
     * Additional method (bonus).
     * Returns a viewing options menu.
     *
     * @return the viewing options menu
     * @throws IOException in case of input/output error
     */
    private MenuButton optionsMenu() throws IOException {
        // Enables/disables the drawing of the asterisms
        CheckMenuItem asterismEnable = new CheckMenuItem("Astérismes");
        asterismEnable.selectedProperty().bindBidirectional(canvasManager.asterismEnableProperty());
        asterismEnable.setSelected(true);
        setMenuIcon(asterismEnable, ASTERISM_CHAR); // Sets the icon of the menu item

        // Enables/disables the drawing of the satellites
        CheckMenuItem satelliteEnable = new CheckMenuItem("Satellites");
        satelliteEnable.selectedProperty().bindBidirectional(canvasManager.satelliteEnableProperty());
        satelliteEnable.setSelected(false);
        setMenuIcon(satelliteEnable, SAT_CHAR); // Sets the icon of the menu item

        // The menu button of the drawing options
        Text optionsText = new Text(OPTIONS_CHAR);
        optionsText.setFont(fontAwesome());

        return new MenuButton("Options", optionsText, asterismEnable, satelliteEnable);
    }

    /**
     * Additional method (bonus).
     * Returns a button which takes a photography of the sky when pressed.
     *
     * @return the photo button
     * @throws IOException in case of input/output error
     */
    private Button photoButton() throws IOException {
        Button photoButton = new Button(CAMERA_CHAR);
        photoButton.setFont(fontAwesome());

        photoButton.setOnMousePressed(event -> {
            String fileName = String.format("sky lon=%.2f lat=%.2f %s ",
                    observerLocationBean.getLonDeg(), observerLocationBean.getLatDeg(),
                    dateToString(dateTimeBean.getZonedDateTime()) + ".png");

            WritableImage fxImage = canvasManager.canvas().snapshot(null, null);

            BufferedImage swingImage = SwingFXUtils.fromFXImage(fxImage, null);
            try {
                ImageIO.write(swingImage, "png", new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return photoButton;
    }

    /**
     * Additional method (bonus).
     * Returns a choice box of the planets of the solar system.
     *
     * @return the choice box of the planets of the solar system
     */
    private ChoiceBox<Planet> planetsMenu() {
        ObservableList<Planet> observablePlanets = FXCollections.observableList(canvasManager.observedSky().planets());
        ChoiceBox<Planet> planetsMenu = new ChoiceBox<>();
        planetsMenu.setItems(observablePlanets);

        planetsMenu.valueProperty().addListener(
                (o, oV, nV) -> {
                    EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(
                            dateTimeBean.getZonedDateTime(), observerLocationBean.getCoordinates());
                    HorizontalCoordinates hor = equToHor.apply(planetsMenu.getValue().equatorialPos());

                    if (hor.altDeg() >= 0 && hor.altDeg() <= 90) {
                        viewingParametersBean.setCenter(hor);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setContentText("Objet impossible à observer !");
                        alert.showAndWait();
                    }
                });

        return planetsMenu;
    }

    /**
     * Additional method.
     * Sets the icon of the given item to the given encoding character.
     *
     * @param menuItem  The menu item
     * @param character The character encoding the icon of the item
     * @throws IOException in case of input/output item
     */
    private void setMenuIcon(CheckMenuItem menuItem, String character) throws IOException {
        Text menuItemText = new Text(character);
        menuItemText.setFont(fontAwesome());
        menuItem.setGraphic(menuItemText);
    }

    /**
     * Additional method (bonus).
     * Returns the String representation of the date of observation.
     *
     * @param when The date of observation
     * @return the String representation of the date of observation
     */
    private String dateToString(ZonedDateTime when) {
        return when.getYear() + "-" + when.getMonthValue() + "-" + when.getDayOfMonth() + " " + when.getHour() + "h"
                + when.getMinute();
    }
}
