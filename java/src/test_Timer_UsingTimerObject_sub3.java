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

public class test_Timer_UsingTimerObject_sub3 {

    public static class SilenceTask extends TimerTask {
        // VARIABLES
        private int duration;

        // METHODS
        // *** Constructors
        public SilenceTask (int duration) {this.duration = duration;}

        // *** Utility Methods
        public void run() {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            int oneMin = 60*1000;

            int seconds = 0;
            boolean hasDisplayed = false;
            int lastSecond = 0;
            while (elapsedTime <= 5000) {
                if (elapsedTime % 1000 == 0) {
                    if (hasDisplayed == false) {
                        System.out.println(seconds);
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
            try {
                Sound.playSound(sound);
                System.out.println("test");
            } catch (Exception e) {
                System.out.println("EROROOPROROR");
            }
        }
    }

    public static void main(String[] args)
    {
        Scanner keybIn = new Scanner(System.in);
        Timer timer = new Timer();
        Sequence sequence = new Sequence();
        sequence.editInterval(0, 5);
        sequence.addInterval(10);
        sequence.addInterval(5);
        // Should be: Tone - 5s - Tone - 10s - Tone - 5s - Tone

        // Sequence to array of Intervals
        /** ArrayList of Interval objects **/
        ArrayList<Interval> intervalArrayList = new ArrayList<>(sequence.getSequenceArray());
        /** Convert ArrayList into an Array **/
        Interval[] intervalArray = intervalArrayList.toArray(new Interval[intervalArrayList.size()]);
        /** TimerTask array = we will build tasks into this **/
        TimerTask[] timerTasks = new TimerTask[intervalArray.length];

        // Create an array of tasks to be performed by the timer
        int sSIndex = 0;
        for (Interval interval : intervalArray) {
            // Play sound
            System.out.println("Counter: " + sSIndex);
            if (interval.getIsFirstOrLast()) {
                try {
                    String soundPath = Sound.getPathFromSoundList(sequence.getMainSound());
                    System.out.println(soundPath);
                    timerTasks[sSIndex] = new PlaySoundTask(Sound.createSoundStream(soundPath));
                } catch (Exception e) {
                    System.out.println("HERE 1");
                }
            } else {
                try {
                    String soundPath = Sound.getPathFromSoundList(sequence.getIntervalSound());
                    System.out.println(soundPath);
                    timerTasks[sSIndex] = new PlaySoundTask(Sound.createSoundStream(soundPath));
                } catch (Exception e) {
                    System.out.println("HERE 2");
                }
            }
            sSIndex++;
        }

        System.out.println("Num timer tasks: " + timerTasks.length);
        int taskIndex = 0;
        long accruedDuration = 1L;
        for (TimerTask task: timerTasks) {
            timer.schedule(task, accruedDuration);
            if (taskIndex < timerTasks.length - 1) {
                System.out.println("Duration before next sound: " + intervalArray[taskIndex].getDuration());

                taskIndex++;
                accruedDuration += (long) intervalArray[taskIndex].getDuration() + 1; // add 1 so its never 0 (produces an error)
            }
        }
    }
}
