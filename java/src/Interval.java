/**
 * Represents an Interval object.
 * @author Joseph Haley
 */
public class Interval {
    // VARIABLES
    /** Whether the Interval is first or last ( = True), or not ( = False) */
    private boolean isFirstOrLast;
    /** Duration of the Interval */
    private double duration; // in minutes

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create Intervals
     */
    public Interval(double duration, boolean isFirstOrLast) {
        this.isFirstOrLast = isFirstOrLast;
        this.duration = duration;
    }

    // *** Variable methods
    /**
     * Set the duration
     *
     * @param duration Duration of the sequence in minutes
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Get the duration
     *
     * @return Duration of the sound in minutes
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Gets whether this Interval is a first or last / Main Interval
     * @return whether this Interval is a Main Interval
     */
    public boolean getIsFirstOrLast() {
        return isFirstOrLast;
    }

    // *** Utility methods
    /**
     * Gets a string representation of the Interval object
     * @return a string representation of the Interval object
     */
    @Override
    public String toString() {
        return "Interval{" +
                "isFirstOrLast=" + isFirstOrLast +
                ", duration=" + duration +
                '}';
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        Interval ss1 = new Interval(20, true);
        System.out.println(ss1);

        Interval ss2 = new Interval(30, false);
        System.out.println(ss2);

        Interval ss3 = new Interval(1, false);
        System.out.println(ss3);
    }
}