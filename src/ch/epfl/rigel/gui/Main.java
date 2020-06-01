package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
            RESET_CHAR = "\uf0e2", PLAY_CHAR = "\uf04b", PAUSE_CHAR = "\uf04c";

    // (Bonus) The Unicode characters (UTF-16 first range) of the icons of the bonus buttons and menus
    private static final String OPTIONS_CHAR = "\uf013", ASTERISM_CHAR = "\uf005",
            SAT_CHAR = "\uf09e", NAME_CHAR = "\uf075", MINIMALIST_CHAR = "\uf06e", INFO_CHAR = "\uf05a",
            UP_CHAR = "\uf062", DOWN_CHAR = "\uf063", RIGHT_CHAR = "\uf061", LEFT_CHAR = "\uf060", ZOOM_IN = "\uf00e",
            ZOOM_OUT = "\uf010";

    /**
     * Launches the graphical interface.
     *
     * @param args
     *            The command line argument
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

            Canvas canvas = canvasManager.getCanvas();
            Pane skyPane = new Pane(canvas);  // The view of the sky (the center part of the graphical interface)

            // The dimensions of the canvas are bounded to those of the sky pane
            canvas.widthProperty().bind(skyPane.widthProperty());
            canvas.heightProperty().bind(skyPane.heightProperty());

            primaryStage.setTitle("Rigel");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // The main pane
            BorderPane root = new BorderPane(skyPane, controlBar(), null, informationBar(), null);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            skyPane.requestFocus(); // Sets the canvas as the receiver of the keyboard events
        }
    }

    /**
     * Finds and returns a resource using its name.
     *
     * @param resourceName
     *            The name of the resource
     * @return The resource as an input stream
     */
    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    /**
     * Returns the control bar (the top part of the graphical interface).
     * @return the control bar
     */
    private HBox controlBar() throws IOException {
        HBox controlBar = new HBox(
                observerLocationControl(), new Separator(Orientation.VERTICAL),
                observationTimeControl(), new Separator(Orientation.VERTICAL),
                timelapseControl(), new Separator(Orientation.VERTICAL),
                bonusInterface());

        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        return controlBar;
    }

    /**
     * Returns the information bar (bottom part of the graphical interface).
     * @return the information bar
     */
    private BorderPane informationBar() {
        BorderPane informationBar = new BorderPane();

        informationBar.setLeft(fovText());
        informationBar.setCenter(closestObjectText());
        informationBar.setRight(mousePositionText());
        informationBar.setStyle("-fx-padding: 4;-fx-background-color: white;");

        return informationBar;
    }

    /**
     * Returns the observer location control unit (first sub-pane of the control bar)
     * @return the observer location control unit
     */
    private HBox observerLocationControl() {
        // Updates the longitude of the observation (in degrees) according to the one entered by the user
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonField = coordinateField(true);

        // Updates the latitude of the observation (in degrees) according to the one entered by the user
        Label latLabel = new Label("Latitude (°) :");
        TextField latField = coordinateField(false);

        HBox observerLocationControl = new HBox(lonLabel, lonField, latLabel, latField);
        observerLocationControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observerLocationControl;
    }

    /**
     * Returns the observation time control unit (second sub-pane of the control bar).
     * @return the observation time control unit
     */
    private HBox observationTimeControl() {
        // Updates the date of observation according to the date picked by the user
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

        // The observation time control unit
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
     * Returns the timelapse control unit (third sub-pane of the control bar).
     * @return the timelapse control unit
     */
    private HBox timelapseControl() throws IOException {
        // The control unit of the timelapse
        HBox timelapseControl = new HBox(acceleratorsMenu(), resetButton(), playPauseButton());
        timelapseControl.setStyle("-fx-spacing: inherit;");

        return timelapseControl;
    }

    /**
     * Returns the text displaying the field of view (in degrees) (left part of the information bar).
     * @return the text displaying the field of view (in degrees)
     */
    private Text fovText() {
        Text fovText = new Text();

        fovText.textProperty().bind(
                Bindings.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParametersBean.fieldOfViewDegProperty()));

        return fovText;
    }

    /**
     * Returns the text displaying the object closest to the mouse cursor (central part of the information bar).
     * @return the text displaying the object closest to the mouse cursor
     */
    private Text closestObjectText() {
        Text closestObjectText = new Text();

        // Displays the closest object to the mouse cursor
        canvasManager.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    if (n != null) {
                        closestObjectText.setText(n.info());
                    } else {
                        closestObjectText.setText("");
                    }
                });

        return closestObjectText;
    }

    /**
     * Returns the text displaying the updated horizontal position of the mouse cursor (right part of the information bar).
     * @return the text displaying the updated horizontal position of the mouse cursor
     */
    private Text mousePositionText() {
        // Formats and displays the updated horizontal position of the mouse cursor (right zone of the information bar)
        Text mousePositionText = new Text();

        mousePositionText.textProperty().bind(
                Bindings.format(Locale.ROOT, "Azimuth : %.2f°, hauteur : %.2f°",
                        canvasManager.mouseAzDegBinding(), canvasManager.mouseAltDegBinding()));

        return mousePositionText;
    }

    /**
     * Returns a field containing a coordinate value.
     *
     * @param isTrue
     *            if the coordinate is the longitude, is true, and if the coordinate is the latitude, is false
     * @return a field containing a coordinate value
     */
    private TextField coordinateField(boolean isTrue) {
        TextField coordinateField = new TextField();
        coordinateField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");

        TextFormatter<Number> textFormatter = coordinatesTextFormatter(isTrue);
        coordinateField.setTextFormatter(textFormatter);

        // Binds the
        if (isTrue) {
            textFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());
        } else {
            textFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());
        }

        return coordinateField;
    }

    /**
     * Returns the dropdown menu of the time-zones of observation.
     * @return the dropdown menu of the time-zones of observation
     */
    private ComboBox<ZoneId> zoneIdMenu() {
        // Updates the time-zone of observation according to the one selected by the user in the menu
        ComboBox<ZoneId> zoneIdMenu = new ComboBox<>(OBSERVABLE_ZONE_IDS); // Dropdown menu of the time-zones
        zoneIdMenu.setStyle("-fx-pref-width: 180;");
        zoneIdMenu.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        return zoneIdMenu;
    }


    /**
     * Returns a button that resets the accelerator animation.
     * @return a button that resets the accelerator animation
     * @throws IOException in case of input/output error
     */
    private Button resetButton() throws IOException {
        Button resetButton = new Button(RESET_CHAR);
        resetButton.setFont(fontAwesome());

        // When pressed, resets the observation time to the current time
        resetButton.setOnMousePressed(
                mouseEvent -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));

        // Disables the reset button when an animation is running
        resetButton.disableProperty().bind(
                when(timeAnimator.runningProperty()).then(true).otherwise(false)
        );

        return resetButton;
    }

    /**
     * Returns a button that starts or pauses the accelerator animation.
     * @return a button that starts or pauses the accelerator animation
     * @throws IOException in case of input/output error
     */
    private Button playPauseButton() throws IOException {
        Button playPauseButton = new Button();
        playPauseButton.setFont(fontAwesome());

        // When an animation is running, the button's image is pause, and play otherwise
        playPauseButton.textProperty().bind(
                when(timeAnimator.runningProperty()).then(PAUSE_CHAR).otherwise(PLAY_CHAR));

        // Controls the pressing of the Play/Pause button
        playPauseButton.setOnMouseClicked(mouseEvent -> {
            if (timeAnimator.isRunning()) {
                timeAnimator.stop();
            } else {
                timeAnimator.start();
            }
        });

        return playPauseButton;
    }

    /**
     * Returns a menu of the time accelerators.
     * @return a menu of the time accelerators
     */
    private ChoiceBox<NamedTimeAccelerator> acceleratorsMenu() {
        ChoiceBox<NamedTimeAccelerator> acceleratorsMenu = new ChoiceBox<>();
        acceleratorsMenu.setItems(OBSERVABLE_ACCELERATORS);
        acceleratorsMenu.setValue(STARTING_ACCELERATOR);

        timeAnimator.acceleratorProperty().bind(
                Bindings.select(acceleratorsMenu.valueProperty(), "accelerator"));

        // Disables the accelerators menu when an animation is running
        acceleratorsMenu.disableProperty().bind(
                when(timeAnimator.runningProperty()).then(true).otherwise(false));

        return acceleratorsMenu;
    }


    /**
     * Returns the text formatter of a geographic coordinate (double value).
     * Formats the geographic coordinate entered in its corresponding text field such that the latter only accepts
     * values with two decimal places and within the valid intervals.
     *
     * @param isTrue
     *            Selects the coordinate (longitude or longitude) using true or false, respectively
     * @return the text formatter of a geographic coordinate
     */
    private TextFormatter<Number> coordinatesTextFormatter(boolean isTrue) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordinateDeg = stringConverter.fromString(newText).doubleValue();

                if (isTrue) {
                    return GeographicCoordinates.isValidLonDeg(newCoordinateDeg) ? change : null;
                } else {
                    return GeographicCoordinates.isValidLatDeg(newCoordinateDeg) ? change : null;
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
     * Returns the font "Font Awesome 5" of size 15.
     * @return the font "Font Awesome 5" of size 15.
     * @throws IOException in case of input/output error.
     */
    private Font fontAwesome() throws IOException {
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            return Font.loadFont(fontStream, 15);
        }
    }

    /**
     * (Bonus) Returns the graphical interface containing the bonus buttons and menus.
     * @return the interface containing the bonus buttons and menus
     * @throws IOException in case of input/output error
     */
    private HBox bonusInterface() throws IOException {
        HBox bonusInterface = new HBox(planetsMenu(), optionsMenu(), controlsInfoButton());
        bonusInterface.setStyle("-fx-spacing: inherit");

        return bonusInterface;
    }


    /**
     * (Bonus) Returns the choice box of the planets of the solar system.
     * @return the choice box of the planets of the solar system
     */
    private ChoiceBox<Planet> planetsMenu() {
        ObservableList<Planet> observablePlanets = FXCollections.observableList(canvasManager.getObservedSky().planets());

        ChoiceBox<Planet> planetsMenu = new ChoiceBox<>();
        planetsMenu.setItems(observablePlanets);

        Alert observationAlert = new Alert(Alert.AlertType.ERROR);
        observationAlert.setContentText("Objet impossible à observer !");

        planetsMenu.valueProperty().addListener(
                (o, oV, nV) -> {
                    EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(
                            dateTimeBean.getZonedDateTime(), observerLocationBean.getCoordinates());

                    HorizontalCoordinates hor = equToHor.apply(planetsMenu.getValue().equatorialPos());

                    if (0 <= hor.altDeg() && hor.altDeg() <= 90) {
                        viewingParametersBean.setCenter(hor);
                    } else {
                        observationAlert.showAndWait();
                    }
                });

        // Disables the planets menu when an animation is running
        planetsMenu.disableProperty().bind(
                when(timeAnimator.runningProperty()).then(true).otherwise(false)
        );

        return planetsMenu;
    }

    /**
     * (Bonus) Returns the viewing options menu.
     * @return the viewing options menu
     * @throws IOException in case of input/output error
     */
    private MenuButton optionsMenu() throws IOException {
        // Enables/disables the drawing of the asterisms
        CheckMenuItem asterismEnable = new CheckMenuItem("Astérismes");
        asterismEnable.selectedProperty().bindBidirectional(canvasManager.asterismEnableProperty());
        setMenuIcon(asterismEnable, ASTERISM_CHAR); // Sets the icon of the menu item

        // Enables/disables the drawing of the satellites
        CheckMenuItem satelliteEnable = new CheckMenuItem("Satellites");
        satelliteEnable.selectedProperty().bindBidirectional(canvasManager.satelliteEnableProperty());
        setMenuIcon(satelliteEnable, SAT_CHAR); // Sets the icon of the menu item

        // Enables/disables the display of the names of the brightest objects
        CheckMenuItem nameEnable = new CheckMenuItem("Noms des objets brillants");
        nameEnable.selectedProperty().bindBidirectional(canvasManager.nameEnableProperty());
        setMenuIcon(nameEnable, NAME_CHAR);

        // Enables/disables the minimalist view
        CheckMenuItem minimalistView = new CheckMenuItem("Vue minimaliste");
        minimalistView.selectedProperty().addListener(
                o -> {
                    asterismEnable.setSelected(false);
                    satelliteEnable.setSelected(false);
                    nameEnable.setSelected(false);
                }
        );
        setMenuIcon(minimalistView, MINIMALIST_CHAR);

        // Prevents the user from enabling the viewing options when the minimalist view is enabled
        asterismEnable.disableProperty().bind(
                when(minimalistView.selectedProperty()).then(true).otherwise(false)
        );

        satelliteEnable.disableProperty().bind(
                when(minimalistView.selectedProperty()).then(true).otherwise(false)
        );

        nameEnable.disableProperty().bind(
                when(minimalistView.selectedProperty()).then(true).otherwise(false)
        );

        asterismEnable.setSelected(true); // By default, the asterisms are drawn
        satelliteEnable.setSelected(false); // By default, the satellites are not drawn
        nameEnable.setSelected(true); // By default, the names of the bright objects are drawn

        // The viewing options menu
        Text optionsText = new Text(OPTIONS_CHAR);
        optionsText.setFont(fontAwesome());

        return new MenuButton("Options", optionsText, asterismEnable, satelliteEnable, nameEnable, minimalistView);
    }

    /**
     * (Bonus) Returns the button displaying the information about the controls.
     * @return the button displaying the information about the controls
     * @throws IOException in case of input/output error
     */
    private Button controlsInfoButton() throws IOException {
        Button controlsInfoButton = new Button(INFO_CHAR);
        controlsInfoButton.setFont(fontAwesome());

        Font infoFont = new Font(15); // The font for the information text of each control

        // The UP control
        Text up = new Text(UP_CHAR);
        up.setFont(fontAwesome());
        Text upInfo = new Text("Regarder vers le haut");
        upInfo.setFont(infoFont);

        // The DOWN control
        Text down = new Text(DOWN_CHAR);
        down.setFont(fontAwesome());
        Text downInfo = new Text("Regarder vers le bas");
        downInfo.setFont(infoFont);

        // The RIGHT control
        Text right = new Text(RIGHT_CHAR);
        right.setFont(fontAwesome());
        Text rightInfo = new Text("Regarder vers la droite");
        rightInfo.setFont(infoFont);

        // The LEFT control
        Text left = new Text(LEFT_CHAR);
        left.setFont(fontAwesome());
        Text leftInfo = new Text("Regarder vers la gauche");
        leftInfo.setFont(infoFont);

        // The zoom-in control
        Text zoomIn = new Text(ZOOM_IN);
        zoomIn.setFont(fontAwesome());
        Text zoomInInfo = new Text("Zoomer (scroll vers le bas / pavé tactile vers le haut)");
        zoomInInfo.setFont(infoFont);

        // The zoom-out control
        Text zoomOut = new Text(ZOOM_OUT);
        zoomOut.setFont(fontAwesome());
        Text zoomOutInfo = new Text("Dézoomer (scroll vers le haut / pavé tactile vers le bas)");
        zoomOutInfo.setFont(infoFont);

        GridPane root = new GridPane(); // Each row contains an information about the control

        // Fills the grid
        root.add(up, 1, 0);
        root.add(upInfo, 2, 0);

        root.add(down, 1, 1);
        root.add(downInfo, 2, 1);

        root.add(right, 1, 2);
        root.add(rightInfo, 2, 2);

        root.add(left, 1, 3);
        root.add(leftInfo, 2, 3);

        root.add(zoomIn, 1, 4);
        root.add(zoomInInfo, 2, 4);

        root.add(zoomOut, 1, 5);
        root.add(zoomOutInfo, 2, 5);

        root.setHgap(10); // Adds a horizontal gap between each column
        root.setVgap(10); // Adds a vertical gap between each row

        Stage stage = new Stage();
        stage.setTitle("Contrôles");
        stage.setScene(new Scene(root, 400, 175));

        // When the button is pressed, shows the keyboard and mouse controls used to observe sky
        controlsInfoButton.setOnMousePressed(mouseEvent -> stage.show());

        return controlsInfoButton;
    }

    /**
     * (Bonus) Sets the icon of the given menu item.
     *
     * @param menuItem
     *            The menu item
     * @param character
     *            The character encoding the icon of the item
     * @throws IOException in case of input/output item
     *
     */
    private void setMenuIcon(CheckMenuItem menuItem, String character) throws IOException {
        Text menuItemText = new Text(character);
        menuItemText.setFont(fontAwesome());
        menuItem.setGraphic(menuItemText);
    }
}
