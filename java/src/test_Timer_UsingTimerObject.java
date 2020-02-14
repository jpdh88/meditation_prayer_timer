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
 *      .schedule(play SubSequence[0] sound, 0 delay)
 *      .schedule(play SubSequence[1] sound, SubSequence[0] delay)
 *      .schedule(play SubSequence[2] sound, SubSequence[1] delay)
 *      ...etc.
 *      .schedule(play SubSequence[last] sound, SubSequence[last - 1] delay)
 */

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class test_Timer_UsingTimerObject {

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

        // Utility methods
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
        sequence.editSubSequence(0, 5);
        sequence.addSubSequence(10);
        sequence.addSubSequence(5);
        // Should be: Tone - 5s - Tone - 10s - Tone - 5s - Tone

        // Sequence to array of SubSequences
        /** ArrayList of SubSequence objects **/
        ArrayList<SubSequence> subSequenceArrayList = new ArrayList<>(sequence.getSequenceArray());
        /** Convert ArrayList into an Array **/
        SubSequence[] subSequenceArray = subSequenceArrayList.toArray(new SubSequence[subSequenceArrayList.size()]);
        /** TimerTask array = we will build tasks into this **/
        TimerTask[] timerTasks = new TimerTask[subSequenceArray.length];

        // Create an array of tasks to be performed by the timer
        int sSIndex = 0;
        for (SubSequence subSequence: subSequenceArray) {
            // Play sound
            System.out.println("Counter: " + sSIndex);
            if (subSequence.getIsFirstOrLast()) {
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
                System.out.println("Duration before next sound: " + subSequenceArray[taskIndex].getDuration());

                taskIndex++;
                accruedDuration += (long) subSequenceArray[taskIndex].getDuration() + 1; // add 1 so its never 0 (produces an error)
            }
        }
    }
}
