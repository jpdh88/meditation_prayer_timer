/**
 * Represents an Interval object.
 * @author Joseph Haley
 */
public class Interval {
    // VARIABLES
    /** Duration of the Interval */
    private double duration; // in minutes

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create Intervals
     */
    public Interval(double duration) {
        this.duration = duration;
    }

    // *** Variable methods
    /**
     * Set the duration
     *
     * @param duration Duration of the sequence in minutes
     */
    public void setDuration(double duration) {
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

    // *** Utility methods
    /**
     * Gets a string representation of the Interval object
     * @return a string representation of the Interval object
     */
    @Override
    public String toString() {
        return "Interval{" +
                "duration=" + duration +
                '}';
    }


    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {

    }
}