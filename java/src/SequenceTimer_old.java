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
    private boolean isLastSound; // is this SoundTask for the last sound?
    private static boolean lastSoundDone = false; // indicate that the last sound has been played
    private AudioInputStream sound;

    // METHODS
    // *** Constructors
    public SoundTask (AudioInputStream sound, boolean isLastSound) {
        this.sound = sound;
        this.isLastSound = isLastSound;
    }

    // *** Variable Methods
    public static boolean getLastSoundDone() {
        return lastSoundDone;
    }

    // *** Utility Methods
    /**
     * When a SoundTask object is called, the following code is run
     */
    public void run() {
        try {
            SequenceTimer.setHasSoundTriggered(true);
            Sound.playSound(sound);
            if (isLastSound) {
                lastSoundDone = true;
            }
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
 *  -) output running status of timer to console
 *      - have another thread for this?
 *  -) make status bar more accurate (I don't think its keeping track of the time of the sound-plays)
 *  -) a method to pause the timer
 *
 *  DONE:
 *  4) instead of returning an ArrayList in sequence.getSequenceArray: just return an array
 *  -) ensure that when Timer tasks are finished the while loop checking for user input also ends
 *      - put the while loop in its own thread?
 */
public class SequenceTimer {
    // VARIABLES
    /** The time when the first SoundTask is assigned to the Timer--effectively, this is the start time of the Timer **/
    private long startTime; // in milliseconds
    /** The total duration that the Timer will run for **/
    private long totalDuration; // in milliseconds
    private final long totalDurationBackup; // in milliseconds

    /** The Sequence that will be parsed into tasks and then passed to the timer to be run **/
    private Sequence sequence;
    /** Array of intervals **/
    private Interval[] intervalArray;
    private Interval[] intervalArrayBackup; // so we always have a copy of the original array (for
                                                // restarting timer from the beginning)

    /** The internal timer which will execute the tasks **/
    private Timer timer;

    /** The status bar output **/
    int numCharColumns = 100; // number of characters for the status bar
    String[] statusArray = new String[numCharColumns];

    /** For communication between threads **/
    private boolean isTimerRunning = false; // for indicating whether the Timer is running
    private boolean killStatusThread = false; // for killing the infinite loop in the Status Thread

    private boolean isTimerPaused = false; // for indicating whether the Timer is paused (to distinguish it from Timer
                                            // being shut down
    private static boolean hasSoundTriggered = false; // for indicating whether a sound has just been played

    // METHODS
    // *** Constructor(s)
    /**
     * Creates a SequenceTimer object
     * @param sequence the Sequence object which will be run through the Timer
     */
    public SequenceTimer (Sequence sequence) {
        // Initialize sequence-related variables
        this.sequence = sequence;
        intervalArray = this.sequence.getSequenceArray();
        intervalArrayBackup = new Interval[intervalArray.length];
        System.arraycopy(intervalArray, 0, intervalArrayBackup, 0, intervalArray.length);

        // Create the timer
        timer = new Timer("Timer Daemon", true);

        // Calculate the total duration of the sequence (milliseconds)
        long subDurations = 1L; // cannot be 0, Timer cannot schedule events with a delay of 0
        for (Interval interval: intervalArray) {
            subDurations += interval.getDuration();
        }
        totalDuration = subDurations;
        totalDurationBackup = totalDuration;
    }

    // *** Value Method(s)
    /**
     * Sets the time the Timer was started
     */
    private void setStartTime() {
        startTime = System.currentTimeMillis();
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
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Gets the total duration that the sequence timer will run for
     * @return The total duration of the sequence in milliseconds
     */
    public long getTotalDuration() {
        return totalDuration;
    }

    /**
     * Get whether a sound has been triggered or not
     * @return Whether the sound has been triggered
     */
    public static boolean getHasSoundTriggered() {
        return hasSoundTriggered;
    }

    /**
     * Set whether the sound has been triggered
     * @param triggered Whether the sound has been triggered or not
     */
    public static void setHasSoundTriggered(boolean triggered) {
        hasSoundTriggered = triggered;
    }

    /**
     * Get whether the timer is running or not
     * @return Whether the timer is running or not
     */
    public boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    /**
     * Get whether the timer is paused or not
     * @return Whether the timer is paused or not
     */
    public boolean getIsTimerPaused() {
        return isTimerPaused;
    }

    // *** Utility Method(s)
    /**
     * Method that runs the timer. Essentially, it passes tasks to the Timer which starts running them.
     * TODO: fix the first dash
     */
    public void run() {
        // Status Thread: Start a thread that will make intermittent output to indicate that the Timer is running
        new Thread() {
            public void run() {
                boolean hasDisplayed = false;
                // boolean killStatusThread = false;


                // TODO: SHOULD PUT TOTAL DURATION OF THE BACKUP?? FOR TOTAL?
                //  - when restarting the timer, should have the status bar reset at the proper time (add an offset
                //      parameter to the function??)
                //      ... pass in the elapsed time?
                long totalDuration = getTotalDuration(); // total duration of the session
                // By calculating our skipDuration in this way, we ensure that the status bar will change only when needed
                //  (maximum # of changes in the status bar = numCharColumns)
                final long skipDuration = 1000; // (every 5 seconds)

                long lastDuration = 0L;

                while (true) { // this is required in order for nested loop to register change in isTimerRunning to true
                    while (isTimerRunning) {
                        long elapsedTime = getElapsedTime();

                        // Status Indicator
                        if (elapsedTime % skipDuration >= 0 && elapsedTime % skipDuration <= 5) { // print something every nth second
                            if (!hasDisplayed) {
                                statusArray = statusIndicator(statusArray, elapsedTime, totalDuration, getHasSoundTriggered());
                                hasDisplayed = true;
                            }
                        }
                        if (lastDuration + skipDuration > elapsedTime) {
                            hasDisplayed = false;
                            lastDuration += skipDuration;
                        }

                        // TODO: remove this?
                        // Has a sound been triggered?
                        if (SequenceTimer.getHasSoundTriggered()) {

                        }

                        // Has the final sound been played?
                        if (SoundTask.getLastSoundDone()) {
                            endTimer();
                        }
                    }
                    if (killStatusThread) {
                        break;
                    }
                }
            }
        }.start();

        // Schedule all the Sounds
        String soundPath = "";
        long accruedDuration = 1L;
        for (int intervalIndex = 0; intervalIndex < intervalArray.length; intervalIndex++) {
            // Determine the task
            if (intervalArray[intervalIndex] != null) {
                if (intervalIndex == 0) { // First interval (uses main sound)

                    soundPath = Sound.getPathFromSoundList(sequence.getMainSoundName());
                    // Schedule the task
                    try {
                        timer.schedule(new SoundTask(Sound.createSoundStream(soundPath), false), accruedDuration);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e);
                    }

                    // "Start" the object's internal status clock
                    setStartTime();
                    // Let any threads know that the Timer has scheduled tasks
                    this.isTimerRunning = true;

                } else if (intervalIndex == intervalArray.length - 1) { // Last interval (uses main sound)

                    soundPath = Sound.getPathFromSoundList(sequence.getMainSoundName());
                    // Schedule the task
                    try {
                        timer.schedule(new SoundTask(Sound.createSoundStream(soundPath), true), accruedDuration);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e);
                    }

                } else { // Middle intervals (use secondary sound)

                    soundPath = Sound.getPathFromSoundList(sequence.getSecondarySoundName());
                    // Schedule the task
                    try {
                        timer.schedule(new SoundTask(Sound.createSoundStream(soundPath), false), accruedDuration);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e);
                    }
                }

                accruedDuration += intervalArray[intervalIndex].getDuration();
            }
        }
    }

    /**
     * Ends a running SequenceTimer: cancels the timer and exits the maintenance loop
     */
    public void endTimer() {
        // TODO: can we test whether everything has been killed (the timer and all its threads)
        timer.cancel();
        isTimerRunning = false;
        killStatusThread = true;
    }

    /**
     * Pauses the timer: cancels the timer, and prepares to start it up again
     *  - When the timer is cancelled, this function will re-create the sequence array
     *      - any sequences which are completed will be set to null
     *      - the current sequence will have its duration re-calculated
     */
    public void pauseTimer() {
        // Kill the timer and all related threads
        killStatusThread = true;
        timer.cancel();
        isTimerPaused = true;
        isTimerRunning = false;

        // Get the time
        long elapsedTime = getElapsedTime();
        long timeLeft = getTotalDuration() - getElapsedTime();

        // TODO - is this actually working?
        // TODO - make sure Timer can restart from the beginning before working on this further
        // Find which task we are on
        long combinedDuration = 0;
        int indexOfCurrentTask = 0;
        long timeLeftInCurrentTask = 0;

        for (int taskIndex = 0; taskIndex < intervalArray.length; taskIndex++) {
            combinedDuration += intervalArray[taskIndex].getDuration();

            if (elapsedTime < combinedDuration) {
                indexOfCurrentTask = taskIndex;
                timeLeftInCurrentTask = combinedDuration - elapsedTime;
                break;
            }
        }

        // Edit the sequence array to reflect what time we have used up
        for (int taskIndex = 0; taskIndex < intervalArray.length; taskIndex++) {
            if (taskIndex < indexOfCurrentTask) {
                intervalArray[taskIndex] = null;
            } else if (taskIndex == indexOfCurrentTask) {
                intervalArray[taskIndex].setDuration(timeLeftInCurrentTask);
            }
            // Otherwise: leave things alone (the interval tasks haven't been finished yet; they can stay as is)
        }

        for (Interval eachInterval: intervalArray) {
            System.out.println(eachInterval.getDuration());
        }

    }
    /**
     * Restarts a paused timer
     *
     * TODO: PROBLEM -- outputs multiple status bars for some reason (are two timers being created)?
     */
    public void restartTimer() {
        // Create a new timer
        timer = new Timer("Timer Daemon", true);

        // Run the new timer (tasks will be rebuilt)
        killStatusThread = false;
        this.run();
        isTimerRunning = true;
        isTimerPaused = false;
    }

    public void restartTimerBeginning() {
        // Create a new timer
        timer = new Timer("Timer Daemon", true);

        // copy intervalArrayBackup to intervalArray
        System.arraycopy(intervalArrayBackup, 0, intervalArray, 0, intervalArrayBackup.length);

        // Run the new timer (where tasks will be rebuilt)
        this.run();
        isTimerRunning = true;
        isTimerPaused = false;
    }

    /**
     * Prints a status indicator to the terminal screen.
     * @param statusArray A representation in characters of how much of the timer has already occurred
     * @param currentDuration How far along the process is (i.e. the iteration the loop is on)
     * @param totalDuration How long the process is (i.e. the total number of iterations in the loop)
     * @param hasSoundTriggered Whether a sound has been triggered or not: this is significant--refer to code
     * @return The reformulated statusArray (so it can be re-used
     *
     * TODO:
     *  - clean up: this is sloppy
     */
    public static String[] statusIndicator (String[] statusArray, double currentDuration, double totalDuration, boolean hasSoundTriggered) {
        String[] newStatusArray = Arrays.copyOf(statusArray, statusArray.length);

        // The number of chars in the array will determine our 100% point
        int presentIndex = (int) (currentDuration / totalDuration * statusArray.length);

        String backItUp = ""; // string for building the cursor-backer-upper
        String indicator = ""; // string for building the status bar

        // Only change the array if we need to over-write the character at our present index
        if (presentIndex < statusArray.length - 1) {
            if (statusArray[presentIndex] == " " || statusArray[presentIndex] == null) {
                for (int chr = presentIndex; chr < statusArray.length; chr++) {
                    if (chr == presentIndex) {
                        if (hasSoundTriggered) { // a sound has played
                            newStatusArray[chr] = "O";
                            SequenceTimer.setHasSoundTriggered(false); // reset hasSoundTriggered boolean flag
                        } else { // time has passed
                            newStatusArray[chr] = "-";
                        }
                    } else { // indicate time has not passed yet
                        newStatusArray[chr] = " ";
                    }
                }
            }
        } else if (presentIndex == statusArray.length - 1) { // final sound
            newStatusArray[statusArray.length - 1] = "O";
            SequenceTimer.setHasSoundTriggered(false);
        }

        // We need as many backspace characters as are printed to ensure that our status indicator remains in the same
        //  place: charColumns + whatever characters are added to the output
        if (currentDuration != 1) { // we don't need backup characters on the first iteration
            for (int chr = 1; chr <= statusArray.length + 29; chr++) {
                backItUp += "\b";
            }
        }

        // Create status indicator string from the array
        for (String item : newStatusArray) {
            indicator += item;
        }

        // Output backup characters and indicator string w/ a percentage
//        int percentage = (int)((double)presentIndex / (double)statusArray.length * 100);
//        if (percentage <= 100) {
//            System.out.print(backItUp); // backup the cursor to the starting point
//            System.out.print("[" + indicator + "] " + String.format("%3d", percentage) + "%\n");
//        }

        int totalDurSecs = (int)(totalDuration / 1000);
        int totalDurOnlyMins = totalDurSecs / 60;
        int totalDurOnlySecs = totalDurSecs % 60;

        int currentDurSecs = (int)(currentDuration / 1000);
        int currentDurOnlyMins = currentDurSecs / 60;
        int currentDurOnlySecs = currentDurSecs % 60;

        if (currentDuration <= totalDuration) {
            System.out.print(backItUp); // backup the cursor to the starting point
            System.out.print("Status: [" + indicator + "] " + String.format("%3d", currentDurOnlyMins) + "m" + String.format("%2d", currentDurOnlySecs) + "s" +
                    " / " + String.format("%3d", totalDurOnlyMins) + "m" + totalDurOnlySecs + "s");
        }

        // Return the array so it can be reused
        return newStatusArray;
    }

    /**
     * Main Method: For testing
     * @param args
     */
    public static void main(String[] args) {

        Sequence sequence = new Sequence();
        sequence.editInterval(0, (15*60*1000));

        SequenceTimer sequenceTimer = new SequenceTimer(sequence);
        sequenceTimer.run();

        // Conditions to exit the while loop:
        //  - on user choice
        //  - if both timer is not running AND timer is not paused
        while (sequenceTimer.getIsTimerRunning() || sequenceTimer.getIsTimerPaused()) {
            Scanner keybIn = new Scanner(System.in);
            System.out.println("observer thread killed? " + sequenceTimer.killStatusThread);
            String userInput = keybIn.nextLine();

            if (!(sequenceTimer.getIsTimerRunning() || sequenceTimer.getIsTimerPaused())) {
                break;
            } else if (userInput.equals("q")) { // exit condition is met
                sequenceTimer.endTimer();
                break;
            } else if (userInput.equals("p")) {
                sequenceTimer.pauseTimer();
                System.out.println("Timer paused");
            } else if (userInput.equals("r")) {
                System.out.println("Restarting timer");
                sequenceTimer.restartTimer();
            } else if (userInput.equals("l")) {
                sequenceTimer.restartTimerBeginning();
                System.out.println("From beginning");
            }
        }
        System.out.println("-END OF SEQUENCE TIMER-");

    }

}