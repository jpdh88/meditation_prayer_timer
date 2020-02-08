/**
 * Represents a SubSequence object.
 * @author Joseph Haley
 */
public class SubSequence {
    // VARIABLES
    /** Whether the SubSequence is first or last ( = True), or not ( = False) */
    private boolean isFirstOrLast;
    /** Duration of the SubSequence */
    private int duration; // in seconds

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create SubSequences
     */
    public SubSequence(int duration, boolean isFirstOrLast) {
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
     * Sets whether this SubSequence is a first or last / Main SubSequence
     * @param isFirstOrLast whether this SubSequence is a Main SubSequence or not
     */
    public void setIsFirstOrLast(boolean isFirstOrLast) {
        this.isFirstOrLast = isFirstOrLast;
    }

    /**
     * Gets whether this SubSequence is a first or last / Main SubSequence
     * @return whether this SubSequence is a Main SubSequence
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
        SubSequence ss1 = new SubSequence(1800, true);
        System.out.println(ss1);

        SubSequence ss2 = new SubSequence(50, false);
        System.out.println(ss2);

        SubSequence ss3 = new SubSequence(500, false);
        System.out.println(ss3);
    }
}