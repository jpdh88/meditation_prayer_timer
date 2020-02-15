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

// USING THIS METHOD FOR THE MEDITATION AND PRAYER TIMER !!!!!!!!!!

// CAN JUST SET isDaemon TO TRUE IN THE TIMER CONSTRUCTOR AND IT RUNS THE TIMER IN ANOTHER THREAD!!!!!!!
public class test_Timer_UsingTimerObject_sub6 {

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

            int seconds = 16;
            boolean hasDisplayed = false;
            long lastSecond = 0L;
            while (elapsedTime <= duration) {
                if (elapsedTime % 1000 == 0) { // print something every second
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
                System.out.println("Sound played.");
            } catch (Exception e) {
                System.out.println("EROROOPROROR");
            }
        }
    }

    public static void main(String[] args)
    {
        Scanner keybIn = new Scanner(System.in);
        Timer timer = new Timer(true);
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
        TimerTask[] timerTasks = new TimerTask[(intervalArray.length * 2) - 1];

        // Build an array of tasks to be performed by the timer
        //  - 1) first Interval (index 0 of array)
        //      Task: Play a sound
        try {
            String soundPath = Sound.getPathFromSoundList(sequence.getMainSound());
            timerTasks[0] = new PlaySoundTask(Sound.createSoundStream(soundPath));
        } catch (Exception e) {
            System.out.println("Problem 1");
        }
        //      Task: Duration of silence
        timerTasks[1] = new TimeDurationTask(intervalArray[0].getDuration() - Sound.clipDuration);

        //  - 2) middle intervals
        int intervalIndex;
        int taskIndex = 2;
        for (intervalIndex = 1; intervalIndex <= intervalArray.length - 2; intervalIndex++) {
            // Task: Play a sound
            try {
                String soundPath = Sound.getPathFromSoundList(sequence.getIntervalSound());
                timerTasks[taskIndex] = new PlaySoundTask(Sound.createSoundStream(soundPath));
            } catch (Exception e) {
                System.out.println("Problem 2");
            }
            // Task: Duration of silence
            timerTasks[taskIndex + 1] = new TimeDurationTask(intervalArray[intervalIndex].getDuration() - Sound.clipDuration);
            taskIndex += 2;
        }

        //  - 3) final interval
        try {
            String soundPath = Sound.getPathFromSoundList(sequence.getMainSound());
            timerTasks[timerTasks.length - 1] = new PlaySoundTask(Sound.createSoundStream(soundPath));
        } catch (Exception e) {
            System.out.println("Problem 3");
        }

        for (TimerTask task: timerTasks) {
            System.out.println("Tasks: " + task);
        }

        // Add the above tasks to the Timer with appropriate duration values
        System.out.println("Num timer tasks: " + timerTasks.length);
        long accruedDuration = 1L;
        intervalIndex = 0;
        for (taskIndex = 0; taskIndex < timerTasks.length - 1; taskIndex += 2) { // takes care of all but final task
            System.out.println("accruedDuration: " + accruedDuration);
            System.out.println("taskIndex: " + taskIndex);
            System.out.println("\ttwo tasks");
            timer.schedule(timerTasks[taskIndex], accruedDuration);
            timer.schedule(timerTasks[taskIndex + 1], accruedDuration + Sound.clipDuration);
            // we delay the second task by the length of the sound clip so that it only starts after the sound has played
            //  we can do this without losing total time because we subtracted the same amount when we built the task array

            accruedDuration += intervalArray[intervalIndex].getDuration();
            intervalIndex++;
        }
        System.out.println("accruedDuration: " + accruedDuration);
        timer.schedule(timerTasks[taskIndex], accruedDuration); // the final task

        // WORKS!!!!!
//        try {
//            Thread.sleep(6000);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        timer.cancel();

        // WORKS!!!!!!!!!
        boolean done = false;
        while (!done) {
            int exitNum = keybIn.nextInt();
            if (exitNum == 0) {
                done = true;
                timer.cancel();
            }
        }
    }
}
