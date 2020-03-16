import com.sun.javafx.geom.Rectangle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * A GUI example of meditation timer
 * started 2020-03-14
 *
 * TODO:
 *  - shorten the lengths of the sound clips (as short as possible)
 *  - _actually_ encapsulate the array returned by Sequence
 *      - this will require changing several methods
 *
 * Background image: https://www.pexels.com/@zhangkaiyv
 *
 * @author josephhaley
 * @author Sam Scott (GUI template)
 */
public class GUI extends Application {
    // VARIABLES
    //  - Window and graphics dimensions
    private final double WINDOW_W = 412;
    private final double WINDOW_H = 732;
    private final double MARGIN = 15;
    private final double PADDING = 10;
    private final double COMP_MAX_W = WINDOW_W - MARGIN * 2;
    private final double COMP_H = WINDOW_H * 0.09;

    //  - Instance variable(s)
    Sequence sequence = new Sequence();
    ObservableList soundListMain = FXCollections.observableArrayList(Sound.getSoundList());
    ObservableList soundListSecond = FXCollections.observableArrayList(Sound.getSoundList());
    String mainSound = sequence.getMainSoundName();
    String secondSound = sequence.getSecondarySoundName();

    //  - Graphical components
    //      *** background
    Canvas canvasBG;
    GraphicsContext gcBG;
    //      *** Sequence Editor components
    Label lblInterval;
    ListView<String> lvInterval;
    Button btnAddInterval;
    Button btnMoveIntervalLeft;
    Button btnMoveIntervalRight;
    Button btnDeleteInterval;
    Label lblDuration;
    TextField tfDuration;
    Button btnSetDuration;
    Label lblDurationBackground;
    //      *** Sound options components
    ComboBox cbMainSound;
    Button btnPlayMainSound;
    ComboBox cbSecondSound;
    Button btnPlaySecondSound;
    Label lblSounds;
    Label lblSoundBackground;
    //      *** Progress Indicator
    ProgressIndicator piSessionStatus;

    // METHODS
    //  - Helper methods
    /**
     * Gets an array, of the durations, of all intervals in the sequence
     * @return
     */
    public String[] getSequenceViewerItems() {
        // we subtract one from the length of the array because we don't want to display the final interval (it's just
        //  a placeholder for final sound
        String[] durations = new String[sequence.getSequenceArray().length - 1];

        for (int index = 0; index < sequence.getSequenceArray().length - 1; index++) {
            // durations[index] = "(" + (index + 1) + ")\n" + Double.toString(sequence.getSequenceArray()[index].getDuration()) + "\nmins";
            durations[index] = "Interval " + (index + 1) + ":\n" + Double.toString(sequence.getSequenceArray()[index].getDuration()) + "mins";
            System.out.print(durations[index] + " - ");
        }
        System.out.print("\n");

        return durations;
    }
    //  - Graphic methods
    /**
     * Draws (or re-draws) the "Sequence Editor" components
     */
    public void updateSequenceViewer() {
        // Populate the ListView with the sequence array
        lvInterval.setItems(FXCollections.observableArrayList(getSequenceViewerItems()));
    }
    public void updatePISessionStatus() {

    }
    /**
     * Draws all static graphical components
     */
    public void drawStaticComponents() {
        // Draws components bottom to top
        //  - draw the buttons
        final double BUTTON_W = ((COMP_MAX_W - PADDING * 2) * 0.75) / 2;
        btnAddInterval.relocate(MARGIN, WINDOW_H - MARGIN - COMP_H);
        btnDeleteInterval.relocate(MARGIN + PADDING + BUTTON_W, WINDOW_H - MARGIN - COMP_H);
        btnMoveIntervalLeft.relocate(MARGIN, WINDOW_H - MARGIN - PADDING - COMP_H * 2);
        btnMoveIntervalRight.relocate(MARGIN + PADDING + BUTTON_W, WINDOW_H - MARGIN - PADDING - COMP_H * 2);
        //  - draw the ListView
        final double LV_H = WINDOW_H - MARGIN - PADDING * 3 - COMP_H * 3;
        lvInterval.relocate(MARGIN, LV_H);
        //  - draw the duration editor
        final double DURATION_W = MARGIN + ((COMP_MAX_W - PADDING) * 0.75) + PADDING;
        final double DURATION_H = LV_H - PADDING - lblInterval.getHeight();
        lblDuration.setPrefHeight(lblInterval.getHeight());
        lblDuration.relocate(DURATION_W, DURATION_H);
        lblDurationBackground.setPrefHeight(PADDING * 2 + (COMP_H / 2) * 2 + lblDuration.getHeight());
        lblDurationBackground.relocate(DURATION_W, DURATION_H);
        btnSetDuration.relocate(DURATION_W, LV_H + COMP_H / 2 + PADDING);
        tfDuration.relocate(DURATION_W, LV_H);
        //  - draw the ListView label
        lblInterval.relocate(MARGIN, DURATION_H);
        //  - draw the Sound options
        lblSoundBackground.setPrefHeight(PADDING * 2 + (COMP_H / 2) * 2 + lblSounds.getHeight());
        lblSoundBackground.relocate(MARGIN, DURATION_H - (COMP_H / 2) * 2 - PADDING * 3 - lblSounds.getHeight());
        cbSecondSound.relocate(MARGIN, DURATION_H - COMP_H / 2 - PADDING * 1);
        btnPlaySecondSound.relocate(MARGIN + BUTTON_W + PADDING, DURATION_H - COMP_H / 2 - PADDING * 1);
        cbMainSound.relocate(MARGIN, DURATION_H - (COMP_H / 2) * 2 - PADDING * 2);
        btnPlayMainSound.relocate(MARGIN + BUTTON_W + PADDING, DURATION_H - (COMP_H / 2) * 2 - PADDING * 2);
        lblSounds.relocate(MARGIN, DURATION_H - (COMP_H / 2) * 2 - PADDING * 3 - lblSounds.getHeight());
        //  - draw the progress indicator
        piSessionStatus.relocate(MARGIN, MARGIN);
    }

    //  - Handler methods
    //      *** ...for the Sound components
    private void playSoundHandler(MouseEvent e) {
        Object buttonPressed = e.getSource();

        if (buttonPressed == btnPlayMainSound) { // Play the Main sound
            // Find the index (in the ListView) of the sound chosen by the user
            int soundIndex = cbMainSound.getSelectionModel().getSelectedIndex();
            // Find the name of the sound
            String soundName = Sound.getSoundList()[soundIndex];
            // Set the Sequence object's main sound
            sequence.setMainSound(soundName);

            // Play the sound
            sequence.playMainSound();
        } else if (buttonPressed == btnPlaySecondSound) {
            // Find the index (in the ListView) of the sound chosen by the user
            int soundIndex = cbSecondSound.getSelectionModel().getSelectedIndex();
            // Find the name of the sound
            String soundName = Sound.getSoundList()[soundIndex];
            // Set the Sequence object's main sound
            sequence.setSecondarySound(soundName);

            // Play the sound
            sequence.playSecondarySound();
        }
    }
    //      *** ...for the ListView and Duration editing components
    /**
     * Puts the duration of a selected Interval into the duration TextField
     * @param e MouseEvent
     */
    private void printDurationHandler(MouseEvent e) {
        int index = lvInterval.getSelectionModel().getSelectedIndex();
        double duration = sequence.getSequenceArray()[index].getDuration();

        tfDuration.setText(Double.toString(duration));
        updateSequenceViewer();
    }
    /**
     * Edits an interval's duration
     * @param e ActionEvent
     */
    private void editDurationHandler(ActionEvent e) {
        double newDuration = 0;
        String text = tfDuration.getText();
        try {
            // Convert the String text to a double
            newDuration = Double.parseDouble(text);

            // If the new duration is within the elements, then change the Interval's duration
            if (newDuration >= 0 && newDuration <= 120) {
                int index = lvInterval.getSelectionModel().getSelectedIndex();
                sequence.getSequenceArray()[index].setDuration(newDuration);
                updateSequenceViewer();
            } else { // Throw exception because the duration is outside the limits
                throw new Exception();
            }
            System.out.println("here 1");
        } catch (Exception exception) {
            // Just re-set the TextField to the Interval's original duration
            int index = lvInterval.getSelectionModel().getSelectedIndex();
            double duration = sequence.getSequenceArray()[index].getDuration();
            System.out.println("here 2");
            tfDuration.setText(Double.toString(duration));
            updateSequenceViewer();
        }
    }
    //      *** ...for the buttons
    /**
     * Adds an Interval object to the end of the Sequence's Interval Array
     * @param e ActionEvent
     */
    private void addIntervalHandler(ActionEvent e) {
        // Default duration = 10 minutes
        sequence.addInterval(10);
        updateSequenceViewer();
    }
    /**
     * Deletes an Interval object from the Sequence's Interval Array
     * @param e ActionEvent
     */
    private void deleteIntervalHandler(ActionEvent e) {
        sequence.deleteInterval(lvInterval.getSelectionModel().getSelectedIndex());
        updateSequenceViewer();
    }
    /**
     * Moves an Interval object to the left (swaps it with the Interval directly to its left)
     * @param e ActionEvent
     */
    private void moveIntervalLeftHandler(ActionEvent e) {
        sequence.moveIntervalLeft(lvInterval.getSelectionModel().getSelectedIndex());
        updateSequenceViewer();
    }
    /**
     * Moves an Interval object to the right (swaps it with the Interval directly to its right)
     * @param e ActionEvent
     */
    private void moveIntervalRightHandler(ActionEvent e) {
        sequence.moveIntervalRight(lvInterval.getSelectionModel().getSelectedIndex());
        updateSequenceViewer();
    }

    // TODO: Instance Variables for View Components and Model
    // TODO: Private Event Handlers and Helper Methods
    /**
     * This is where you create your components and the model and add event
     * handlers.
     *
     * @param stage The main stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, WINDOW_W, WINDOW_H); // size of the window
        scene.getStylesheets().add("resources/css/defaultStyles.css");
        stage.setTitle("Meditation and Prayer Timer"); // set the window title here
        stage.setScene(scene);

        // TODO: 2. Create the GUI components
        // CREATE the background
        canvasBG = new Canvas(WINDOW_W, WINDOW_H);
        gcBG = canvasBG.getGraphicsContext2D();
        // CREATE the Sequence Editor objects
        //  - ListView of sequence intervals
        lblInterval = new Label("SESSION INTERVALS");
        lvInterval = new ListView<String>();
        //  - Duration editor components
        lblDurationBackground = new Label("");
        lblDuration = new Label("INTERVAL\nDURATION");
        tfDuration = new TextField();
        btnSetDuration = new Button("SET");
        //  - Sequence Editor buttons
        btnAddInterval = new Button("ADD");
        btnMoveIntervalLeft = new Button ("\u2190 MOVE");
        btnMoveIntervalRight = new Button ("MOVE \u2192");
        btnDeleteInterval = new Button ("DELETE");
        // CREATE Sound options
        lblSoundBackground = new Label("");
        cbMainSound = new ComboBox(soundListMain);
        btnPlayMainSound = new Button ("\u25b6"); // play symbol
        cbSecondSound = new ComboBox(soundListSecond);
        btnPlaySecondSound = new Button ("\u25b6"); // play symbol
        lblSounds = new Label ("INTERVAL SOUNDS");
        // CREATE Progress indicator
        piSessionStatus = new ProgressIndicator();

        // TODO: 3. Add components to the root
        //  - Background components
        root.getChildren().addAll(canvasBG);
        //  - Sequence Editor components
        root.getChildren().addAll(lvInterval, lblInterval,
                btnAddInterval,btnMoveIntervalLeft, btnMoveIntervalRight, btnDeleteInterval,
                lblDurationBackground, lblDuration, tfDuration, btnSetDuration);
        //  - Sound options
        root.getChildren().addAll(lblSoundBackground,
                cbMainSound, btnPlayMainSound, cbSecondSound, btnPlaySecondSound, lblSounds);
        //  - Progress indicator
        root.getChildren().addAll(piSessionStatus);

        // TODO: 4. Configure the components (colors, fonts, size, location)
        // CONFIGURE the background
        gcBG.drawImage(new Image("resources/images/background.png"), 0, 0, WINDOW_W, WINDOW_H);
        // CONFIGURE the Sequence Editor objects
        //  - ListView of sequence intervals
        lblInterval.setMaxWidth((COMP_MAX_W - PADDING) * 0.75);
        lblInterval.setPrefWidth((COMP_MAX_W - PADDING) * 0.75);
        lblInterval.setAlignment(Pos.CENTER);
        lvInterval.setPrefWidth((COMP_MAX_W - PADDING) * 0.75);
        lvInterval.setPrefHeight(COMP_H + PADDING);
        lvInterval.setOrientation(Orientation.HORIZONTAL);
        //  - Duration editor components
        lblDurationBackground.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        lblDurationBackground.getStyleClass().add("label-background");
        lblDuration.getStyleClass().add("secondary-label");
        lblDuration.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        lblDuration.setAlignment(Pos.BOTTOM_CENTER);
        tfDuration.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        tfDuration.setPrefHeight(COMP_H / 2);
        tfDuration.setText("slct an ntrvl");
        btnSetDuration.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        btnSetDuration.setPrefHeight(COMP_H / 2);
        btnSetDuration.getStyleClass().add("button-set");
        //  - Sequence Editor buttons
        btnAddInterval.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H);
        btnAddInterval.getStyleClass().add("button-add");
        btnAddInterval.setAlignment(Pos.TOP_RIGHT);
        btnDeleteInterval.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H);
        btnDeleteInterval.getStyleClass().add("button-delete");
        btnDeleteInterval.setAlignment(Pos.TOP_LEFT);
        btnMoveIntervalLeft.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H);
        btnMoveIntervalLeft.getStyleClass().add("button-move-l");
        btnMoveIntervalLeft.setAlignment(Pos.BOTTOM_RIGHT);
        btnMoveIntervalRight.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H);
        btnMoveIntervalRight.getStyleClass().add("button-move-r");
        btnMoveIntervalRight.setAlignment(Pos.BOTTOM_LEFT);
        // CONFIGURE Sound options
        final double SOUND_MAX_W = ((COMP_MAX_W - PADDING * 2) * 0.75) / 2 + PADDING + COMP_H / 2;
        lblSoundBackground.setPrefWidth(SOUND_MAX_W);
        lblSoundBackground.getStyleClass().add("label-background");
        cbMainSound.setVisibleRowCount(4);
        cbMainSound.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H / 2);
        cbSecondSound.setVisibleRowCount(4);
        cbSecondSound.setPrefSize(((COMP_MAX_W - PADDING * 2) * 0.75) / 2, COMP_H / 2);
        cbSecondSound.getSelectionModel().select(Sound.getSoundIndex(secondSound));
        cbMainSound.getSelectionModel().select(Sound.getSoundIndex(mainSound));
        btnPlayMainSound.setPrefSize(COMP_H / 2, COMP_H / 2);
        btnPlayMainSound.getStyleClass().add("button-play");
        btnPlaySecondSound.setPrefSize(COMP_H / 2, COMP_H / 2);
        btnPlaySecondSound.getStyleClass().add("button-play");
        lblSounds.setPrefSize(SOUND_MAX_W, COMP_H / 2);
        lblSounds.getStyleClass().add("secondary-label");
        lblSounds.setAlignment(Pos.BOTTOM_CENTER);
        // CONFIGURE Progress indicator
        piSessionStatus.setProgress(0.1);
        piSessionStatus.setPrefSize(COMP_MAX_W, COMP_MAX_W * 0.75);

        // TODO: 5. Add Event Handlers and do final setup
        //  - Sound handlers
        btnPlayMainSound.setOnMouseClicked(this::playSoundHandler);
        btnPlaySecondSound.setOnMouseClicked(this::playSoundHandler);
        //  - Sequence Editor ListView and Duration editing handlers
        lvInterval.setOnMouseClicked(this::printDurationHandler);
        btnSetDuration.setOnAction(this::editDurationHandler);
        //  - Sequence Editor button handlers
        btnAddInterval.setOnAction(this::addIntervalHandler);
        btnDeleteInterval.setOnAction(this::deleteIntervalHandler);
        btnMoveIntervalLeft.setOnAction(this::moveIntervalLeftHandler);
        btnMoveIntervalRight.setOnAction(this::moveIntervalRightHandler);

        // TODO: 6. Show the stage
        stage.show();

        // Show this after the stage so that we can get the width of components (for displaying things correctly)
        Thread.sleep(1000);
        drawStaticComponents();
        updateSequenceViewer();
        updatePISessionStatus();
    }

    /**
     * Make no changes here.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}
