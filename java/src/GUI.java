import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A GUI example of meditation timer
 * started 2020-03-14
 *
 * Background image: https://www.pexels.com/@snapwire
 *
 * @author josephhaley
 * @author Sam Scott (GUI template)
 */
public class GUI extends Application {
    // VARIABLES
    //  - Window attributes
    private final double WINDOW_W = 412;
    private final double WINDOW_H = 732;
    private final double MARGIN = 10;
    private final double PADDING = 5;
    //  - Instance variable(s)
    Sequence sequence = new Sequence();
    //  - Graphical components
    //      *** background
    Canvas canvasBG;
    GraphicsContext gcBG;
    //      *** Sequence Editor components
    ListView<String> lvInterval;
    Button btnAddInterval;
    Button btnMoveIntervalLeft;
    Button btnMoveIntervalRight;
    Button btnDeleteInterval;

    // METHODS
    //  - Helper methods
    /**
     * Gets an array, of the durations, of all intervals in the sequence
     * @return
     */
    public String[] getIntervalDurations() {
        // we subtract one from the length of the array because we don't want to display the final interval (it's just
        //  a placeholder for final sound
        String[] durations = new String[sequence.getSequenceArray().length - 1];

        for (int index = 0; index < sequence.getSequenceArray().length - 1; index++) {
            durations[index] = Double.toString(sequence.getSequenceArray()[index].getDuration());
        }

        return durations;
    }
    //  - Graphic methods
    //      *** draw the ListView
    public void updateLVInterval() {
        // populate the list view with the sequence array
        lvInterval.setItems(FXCollections.observableArrayList(getIntervalDurations()));

        // set the attributes
        lvInterval.setPrefWidth(WINDOW_W - MARGIN * 2);
        lvInterval.setPrefHeight(WINDOW_H * 0.07);
        lvInterval.setOrientation(Orientation.HORIZONTAL);

        lvInterval.relocate(MARGIN, WINDOW_H * 0.60);
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
        stage.setTitle("Meditation and Prayer Timer"); // set the window title here
        stage.setScene(scene);

        // TODO: 1. Create the model
        // TODO: 2. Create the GUI components
        // Set up the background
        canvasBG = new Canvas(WINDOW_W, WINDOW_H);
        gcBG = canvasBG.getGraphicsContext2D();
        gcBG.drawImage(new Image("resources/images/background.png"), 0, 0, WINDOW_W, WINDOW_H);
        // Set up the Sequence Editor objects
        lvInterval = new ListView<String>();
        updateLVInterval();
        btnAddInterval = new Button("Add Interval");
        btnMoveIntervalLeft = new Button ("Move Left");
        btnMoveIntervalRight = new Button ("Move Right");
        btnDeleteInterval = new Button ("Delete Interval");


        // TODO: 3. Add components to the root
        root.getChildren().addAll(canvasBG, lvInterval);

        // TODO: 4. Configure the components (colors, fonts, size, location)
        // TODO: 5. Add Event Handlers and do final setup
        // TODO: 6. Show the stage
        stage.show();
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
