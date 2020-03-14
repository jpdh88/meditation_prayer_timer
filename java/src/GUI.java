import javafx.application.Application;
import javafx.scene.Scene;
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
        // TODO: Add your GUI-building code here

        // 1. Create the model
        // 2. Create the GUI components
        // 3. Add components to the root
        // 4. Configure the components (colors, fonts, size, location)
        // 5. Add Event Handlers and do final setup
        // 6. Show the stage
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
