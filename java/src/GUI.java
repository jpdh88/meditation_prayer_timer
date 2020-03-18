import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.Date;

/**
 * A GUI example of meditation xtimer
 * started 2020-03-14
 *
 * TODO:
 *  - shorten the lengths of the sound clips (as short as possible)
 *  - _actually_ encapsulate the array returned by Sequence
 *      - this will require changing several methods
 *  - create variable in Sequence for "current Profile" => integrate this
 *
 * Style1 image: https://www.pexels.com/@zhangkaiyv
 * Style2 image: https://www.pexels.com/@punchbrandstock
 *
 * @author josephhaley
 * @author Sam Scott (GUI template)
 */
public class GUI extends Application {
    // VARIABLES
    //  - Window and graphics dimensions
    /** The GUI style that will be used **/
    private final String STYLE = "style1"; // style1, style2, or style3
    /** Width of the window **/
    private final double WINDOW_W = 412;
    /** Height of the window ***/
    private final double WINDOW_H = 732;
    /** Default margin for arranging graphical components **/
    private final double MARGIN = 15;
    /** Default padding for arranging graphical components **/
    private final double PADDING = 10;
    /** Max width of a graphical component (many graphical components are relative to this) **/
    private final double COMP_MAX_W = WINDOW_W - MARGIN * 2;
    /** The standard height of a graphical component (many graphical components are relative to this) **/
    private final double COMP_H = WINDOW_H * 0.09;
    //  - Timer variables
    /** The total duration of all the intervals in the Sequence object **/
    private double totalDuration;
    /** The time the timer was started **/
    private long timerStartTime;
    /** The time the timer was paused **/
    private long timerPausedTime;
    /** Whether the timer should keep running or not **/
    private boolean keepTimerRunning;
    /** Whether the timer is paused or not **/
    private boolean timerIsPaused = false;
    /** Array of sound tasks and when to play them **/
    private double[][] soundSchedule;
    /** Keeps track of what sound we're supposed to play in the timer **/
    private int soundIndex = 0;

    //  - Instance variable(s)
    /** The Sequence object to keep track of the intervals **/
    Sequence sequence = new Sequence();
    /** ObservableList of Profiles (for the combobox) **/
    ObservableList profilesList = FXCollections.observableArrayList(getProfiles());
    /** ObservableList of Main sounds (for the combobox) **/
    ObservableList soundListMain = FXCollections.observableArrayList(Sound.getSoundList());
    /** ObservableList of Secondary sounds (for the combobox) **/
    ObservableList soundListSecond = FXCollections.observableArrayList(Sound.getSoundList());
    /** The main sound to be used (can be changed by user) **/
    String mainSound = sequence.getMainSoundName();
    /** The secondary sound to be used (can be changed by user) **/
    String secondSound = sequence.getSecondarySoundName();

    //  - Graphical components
    //      *** background
    Canvas canvasBG;
    GraphicsContext gcBG;
    //      *** Sequence Editor components
    Label lblInterval;
    ListView<String> lvIntervals;
    Button btnAddInterval;
    Button btnMoveIntervalLeft;
    Button btnMoveIntervalRight;
    Button btnDeleteInterval;
    TextField tfDuration;
    Button btnSetDuration;
    //      *** Profile components
    ComboBox cbProfiles;
    Button btnLoadProfile;
    Button btnSaveProfile;
    Button btnDeleteProfile;
    Button btnSetDefaultProfile;
    Label lblProfiles;
    //      *** Sound options components
    ComboBox cbMainSound;
    Button btnPlayMainSound;
    ComboBox cbSecondSound;
    Button btnPlaySecondSound;
    Label lblPrimSound;
    Label lblSecondSound;
    //      *** Progress Indicator
    ProgressIndicator piSessionStatus;
    //      *** Control Timers
    Button btnStartTimer;
    Button btnPauseTimer; // maybe
    Button btnStopTimer;
    Button btnContinueTimer;

    // METHODS
    //  - Timer methods
    /**
     * Starts the timer, whether it has come from pause or not
     */
    public void startTimer() {
        // Was timer paused?
        if (timerIsPaused) { // YES: then we can't over-write existing variables
            // Timer is no longer paused
            timerIsPaused = false;

            // Calculate the new start time (to take into account the elapsed time)
            long elapsedTime = timerPausedTime - timerStartTime;
            timerStartTime = System.currentTimeMillis() - elapsedTime;

            // Everything else should take up where it left off
        } else { // NO: reset all variables
            //  - set the start time
            timerStartTime = System.currentTimeMillis();
            //  - get the session's total duration
            totalDuration = sequence.getTotalDuration() * 1000.0 * 60.0; // convert minutes to milliseconds: * 1000 milliseconds * 60 seconds
            //  - set the soundIndex to 0
            soundIndex = 0;

            //  - We want to keep track of when to play sounds and whether a sound has been played or not
            //  (soundSchedule[x][y] => x is the time when the sound should play, y is whether a sound has played or not
            soundSchedule = new double[sequence.getSequenceArray().length][2];
            double time = 0;
            //      - rest of indexes:
            for (int intervalIndex = 0; intervalIndex < sequence.getSequenceArray().length; intervalIndex++) {
                time += sequence.getSequenceArray()[intervalIndex].getDuration() * 1000.0 * 60.0; // convert to milliseconds

                soundSchedule[intervalIndex][0] = time;
                soundSchedule[intervalIndex][1] = 0;
            }
        }

        // The timer is now running
        keepTimerRunning = true;

        // Update the display
        timerIsRunning();

        // Timer thread
        Thread timerThread = new Thread() {
            public void run() {
                boolean hasUpdatedProgress = false;
                double lastProgressUpdate = 0;

                // Always start of the timer with the main sound
                String musicFile2 = Sound.getPathFromSoundList(sequence.getMainSoundName());
                Media sound2 = new Media(new File(musicFile2).toURI().toString());
                MediaPlayer mediaPlayer2 = new MediaPlayer(sound2);
                new Thread() {
                    public void run() {
                        mediaPlayer2.play();
                    }
                }.start();

                while (keepTimerRunning) {
                    // 1) Progress checking
                    double elapsedTime = (new Date()).getTime() - timerStartTime;
                    double proportionComplete = elapsedTime / totalDuration;

                    if (!hasUpdatedProgress) {
                        updateProgressIndicator(proportionComplete);
                        hasUpdatedProgress = true;
                    }

                    if (lastProgressUpdate + 0.0025 <= proportionComplete) {
                        hasUpdatedProgress = false; // we've updated for now
                        lastProgressUpdate += 0.0025; // update the ProgressIndicator in 0.25% increments
                    }

                    // 2) Play a sound?
                    // Are we at, or have we passed, a time when a sound should have been played?
                    if (soundSchedule[soundIndex][0] <= elapsedTime) {
                        // If yes, has this sound NOT been played?
                        if (soundSchedule[soundIndex][1] == 0) {
                            // If yes, which sound are we going to play?
                            if (soundIndex == soundSchedule.length - 1) { // last = main sound

                                String musicFile = Sound.getPathFromSoundList(sequence.getMainSoundName());
                                Media sound = new Media(new File(musicFile).toURI().toString());
                                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                                new Thread() {
                                    public void run() {
                                        mediaPlayer.play();
                                    }
                                }.start();

                                //sequence.playMainSound();
                                System.out.println("-----MAIN SOUND");
                            } else {

                                String musicFile = Sound.getPathFromSoundList(sequence.getSecondarySoundName());
                                Media sound = new Media(new File(musicFile).toURI().toString());
                                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                                new Thread() {
                                    public void run() {
                                        mediaPlayer.play();
                                    }
                                }.start();

                                // sequence.playSecondarySound();
                                System.out.println("-----SECONDARY SOUND");
                            }

                            // record that we've played the sound
                            soundSchedule[soundIndex][1] = 1;
                            soundIndex++;
                        }
                    }

                    // 3) Check for exit or pause conditions
                    //  - timer is done
                    if (elapsedTime >= totalDuration) {
                        keepTimerRunning = false;
                    }
                }
                // Check to see if the Timer is paused
                if (timerIsPaused) {
                    timerIsPaused();
                } else {
                    timerIsStopped();
                }
            }
        };
        timerThread.start();
    }
    /**
     * Stops the timer
     */
    public void stopTimer() {
        keepTimerRunning = false;

        if (timerIsPaused) {
            timerIsPaused = false;
            timerIsStopped();
        }
    }
    /**
     * Pauses the timer
     */
    public void pauseTimer() {
        timerIsPaused = true;
        timerPausedTime = System.currentTimeMillis();
        keepTimerRunning = false;
    }
    /**
     * Configures the display for when the timer is running
     */
    public void timerIsRunning() {
        // Make things invisible
        //  - edit buttons
        btnAddInterval.setVisible(false);
        btnDeleteInterval.setVisible(false);
        btnMoveIntervalLeft.setVisible(false);
        btnMoveIntervalRight.setVisible(false);
        //  - ListView
        lvIntervals.setVisible(false);
        //  - duration editor
        btnSetDuration.setVisible(false);
        tfDuration.setVisible(false);
        //  - ListView label
        lblInterval.setVisible(false);
        //  - Sound options (from the top)
        //      *** primary sound
        lblPrimSound.setVisible(false);
        cbMainSound.setVisible(false);
        btnPlayMainSound.setVisible(false);
        //      *** secondary sound
        lblSecondSound.setVisible(false);
        cbSecondSound.setVisible(false);
        btnPlaySecondSound.setVisible(false);
        //  - progress indicator
        //      (progress indicator stays)
        //  - profiles components
        cbProfiles.setVisible(false);
        btnDeleteProfile.setVisible(false);
        btnLoadProfile.setVisible(false);
        btnSaveProfile.setVisible(false);
        btnSetDefaultProfile.setVisible(false);
        lblProfiles.setVisible(false);
        //  - timer controls
        btnStartTimer.setVisible(false);
        btnPauseTimer.setVisible(true);
        btnStopTimer.setVisible(true);
        btnContinueTimer.setVisible(false);
    }
    /**
     * Configures the display for when the timer is stopped
     */
    public void timerIsStopped() {
        // Make things invisible
        //  - edit buttons
        btnAddInterval.setVisible(true);
        btnDeleteInterval.setVisible(true);
        btnMoveIntervalLeft.setVisible(true);
        btnMoveIntervalRight.setVisible(true);
        //  - draw the ListView
        lvIntervals.setVisible(true);
        //  - draw the duration editor
        btnSetDuration.setVisible(true);
        tfDuration.setVisible(true);
        //  - draw the ListView label
        lblInterval.setVisible(true);
        //  - draw the Sound options (from the top)
        //      *** primary sound
        lblPrimSound.setVisible(true);
        cbMainSound.setVisible(true);
        btnPlayMainSound.setVisible(true);
        //      *** secondary sound
        lblSecondSound.setVisible(true);
        cbSecondSound.setVisible(true);
        btnPlaySecondSound.setVisible(true);
        //  - draw the progress indicator
        piSessionStatus.setProgress(0);
        //  - profiles components
        cbProfiles.setVisible(true);
        btnDeleteProfile.setVisible(true);
        btnLoadProfile.setVisible(true);
        btnSaveProfile.setVisible(true);
        btnSetDefaultProfile.setVisible(true);
        lblProfiles.setVisible(true);
        //  - timer controls
        btnStartTimer.setVisible(true);
        btnPauseTimer.setVisible(false);
        btnStopTimer.setVisible(false);
        btnContinueTimer.setVisible(false);
    }
    /**
     * Configures the display for when the timer is paused
     */
    public void timerIsPaused() {
        btnPauseTimer.setVisible(false);
        btnContinueTimer.setVisible(true);
    }

    //  - Helper methods
    /**
     * Gets an array, of the durations, of all intervals in the sequence
     * @return an array of the durations of all intervals in the sequence
     */
    public String[] getIntervalsForListView() {
        String[] durations = new String[sequence.getSequenceArray().length];

        for (int index = 0; index < sequence.getSequenceArray().length; index++) {
            // durations[index] = "(" + (index + 1) + ")\n" + Double.toString(sequence.getSequenceArray()[index].getDuration()) + "\nmins";
            durations[index] = "Interval " + (index + 1) + ":\n" + Double.toString(sequence.getSequenceArray()[index].getDuration()) + "mins";
            System.out.print(durations[index] + " - ");
        }
        System.out.print("\n");

        return durations;
    }

    /**
     * Gets an array of profiles, formatted for the cbProfiles drop-down box
     * @return an array of profiles formatted for the cbProfiles drop-down box
     */
    public String[] getProfiles() {
        String[] profiles = new String[sequence.getProfiles().length];

        // First profile is Program Default
        profiles[0] = "Program Default";
        for (int profileIndex = 1; profileIndex < profiles.length; profileIndex++) {

            String output = "Profile " + profileIndex;

            if (sequence.getProfiles()[profileIndex]) { // this is a saved profile
                // ... get the date it was saved
                output += " (saved " + sequence.getProfileDateSaved(profileIndex) + ")";
            }
                if (profileIndex == sequence.getDefaultProfile()) {
                output += " (Default)";
            }

            profiles[profileIndex] = output;
        }

        return profiles;
    }
    /**
     * Loads the profile selected by the user and updates the relevant components
     */
    public void loadProfile() {
        sequence.loadProfile(cbProfiles.getSelectionModel().getSelectedIndex());
        updateProfilesGraphics();
        updateIntervalsGraphics();
        updateSoundsGraphics();
    }
    /**
     * Saves the profile created by the user and updates the relevant components
     */
    public void saveProfile() {
        sequence.saveProfile(cbProfiles.getSelectionModel().getSelectedIndex());
        updateProfilesGraphics();
    }
    /**
     * Deletes the profile selected by the user and updates the relevant components
     */
    public void deleteProfile() {
        int profileToDelete = cbProfiles.getSelectionModel().getSelectedIndex();

        // If the user is deleting their default profile we will make the Program Default the new default
        if (profileToDelete == sequence.getDefaultProfile()) {
            sequence.setDefaultProfile(0);
        }

        sequence.deleteProfile(profileToDelete);
        updateProfilesGraphics();
    }
    public void setDefaultProfile() {
        // set the currently selected profile to default
        sequence.setDefaultProfile(cbProfiles.getSelectionModel().getSelectedIndex());
        // in case it hasn't been loaded already
        loadProfile();
    }

    //  - Graphic methods
    /**
     * Draws (or re-draws) the "Sequence Editor" components (used to update display)
     */
    public void updateIntervalsGraphics() {
        // Populate the ListView with the sequence array
        lvIntervals.setItems(FXCollections.observableArrayList(getIntervalsForListView()));
    }
    /**
     * Updates the profiles combo boxes (used to update display)
     */
    public void updateProfilesGraphics() {
        cbProfiles.setItems(FXCollections.observableArrayList(getProfiles()));
    }
    /**
     * Updates the sound combo boxes  (used to update display)
     */
    public void updateSoundsGraphics() {
        cbMainSound.getSelectionModel().select(Sound.getSoundIndex(sequence.getMainSoundName()));
        cbSecondSound.getSelectionModel().select(Sound.getSoundIndex(sequence.getSecondarySoundName()));
    }
    /**
     * Updates the ProgressIndicator (only used by timer methods)
     * @param proportionComplete What proportion of the total duration has been completed
     */
    public void updateProgressIndicator(double proportionComplete) {
        // Update the ProgressIndicator
        piSessionStatus.setProgress(proportionComplete);
    }

    /**
     * Draws all static graphical components (to avoid cluttering the start method)
     */
    public void drawStaticComponents() {
        // Draws components bottom to top
        //  - draw the Sound options
        //      *** primary sound
        final double SOUND_W = (COMP_MAX_W) / 2 - PADDING * 1.5 - btnPlayMainSound.getWidth();
        cbMainSound.setPrefWidth(SOUND_W);
        cbMainSound.relocate(MARGIN, WINDOW_H - lblPrimSound.getHeight() - COMP_H / 2);
        lblPrimSound.relocate(MARGIN, WINDOW_H - COMP_H / 2);
        btnPlayMainSound.relocate(WINDOW_W / 2 - PADDING / 2 - btnPlayMainSound.getWidth(), WINDOW_H - lblPrimSound.getHeight() - COMP_H / 2);
        //      *** secondary sound
        cbSecondSound.setPrefWidth(SOUND_W);
        cbSecondSound.relocate(WINDOW_W / 2 + PADDING / 2, WINDOW_H - lblPrimSound.getHeight() - COMP_H / 2);
        lblSecondSound.relocate(WINDOW_W / 2 + PADDING / 2, WINDOW_H - COMP_H / 2);
        btnPlaySecondSound.relocate(WINDOW_W - MARGIN - btnPlayMainSound.getWidth(), WINDOW_H - lblPrimSound.getHeight() - COMP_H / 2);
        final double SOUND_TOP = WINDOW_H - lblPrimSound.getHeight() - COMP_H / 2;
        //  - draw the buttons
        final double BUTTON_W = ((COMP_MAX_W - PADDING * 2) * 0.75) / 2;
        btnAddInterval.relocate(MARGIN, SOUND_TOP - PADDING - COMP_H);
        btnDeleteInterval.relocate(MARGIN + COMP_MAX_W / 2 + PADDING / 2, SOUND_TOP - PADDING - COMP_H);
        btnMoveIntervalLeft.relocate(MARGIN, SOUND_TOP - PADDING * 2 - COMP_H * 2);
        btnMoveIntervalRight.relocate(MARGIN + COMP_MAX_W / 2 + PADDING / 2, SOUND_TOP - PADDING * 2 - COMP_H * 2);
        final double BUTTONS_TOP = SOUND_TOP - PADDING * 2 - COMP_H * 2;
        //  - draw the ListView
        lvIntervals.relocate(MARGIN, BUTTONS_TOP - lvIntervals.getHeight() - PADDING);
        final double LV_TOP = BUTTONS_TOP - PADDING * 2 - COMP_H;
        //  - draw the duration editor
        final double DURATION_W = MARGIN + ((COMP_MAX_W - PADDING) * 0.75) + PADDING;
        btnSetDuration.relocate(DURATION_W, LV_TOP);
        tfDuration.relocate(DURATION_W, LV_TOP + btnSetDuration.getHeight() + PADDING);
        //  - draw the main label
        lblInterval.relocate(MARGIN, LV_TOP - lblInterval.getHeight());
        final double SETTINGS_TOP = LV_TOP - lblInterval.getHeight();
        // CONFIGURE the Profile components
        cbProfiles.setPrefWidth(COMP_MAX_W - btnLoadProfile.getWidth() - PADDING * 3 - btnSaveProfile.getWidth() - btnDeleteProfile.getWidth());
        cbProfiles.relocate(MARGIN, MARGIN);
        btnLoadProfile.relocate(WINDOW_W - MARGIN - btnDeleteProfile.getWidth() - PADDING - btnSaveProfile.getWidth() - PADDING - btnLoadProfile.getWidth(), MARGIN);
        btnSaveProfile.relocate(WINDOW_W - MARGIN - btnDeleteProfile.getWidth() - PADDING - btnSaveProfile.getWidth(), MARGIN);
        btnDeleteProfile.relocate(WINDOW_W - MARGIN - btnDeleteProfile.getWidth(), MARGIN);
        btnSetDefaultProfile.setPrefWidth(btnLoadProfile.getWidth() + PADDING + btnSaveProfile.getWidth() + PADDING + btnDeleteProfile.getWidth());
        btnSetDefaultProfile.relocate(WINDOW_W - MARGIN - btnDeleteProfile.getWidth() - PADDING - btnSaveProfile.getWidth() - PADDING - btnLoadProfile.getWidth(), MARGIN + btnDeleteProfile.getHeight() + PADDING);
        lblProfiles.relocate(MARGIN, MARGIN + COMP_H / 2);
        final double PROFILE_TOP = LV_TOP - PADDING - COMP_H / 2;
        //  - draw the progress indicator
        final double PI_TOP_BOUND = MARGIN + COMP_H / 2;
        final double PI_BOTTOM_BOUND = SETTINGS_TOP;
        final double PI_Y_COORD = (PI_BOTTOM_BOUND - PI_TOP_BOUND - piSessionStatus.getHeight()) / 2 + PI_TOP_BOUND + PADDING;
        piSessionStatus.relocate(MARGIN, PI_Y_COORD);
        //  - timer controls
        btnStartTimer.relocate((WINDOW_W - btnStartTimer.getWidth()) / 2, PI_Y_COORD + (piSessionStatus.getHeight()) / 2);
        btnPauseTimer.relocate(MARGIN, WINDOW_H - MARGIN - btnPauseTimer.getHeight());
        btnStopTimer.relocate(MARGIN, WINDOW_H - MARGIN - btnPauseTimer.getHeight() - PADDING - btnStopTimer.getHeight());
        btnContinueTimer.relocate(MARGIN, WINDOW_H - MARGIN - btnPauseTimer.getHeight());
        btnPauseTimer.setVisible(false);
        btnStopTimer.setVisible(false);
        btnContinueTimer.setVisible(false);
    }

    //  - Handler methods
    //      *** ...for the Sound components
    /**
     * Handler: Plays a sound when one of the play buttons have been pressed
     * NOTE: this uses the old non-JavaFX version--keeping it here for later learning
     * @param e MouseEvent
     */
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
    /**
     * Handler: Sets the main sound to the one chosen by the user in the combo box
     * @param e Event
     */
    public void setMainSoundHandler(Event e) {
        int soundIndex = cbMainSound.getSelectionModel().getSelectedIndex();
        sequence.setMainSound(Sound.getSoundList()[soundIndex]);
    }
    /**
     * Handler: Sets the main sound to the one chosen by the user in the combo box
     * @param e
     */
    public void setSecondSoundHandler(Event e) {
        int soundIndex = cbSecondSound.getSelectionModel().getSelectedIndex();
        sequence.setSecondarySound(Sound.getSoundList()[soundIndex]);
    }
    //      *** ...for the ListView and Duration editing components
    /**
     * Handler: Puts the duration of a selected Interval into the duration TextField
     * @param e MouseEvent
     */
    private void printDurationHandler(MouseEvent e) {
        int index = lvIntervals.getSelectionModel().getSelectedIndex();
        double duration = sequence.getSequenceArray()[index].getDuration();

        tfDuration.setText(Double.toString(duration));
        updateIntervalsGraphics();
    }
    /**
     * Handler: Edits an interval's duration
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
                int index = lvIntervals.getSelectionModel().getSelectedIndex();
                sequence.getSequenceArray()[index].setDuration(newDuration);
                updateIntervalsGraphics();
            } else { // Throw exception because the duration is outside the limits
                throw new Exception();
            }
        } catch (Exception exception) {
            // Just re-set the TextField to the Interval's original duration
            int index = lvIntervals.getSelectionModel().getSelectedIndex();
            double duration = sequence.getSequenceArray()[index].getDuration();
            tfDuration.setText(Double.toString(duration));
            updateIntervalsGraphics();
        }
    }
    //      *** ...for editing buttons
    /**
     * Handler: Adds an Interval object to the end of the Sequence's Interval Array
     * @param e ActionEvent
     */
    private void addIntervalHandler(ActionEvent e) {
        // Default duration = 10 minutes
        sequence.addInterval(10);
        updateIntervalsGraphics();
    }
    /**
     * Handler: Deletes an Interval object from the Sequence's Interval Array
     * @param e ActionEvent
     */
    private void deleteIntervalHandler(ActionEvent e) {
        sequence.deleteInterval(lvIntervals.getSelectionModel().getSelectedIndex());
        updateIntervalsGraphics();
    }
    /**
     * Handler: Moves an Interval object to the left (swaps it with the Interval directly to its left)
     * @param e ActionEvent
     */
    private void moveIntervalLeftHandler(ActionEvent e) {
        sequence.moveIntervalLeft(lvIntervals.getSelectionModel().getSelectedIndex());
        updateIntervalsGraphics();
    }
    /**
     * Handler: Moves an Interval object to the right (swaps it with the Interval directly to its right)
     * @param e ActionEvent
     */
    private void moveIntervalRightHandler(ActionEvent e) {
        sequence.moveIntervalRight(lvIntervals.getSelectionModel().getSelectedIndex());
        updateIntervalsGraphics();
    }
    //      *** ...for the profile components

    private void loadProfileHandler(ActionEvent e) {
        loadProfile();
    }
    private void saveProfileHandler(ActionEvent e) {
        saveProfile();
    }
    private void deleteProfileHandler(ActionEvent e) {
        deleteProfile();
    }
    private void setDefaultProfileHandler(ActionEvent e) {
        setDefaultProfile();
    }
    //      *** ...for the timer components
    /**
     * Handler: starts the timer
     * @param e ActionEvent
     */
    public void startTimerHandler(ActionEvent e) {
        startTimer();
    }
    /**
     * Handler: stops the timer
     * @param e ActionEvent
     */
    public void stopTimerHandler(ActionEvent e) {
        stopTimer();
    }
    /**
     * Handler: pauses the timer
     * @param e ActionEvent
     */
    public void pauseTimerHandler(ActionEvent e) {
        pauseTimer();
    }

    /**
     * Main start method
     *
     * @param stage The main stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        for (String each: getProfiles()) {
            System.out.println(each);
        }

        Pane root = new Pane();
        Scene scene = new Scene(root, WINDOW_W, WINDOW_H); // size of the window
        scene.getStylesheets().add("resources/css/" + STYLE + ".css");
        stage.setTitle("Meditation and Prayer Timer"); // set the window title here
        stage.setScene(scene);

        // TODO: 2. Create the GUI components
        // CREATE the background
        canvasBG = new Canvas(WINDOW_W, WINDOW_H);
        gcBG = canvasBG.getGraphicsContext2D();
        // CREATE the Sequence Editor objects
        //  - Profile components
        cbProfiles = new ComboBox(profilesList); // TODO: add the list of Profiles
        btnSaveProfile = new Button("SAVE");
        btnLoadProfile = new Button("LOAD");
        btnDeleteProfile = new Button("DELETE");
        btnSetDefaultProfile = new Button("SET AS DEFAULT");
        lblProfiles = new Label("(profiles)");
        //  - ListView of sequence intervals
        lblInterval = new Label("PRAYER SESSION");
        lvIntervals = new ListView<String>();
        //  - Duration editor components
        tfDuration = new TextField();
        btnSetDuration = new Button("SET");
        //  - Sequence Editor buttons
        btnAddInterval = new Button("ADD");
        btnMoveIntervalLeft = new Button ("\u25C0 MOVE");
        btnMoveIntervalRight = new Button ("MOVE \u25B6");
        btnDeleteInterval = new Button ("DELETE");
        // CREATE Sound options
        cbMainSound = new ComboBox(soundListMain);
        btnPlayMainSound = new Button ("\u25b6"); // play symbol
        cbSecondSound = new ComboBox(soundListSecond);
        btnPlaySecondSound = new Button ("\u25b6"); // play symbol
        lblPrimSound = new Label ("(primary sound)");
        lblSecondSound = new Label ("(interval sound)");
        // CREATE Progress indicator
        piSessionStatus = new ProgressIndicator();
        // CREATE Timer Controls
        btnStartTimer = new Button("Begin Session");
        btnPauseTimer = new Button("Pause Session");
        btnStopTimer = new Button("Stop Session");
        btnContinueTimer = new Button("Continue Session");

        // TODO: 3. Add components to the root
        //  - Background components
        root.getChildren().addAll(canvasBG);
        //  - Profile components
        root.getChildren().addAll(cbProfiles, btnSaveProfile, btnLoadProfile, btnDeleteProfile, btnSetDefaultProfile, lblProfiles);
        //  - Sequence Editor components
        root.getChildren().addAll(lvIntervals, lblInterval,
                btnAddInterval,btnMoveIntervalLeft, btnMoveIntervalRight, btnDeleteInterval,
                tfDuration, btnSetDuration);
        //  - Sound options
        root.getChildren().addAll(lblPrimSound, lblSecondSound,
                cbMainSound, btnPlayMainSound, cbSecondSound, btnPlaySecondSound);
        //  - Progress indicator and Timer Controls
        root.getChildren().addAll(piSessionStatus, btnStartTimer, btnPauseTimer, btnStopTimer, btnContinueTimer);

        // TODO: 4. Configure the components (colors, fonts, size, location)
        // CONFIGURE the background
        gcBG.drawImage(new Image("resources/images/" + STYLE + "/background.png"), 0, 0, WINDOW_W, WINDOW_H);
        // CONFIGURE the Profile components
        double PROF_H = COMP_H / 2;
        cbProfiles.setPrefHeight(COMP_H / 2);
        cbProfiles.getSelectionModel().select(sequence.getDefaultProfile());
        btnSaveProfile.setPrefHeight(PROF_H);
        btnSaveProfile.getStyleClass().add("button-profile");
        btnLoadProfile.setPrefHeight(PROF_H);
        btnLoadProfile.getStyleClass().add("button-profile");
        btnDeleteProfile.setPrefHeight(PROF_H);
        btnDeleteProfile.getStyleClass().add("button-profile");
        btnSetDefaultProfile.getStyleClass().add("button-profile");
        btnSetDefaultProfile.setPrefHeight(PROF_H);
        lblProfiles.getStyleClass().add("secondary-label");
        lblProfiles.setAlignment(Pos.BASELINE_LEFT);
        // CONFIGURE the Sequence Editor objects
        //  - ListView of sequence intervals
        lblInterval.setMaxWidth((COMP_MAX_W - PADDING) * 0.75);
        lblInterval.setPrefWidth((COMP_MAX_W - PADDING) * 0.75);
        lblInterval.setAlignment(Pos.BASELINE_LEFT);
        lvIntervals.setPrefWidth((COMP_MAX_W - PADDING) * 0.75);
        lvIntervals.setPrefHeight(COMP_H + PADDING);
        lvIntervals.setOrientation(Orientation.HORIZONTAL);
        //  - Duration editor components
        tfDuration.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        tfDuration.setPrefHeight(COMP_H / 2);
        tfDuration.setText("(duration)");
        btnSetDuration.setPrefWidth((COMP_MAX_W - PADDING) * 0.25);
        btnSetDuration.setPrefHeight(COMP_H / 2);
        btnSetDuration.getStyleClass().add("button-set");
        //  - Sequence Editor buttons
        btnAddInterval.setPrefSize((COMP_MAX_W - PADDING) * 0.5, COMP_H);
        btnAddInterval.getStyleClass().add("button-add");
        btnAddInterval.setAlignment(Pos.TOP_RIGHT);
        btnDeleteInterval.setPrefSize((COMP_MAX_W - PADDING) * 0.5, COMP_H);
        btnDeleteInterval.getStyleClass().add("button-delete");
        btnDeleteInterval.setAlignment(Pos.TOP_LEFT);
        btnMoveIntervalLeft.setPrefSize((COMP_MAX_W - PADDING) * 0.5, COMP_H);
        btnMoveIntervalLeft.getStyleClass().add("button-move-l");
        btnMoveIntervalLeft.setAlignment(Pos.BOTTOM_RIGHT);
        btnMoveIntervalRight.setPrefSize((COMP_MAX_W - PADDING) * 0.5, COMP_H);
        btnMoveIntervalRight.getStyleClass().add("button-move-r");
        btnMoveIntervalRight.setAlignment(Pos.BOTTOM_LEFT);
        // CONFIGURE Sound options
        cbMainSound.setVisibleRowCount(4);
        cbMainSound.setPrefHeight(COMP_H / 2);
        cbSecondSound.setVisibleRowCount(4);
        cbSecondSound.setPrefHeight(COMP_H / 2);
        cbSecondSound.getSelectionModel().select(Sound.getSoundIndex(secondSound));
        cbMainSound.getSelectionModel().select(Sound.getSoundIndex(mainSound));
        btnPlayMainSound.setPrefSize(COMP_H / 2, COMP_H / 2);
        btnPlayMainSound.getStyleClass().add("button-play");
        btnPlaySecondSound.setPrefSize(COMP_H / 2, COMP_H / 2);
        btnPlaySecondSound.getStyleClass().add("button-play");
        lblPrimSound.setPrefSize((COMP_MAX_W - PADDING) / 2,COMP_H / 2 - PADDING / 2);
        lblPrimSound.getStyleClass().add("secondary-label");
        lblPrimSound.setAlignment(Pos.BASELINE_LEFT);
        lblSecondSound.setPrefSize((COMP_MAX_W - PADDING) / 2,COMP_H / 2 - PADDING / 2);
        lblSecondSound.getStyleClass().add("secondary-label");
        lblSecondSound.setAlignment(Pos.BASELINE_LEFT);
        // CONFIGURE Progress indicator
        piSessionStatus.setProgress(0);
        piSessionStatus.setPrefSize(COMP_MAX_W, COMP_MAX_W * 0.75);
        // CONFIGURE Timer Controls
        btnStartTimer.getStyleClass().add("button-timer-start");
        btnStartTimer.setPrefSize(COMP_MAX_W, COMP_H);
        btnPauseTimer.getStyleClass().add("button-timer-control");
        btnPauseTimer.setPrefSize(COMP_MAX_W, COMP_H);
        btnStopTimer.getStyleClass().add("button-timer-control");
        btnStopTimer.setPrefSize(COMP_MAX_W, COMP_H);
        btnStopTimer.getStyleClass().add("button-timer-control");
        btnContinueTimer.setPrefSize(COMP_MAX_W, COMP_H);
        btnContinueTimer.getStyleClass().add("button-timer-control");

        // TODO: 5. Add Event Handlers and do final setup
        //  - Sound handlers
        cbMainSound.setOnAction(this::setMainSoundHandler);
        cbSecondSound.setOnAction(this::setSecondSoundHandler);
        btnPlayMainSound.setOnMouseClicked(this::playSoundHandler);
        btnPlaySecondSound.setOnMouseClicked(this::playSoundHandler);
        //  - Sequence Editor ListView and Duration editing handlers
        lvIntervals.setOnMouseClicked(this::printDurationHandler);
        btnSetDuration.setOnAction(this::editDurationHandler);
        //  - Sequence Editor button handlers
        btnAddInterval.setOnAction(this::addIntervalHandler);
        btnDeleteInterval.setOnAction(this::deleteIntervalHandler);
        btnMoveIntervalLeft.setOnAction(this::moveIntervalLeftHandler);
        btnMoveIntervalRight.setOnAction(this::moveIntervalRightHandler);
        //  - Profile component handlers
        btnLoadProfile.setOnAction(this::loadProfileHandler);
        btnSaveProfile.setOnAction(this::saveProfileHandler);
        btnDeleteProfile.setOnAction(this::deleteProfileHandler);
        btnSetDefaultProfile.setOnAction(this::setDefaultProfileHandler);
        //  - Timer button handlers
        btnStartTimer.setOnAction(this::startTimerHandler);
        btnStopTimer.setOnAction(this::stopTimerHandler);
        btnPauseTimer.setOnAction(this::pauseTimerHandler);
        btnContinueTimer.setOnAction(this::startTimerHandler);

        // TODO: 6. Show the stage
        stage.show();

        // Show this after the stage so that we can get the width of components (for displaying things correctly)
        drawStaticComponents();
        updateProfilesGraphics();
        updateIntervalsGraphics();
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
