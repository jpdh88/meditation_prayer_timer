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
    /** The Sound object associated w/ the SubSequence */
    Sound sound;

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create SubSequences
     */
    public SubSequence(int duration, String soundName) {
        this.duration = duration;
        this.sound = new Sound(soundName);
    }
    public SubSequence(int duration, boolean isFirstOrLast) {
        this.isFirstOrLast = isFirstOrLast;
        this.duration = duration;
        if (isFirstOrLast == true) { // is a first or last SubSequence
            this.sound = new Sound(true);
        } else { // SubSequence is an intervening subsequence
                this.sound = new Sound(false);
        }
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
     * Set the sound to be used at the beginning of the sequence
     *
     * @param soundName The name of the sound (must correspond to a sound in the Sound class)
     */
    public void setSound(String soundName) {
        sound.setSound(soundName);
    }

    /**
     * Get the sound to be used at the beginning of the sequence
     *
     * @return The name of the sound
     */
    public String getSound() {
        return sound.getSoundName();
    }

    // *** Utility methods
    @Override
    public String toString() {
        return  "dur: " + String.format("%6d", duration) +
                "\t\tsound: " + sound;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        SubSequence ss1 = new SubSequence(1800, true);
        System.out.println(ss1);

        SubSequence ss2 = new SubSequence(50, false);
        System.out.println(ss2);

        SubSequence ss3 = new SubSequence(500, "Birds");
        System.out.println(ss3);
    }
}