import javax.sound.sampled.AudioInputStream;
import java.util.*;

/**
 * A method for running a Sequence. Essentially, it parses a Sequence into TimerTasks which are scheduled in a Timer
 *  object running as a daemon.
 *
 * @author Joseph Haley
 *
 * TODO:
 *  1) output running status of timer to console
 *      - have another thread for this?
 *  2) ensure that when Timer tasks are finished the while loop checking for user input also ends
 *      - put the while loop in its own thread?
 *  3) a method to pause the timer
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
            Sound.playSound(sound);
            System.out.println("Sound played.");
        } catch (Exception e) {
            System.out.println("SoundTask Object Error: " + e);
        }
    }

}

public class test_Timer_UsingTimerObject_sub7 {
    // VARIABLES
    private long startTime; // in milliseconds

    private Sequence sequence;
    private Timer timer;

    // METHODS
    // *** Constructor(s)

    // *** Value Method(s)
    private void setStartTime() {
        this.startTime = System.currentTimeMillis();;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getElapsedTime() {
        return ( (new Date()).getTime() - startTime);
    }

    // *** Utility Method(s)
    public void createTimer(Sequence sequence) {
        this.sequence = sequence;
        timer = new Timer("Timer Daemon", true);
    }

    /**
     * Method that runs the timer. Essentially, it passes tasks to the Timer which starts running them.
     */
    public void runTimer() {
        // Get Sequence intervals into an Array of intervals
        /** ArrayList of Interval objects **/
        ArrayList<Interval> intervalArrayList = new ArrayList<>(sequence.getSequenceArray());
        /** Convert ArrayList into an Array **/
        Interval[] intervalArray = intervalArrayList.toArray(new Interval[intervalArrayList.size()]);

        String soundPath = "";
        long accruedDuration = 1L;
        for (int intervalIndex = 0; intervalIndex < intervalArray.length; intervalIndex++) {
            if (intervalIndex == 0) { // First interval (uses main sound)
                soundPath = Sound.getPathFromSoundList(sequence.getMainSound());

                this.setStartTime(); // "Start" the object's internal status clock (to check how long timer has run)
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
    public void endTimer() {
        timer.cancel();
    }
    public void pauseTimer() {

    }

    /**
     * Main Method: For testing
     * @param args
     */
    public static void main(String[] args) {
        test_Timer_UsingTimerObject_sub7 sequenceTimer = new test_Timer_UsingTimerObject_sub7();
        Sequence sequence = new Sequence();
        sequence.editInterval(0, 20);
        sequence.addInterval(30);
        sequence.addInterval(20);

        sequenceTimer.createTimer(sequence);
        sequenceTimer.runTimer();

        while (true) {
            Scanner keybIn = new Scanner(System.in);
            String userInput = keybIn.nextLine();

            if (userInput.equals("q")) { // exit condition is met
                sequenceTimer.endTimer();
                break;
            }
        }
    }

}