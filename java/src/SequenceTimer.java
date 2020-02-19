import javax.sound.sampled.AudioInputStream;
import java.util.*;

/**
 * Represents a TimerTask, which is passed to a Timer.
 *
 * @author Joseph Haley
 */
class SoundTask extends TimerTask {
    // VARIABLES
    /** Info: oneMin = 60s * 1000ms **/
    private int duration; // in milliseconds
    private AudioInputStream sound;

    // METHODS
    // *** Constructors
    public SoundTask (AudioInputStream sound) {
        this.sound = sound;
    }

    // *** Utility Methods
    /**
     * When a SoundTask object is called, the following code is run
     */
    public void run() {
        try {
            System.out.print("\n>");
            Sound.playSound(sound);
        } catch (Exception e) {
            System.out.println("SoundTask Object Error: " + e);
        }
    }

}

/**
 * Represents a Timer for a Sequence. Essentially, using instance methods, a Sequence is parsed into TimerTasks which
 *  are scheduled in a Timer object running as a daemon.
 *
 * @author Joseph Haley
 *
 * TODO:
 *  1) output running status of timer to console
 *      - have another thread for this?
 *  2) ensure that when Timer tasks are finished the while loop checking for user input also ends
 *      - put the while loop in its own thread?
 *  3) a method to pause the timer
 *  4) instead of returning an ArrayList in sequence.getSequenceArray: just return an array
 */
public class SequenceTimer {
    // VARIABLES
    /** The time when the first SoundTask is assigned to the Timer--effectively, this is the start time of the Timer **/
    private long startTime; // in milliseconds
    /** The total duration that the Timer will run for **/
    private long totalDuration; // in milliseconds

    /** The Sequence that will be parsed into tasks and then passed to the timer to be run **/
    private Sequence sequence;
    /** ArrayList of Interval objects **/
    ArrayList<Interval> intervalArrayList;
    /** Convert ArrayList into an Array **/
    Interval[] intervalArray;

    /** The internal timer which will execute the tasks **/
    private Timer timer;
    private boolean isTimerRunning = false;
    private boolean isTimerPaused = false;

    /** The thread which will intermittently output the elapsed time **/
    private Thread elapsedTimeThread;

    // METHODS
    // *** Constructor(s)
    /**
     * Creates a SequenceTimer object
     * @param sequence the Sequence object which will be run through the Timer
     */
    public SequenceTimer (Sequence sequence) {
        // Initialize sequence-related variables
        this.sequence = sequence;
        this.intervalArrayList = new ArrayList<>(this.sequence.getSequenceArray());
        this.intervalArray = intervalArrayList.toArray(new Interval[intervalArrayList.size()]);

        // Create the timer
        timer = new Timer("Timer Daemon", true);

        // Calculate the total duration of the sequence
        long subDuration = 1L;
        for (Interval interval: intervalArray) {
            subDuration += interval.getDuration();
        }
        totalDuration = subDuration;
    }

    // *** Value Method(s)
    /**
     * Sets the time the Timer was started
     */
    private void setStartTime() {
        this.startTime = System.currentTimeMillis();;
    }
    /**
     * Gets the time when the Timer was started
     * @return The time when the Timer was started (in milliseconds)
     */
    public long getStartTime() {
        return startTime;
    }
    /**
     * Get the elapsed time since the Timer was started
     * @return The elapsed time since the Timer was started (in milliseconds)
     */
    public long getElapsedTime() {
        return ((new Date()).getTime() - startTime);
    }

    /**
     * Gets the total duration that the sequence timer will run for
     * @return The total duration of the sequence in milliseconds
     */
    public long getTotalDuration() {
        return totalDuration;
    }

    // *** Utility Method(s)
    /**
     * Method that runs the timer. Essentially, it passes tasks to the Timer which starts running them.
     */
    public void run() {
        // Start a thread that will make intermittent output to indicate that the Timer is running
        new Thread() {
            public void run() {
                boolean hasDisplayed = false;
                final long skipDuration = 5000L;
                long lastDuration = 0L;
                while (isTimerRunning) {
                    if (getElapsedTime() % skipDuration == 0) { // print something every second
                        if (hasDisplayed == false) {
                            System.out.print('.');
                            hasDisplayed = true;
                        }
                    }
                    if (lastDuration + skipDuration == getElapsedTime()) {
                        hasDisplayed = false;
                        lastDuration += skipDuration;
                    }
                }
            }
        }.start();

        // Schedule all the Sounds
        String soundPath = "";
        long accruedDuration = 1L;
        for (int intervalIndex = 0; intervalIndex < intervalArray.length; intervalIndex++) {
            if (intervalIndex == 0) { // First interval (uses main sound)
                soundPath = Sound.getPathFromSoundList(sequence.getMainSound());

                // "Start" the object's internal status clock
                this.setStartTime();
                // Let any threads know that the Timer has scheduled tasks
                this.isTimerRunning = true;
            } else if (intervalIndex == intervalArray.length - 1) { // Last interval (uses main sound)
                soundPath = Sound.getPathFromSoundList(sequence.getMainSound());
            } else { // Middle intervals (use secondary sound)
                soundPath = Sound.getPathFromSoundList(sequence.getIntervalSound());
            }

            try {
                timer.schedule(new SoundTask(Sound.createSoundStream(soundPath)), accruedDuration * 1000);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }

            accruedDuration += intervalArray[intervalIndex].getDuration();
        }
    }

    /**
     * Ends a running SequenceTimer
     */
    public void endTimer() {
        timer.cancel();
        isTimerRunning = false;
    }

    /**
     * Pauses the timer
     */
    public void pauseTimer() {
        long timerStatus = getElapsedTime();

        // Kill the timer and all related threads
        timer.cancel();
        isTimerRunning = false;
        isTimerPaused = true;
    }
    /**
     * Restarts a paused timer
     */
    public void restartTimer() {

    }

    /**
     * Main Method: For testing
     * @param args
     */
    public static void main(String[] args) {

        Sequence sequence = new Sequence();
        sequence.editInterval(0, 20);
        sequence.addInterval(30);
        sequence.addInterval(20);

        SequenceTimer sequenceTimer = new SequenceTimer(sequence);
        sequenceTimer.run();

        // Conditions to exit the while loop:
        //  - on user choice
        //  - if both timer is not running AND timer is not paused
        while (sequenceTimer.isTimerRunning || sequenceTimer.isTimerPaused) {
            Scanner keybIn = new Scanner(System.in);
            String userInput = keybIn.nextLine();

            if (userInput.equals("q")) { // exit condition is met
                sequenceTimer.endTimer();
                break;
            } else if (userInput.equals("a")) {
                System.out.println(sequenceTimer.getElapsedTime());
            }
        }
    }

}