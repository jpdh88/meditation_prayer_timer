/**
 * https://www.geeksforgeeks.org/java-util-timer-class-java/
 */

/**
 * https://www.geeksforgeeks.org/java-util-timertask-class-java/
 * with synchronisation
 */

/**
 * https://docs.oracle.com/javase/7/docs/api/java/util/Timer.html#schedule(java.util.TimerTask,%20java.util.Date)
 */

/**
 * The general idea here:
 *  - To schedule one task: TIMER.schedule(task, delay before play sound--long)
 *  - What I have to do:
 *      .schedule(play Interval[0] sound, 0 delay)
 *      .schedule(play Interval[1] sound, Interval[0] delay)
 *      .schedule(play Interval[2] sound, Interval[1] delay)
 *      ...etc.
 *      .schedule(play Interval[last] sound, Interval[last - 1] delay)
 */

import javax.sound.sampled.AudioInputStream;
import java.util.*;

// too much going on: not running correctly
// going to try another technique

// same as sub4 but with Thread wrapper for playing a sound
public class test_Timer_UsingTimerObject_sub5 {

    public static class TimeDurationTask extends TimerTask {
        // VARIABLES
        /** Info: oneMin = 60s * 1000ms **/
        private int duration; // in milliseconds

        // METHODS
        // *** Constructors
        public TimeDurationTask (int duration) {this.duration = duration * 1000;}

        // *** Utility Methods
        public void run() {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            int seconds = 1;
            boolean hasDisplayed = false;
            int lastSecond = 0;
            int numDotsInRow = 0;
            final int maxDotsInRow = 4;
            while (elapsedTime < duration) {
                if (elapsedTime % 1000 == 0 && elapsedTime > 0) { // print something every second
                    if (hasDisplayed == false) {
                        if (numDotsInRow == maxDotsInRow) {
                            System.out.print("| (" + seconds + "s)\n");
                            numDotsInRow = 0;
                        } else {
                            System.out.print(".");
                            numDotsInRow++;
                        }
                        seconds++;
                        hasDisplayed = true;
                    }
                }
                if (lastSecond + 1000 == elapsedTime) {
                    hasDisplayed = false;
                    lastSecond += 1000;
                }
                elapsedTime = (new Date()).getTime() - startTime;
            }
        }
    }

    public static class PlaySoundTask extends TimerTask {
        // VARIABLES
        private AudioInputStream sound;

        // METHODS
        // *** Constructors
        /**
         * Constructor method: Creates a task that will play a sound
         * @param sound The sound that will be played
         */
        public PlaySoundTask (AudioInputStream sound) {
            this.sound = sound;
        }

        // *** Utility methods
        /**
         * The code that is run by the Timer
         */
        public void run() {
            // wrap sound in anonymous thread
            Thread soundThread = new Thread() {
                public void run(){
                    try {
                        System.out.println("<<O>>");
                        Sound.playSound(sound);
                    } catch (Exception e) {
                        System.out.println("EROROOPROROR");
                    }
                }
            };
            soundThread.start();
        }
    }

    public static void main(String[] args)
    {
        Scanner keybIn = new Scanner(System.in);
        Timer timer = new Timer();
        Sequence sequence = new Sequence();
        sequence.editInterval(0, 20);
        sequence.addInterval(30);
        sequence.addInterval(20);
        // Should be: Tone - 5s - Tone - 10s - Tone - 5s - Tone

        // Sequence to array of Intervals
        /** ArrayList of Interval objects **/
        ArrayList<Interval> intervalArrayList = new ArrayList<>(sequence.getSequenceArray());
        /** Convert ArrayList into an Array **/
        Interval[] intervalArray = intervalArrayList.toArray(new Interval[intervalArrayList.size()]);
        /**
         * TimerTask array = we will build tasks into this
         *  - General structure:
         *      task:   O: play Main sound
         *      task:   -: timer - length of sound (15s)
         *      task:   o: play Secondary sound
         *      task:   -: timer - length of sound (15s)
         *      task:   o: play Secondary sound
         *      task:   -: timer - length of sound (15s)
         *      etc...
         *      task:   O: play Main sound
         *  - Therefore, length of TimerTask array will be length(Interval array)*2 - 1
         */

        long accruedDuration = 10L; // to start things a tiny bit in the future
        String soundPath = "";
        // (we will take care of the final interval after the for loop--it has no duration)
        for (int intervalIndex = 0; intervalIndex < intervalArray.length; intervalIndex++) {
            try {
                if (intervalIndex > 0 && intervalIndex < intervalArray.length - 1) { // i.e. Interval is not first or last
                    soundPath = Sound.getPathFromSoundList(sequence.getIntervalSound());
                } else { // i.e. Interval is a first or last interval (uses Main sound)
                    soundPath = Sound.getPathFromSoundList(sequence.getMainSound());
                }
                // TimerTask to play sound
                timer.schedule(new PlaySoundTask(Sound.createSoundStream(soundPath)), accruedDuration);
                // TimerTask to time a duration
                if (intervalIndex != intervalArray.length - 1) { // no TimeDurationTask for final sound
                    timer.schedule(new TimeDurationTask(intervalArray[intervalIndex].getDuration()), accruedDuration);
                    System.out.println("hit");
                }
            } catch (Exception e) {
                System.out.println("Problem 1");
            }
            System.out.println("soundPath at index " + intervalIndex + ": " + soundPath);
            System.out.println("\tDuration: " + (accruedDuration - 10));

            accruedDuration += intervalArray[intervalIndex].getDuration();
        }

        while (true) {
        }
    }
}
