import java.util.Timer;

/**
 * stuff
 * started 2020-03-06
 * TODO:
 *  - (done) variables
 *  - methods
 *      - constructor
 *      - timer loop and setting boolean flags
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
    /**     [time at which to play sound][whether sound has been played or not] **/
    private long[][] soundSchedule;

    /** For communication between threads **/
    private boolean isTimerRunning = false; // for indicating whether the Timer is running
    private boolean killStatusThread = false; // for killing the infinite loop in the Status Thread
    private boolean isTimerPaused = false; // for indicating whether the Timer is paused (to distinguish it from Timer
    // being shut down
    private static boolean hasSoundTriggered = false; // for indicating whether a sound has just been played


    // METHODS
    // *** Constructor(s)
    public SequenceTimer(Sequence sequence) {
        Interval[] intervals = sequence.getSequenceArray();
        soundSchedule = new long[sequence.getSequenceArray().length][2];

        // First sound plays right away
        soundSchedule[0][0] = 0L;
        soundSchedule[0][1] = 0L;
        for (int eachInterval = 1; eachInterval < intervals.length; eachInterval++) {
            soundSchedule[eachInterval][0] = intervals[eachInterval - 1].getDuration();
            soundSchedule[eachInterval][1] = 0;
        }
    }

    // *** Variable Method(s)
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

    public long[][] getSoundSchedule() {
        return soundSchedule;
    }

    // *** Utility Method(s)
    public void run() {
        // Timer thread: Start a thread that will make intermittent output to indicate that the Timer is running
        new Thread() {
            public void run() {
                boolean hasDisplayed = false;
                // boolean killStatusThread = false;
                long totalDuration = getTotalDuration(); // total duration of the session
                // By calculating our skipDuration in this way, we ensure that the status bar will change only when needed
                //  (maximum # of changes in the status bar = numCharColumns)
                final long skipDuration = 1000; // (every 5 seconds)

                long lastDuration = 0L;

                while (true) { // this is required in order for nested loop to register change in isTimerRunning to true
                    while (isTimerRunning) {
                        long elapsedTime = getElapsedTime();

                        // Status Indicator
//                        if (elapsedTime % skipDuration >= 0 && elapsedTime % skipDuration <= 5) { // print something every nth second
//                            if (!hasDisplayed) {
//                                statusArray = statusIndicator(statusArray, elapsedTime, totalDuration, getHasSoundTriggered());
//                                hasDisplayed = true;
//                            }
//                        }
                        if (lastDuration + skipDuration > elapsedTime) {
                            hasDisplayed = false;
                            lastDuration += skipDuration;
                        }

//                        // TODO: remove this?
//                        // Has a sound been triggered?
//                        if (SequenceTimer.getHasSoundTriggered()) {
//
//                        }
//
//                        // Has the final sound been played?
//                        if (SoundTask.getLastSoundDone()) {
//                            endTimer();
//                        }
                    }
                    if (killStatusThread) {
                        break;
                    }
                }
            }
        }.start();
    }

    @Override
    public String toString() {
        return "";
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        Sequence sequence = new Sequence();
        sequence.editInterval(0, (15*60*1000));
        sequence.addInterval(10*60*1000);

        SequenceTimer timer = new SequenceTimer(sequence);

        for (int index = 0; index < timer.getSoundSchedule().length; index++) {
            System.out.println("SOUND[play at: " + timer.getSoundSchedule()[index][0] + ", has sound played: " + timer.getSoundSchedule()[index][1]);
        }

    }
}
