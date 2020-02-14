import java.util.Date;

/**
 * TimerTechnique1: runs a timer without using Timer object
 *
 *  Adapted from second answer here: https://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
 */
public class test_Timer_UsingLoopAndComparison {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        int twoMins = 2*60*1000;

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

        //Throw your exception
    }
}
