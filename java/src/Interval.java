/**
 * Represents an Interval object.
 * @author Joseph Haley
 */
public class Interval {
    // VARIABLES
    /** Whether the Interval is first or last ( = True), or not ( = False) */
    private boolean isFirstOrLast;
    /** Duration of the Interval */
    private int duration; // in seconds

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create Intervals
     */
    public Interval(int duration, boolean isFirstOrLast) {
        this.isFirstOrLast = isFirstOrLast;
        this.duration = duration;
    }

    // *** Variable methods

    /**
     * Set the duration
     *
     * @param duration Duration of the sequence in seconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get the duration
     *
     * @return Duration of the sound in seconds
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets whether this Interval is a first or last / Main Interval
     * @param isFirstOrLast whether this Interval is a Main Interval or not
     */
    public void setIsFirstOrLast(boolean isFirstOrLast) {
        this.isFirstOrLast = isFirstOrLast;
    }

    /**
     * Gets whether this Interval is a first or last / Main Interval
     * @return whether this Interval is a Main Interval
     */
    public boolean getIsFirstOrLast() {
        return isFirstOrLast;
    }

    // *** Utility methods
    @Override
    public String toString() {
        return  "dur: " + String.format("%6d", duration) +
                "isFirstOrLast:  " + isFirstOrLast;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        Interval ss1 = new Interval(1800, true);
        System.out.println(ss1);

        Interval ss2 = new Interval(50, false);
        System.out.println(ss2);

        Interval ss3 = new Interval(500, false);
        System.out.println(ss3);
    }
}