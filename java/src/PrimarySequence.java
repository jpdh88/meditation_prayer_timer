import java.time.LocalTime;

/**
 * Represents a PrimarySequence object.
 *
 * @author Joseph Haley
 */
public class PrimarySequence {
    // VARIABLES
    /** Duration of the sequence **/
    private int duration; // in seconds
    /** The sound that will start this sequence **/
    private String soundPath;

    // METHODS
    // *** Constructors

    /**
     * Default constructor
     */
    public PrimarySequence() {
        duration = 1800; // 30 minutes
        soundPath = "default sound"; // the default sound
    }
    /**
     * Partial constructor
     */
    public PrimarySequence(String soundPath) {
        this.duration = 1800; // 30 minutes
        this.soundPath = soundPath;
    }
    /**
     * Full constructor
     */
    public PrimarySequence(int duration, String soundPath) {
        this.duration = duration;
        this.soundPath = soundPath;
    }

    // *** Variable methods
    /**
     * Set the duration
     * @param duration Duration of the sound in seconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get the duration
     * @return Duration of the sound in seconds
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the sound to be used at the beginning of the sequence
     * @param soundPath Full path to the sound file
     */
    public void setSound(String soundPath) {
        this.soundPath = soundPath;
    }

    /**
     * Get the sound to be used at the beginning of the sequence
     * @return Full path to the sound file
     */
    public String getSound () {
        return soundPath;
    }
}
