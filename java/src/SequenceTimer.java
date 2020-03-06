import java.util.Timer;

/**
 * stuff
 * started 2020-03-06
 * TODO:
 *  - variables
 *  - methods
 *      -
 *
 * @author josephhaley
 */
public class SequenceTimer {
    // VARIABLES
    /** The time when the first SoundTask is assigned to the Timer--effectively, this is the start time of the Timer **/
    private long startTime; // in milliseconds
    /** The total duration that the Timer will run for **/
    private long totalDuration; // in milliseconds

    /** The Sequence that will be parsed into tasks and then passed to the timer to be run **/
    private Sequence sequence;

    /** For communication between threads **/
    private boolean isTimerRunning = false; // for indicating whether the Timer is running
    private boolean killStatusThread = false; // for killing the infinite loop in the Status Thread
    private boolean isTimerPaused = false; // for indicating whether the Timer is paused (to distinguish it from Timer
    // being shut down
    private static boolean hasSoundTriggered = false; // for indicating whether a sound has just been played


    // METHODS
    // *** Constructor(s)
    public SequenceTimer() {

    }

    // *** Variable Method(s)

    // *** Utility Method(s)
    @Override
    public String toString() {
        return "";
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {

    }
}
