/**
 * Represents a SubSequence object.
 */
public class SubSequence {
    // VARIABLES
    /**
     * Duration of the sequence
     **/
    private int duration; // in seconds
    /**
     * Primary sound is for first and last SubSequences
     **/
    private final String primarySoundPath = "primary sound";
    /**
     * Secondary sound is for intervening SubSequences
     **/
    private final String secondarySoundPath = "secondary sound";
    /**
     * The sound that will start this sequence
     **/
    private String soundPath;

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create the first or last SubSequence
     */
    public SubSequence(String firstOrLast) {
        switch (firstOrLast) {
            case "first":
               duration = 1800;
               soundPath = primarySoundPath; // a default SubSequence has a length of 30 minutes
               break;
            case "last":
                duration = 0;
                soundPath = primarySoundPath;
                break;
        }
    }

    /**
     * Constructor: Used to create intervening SubSequences
     */
    public SubSequence(int duration) {
        this.duration = duration;
        soundPath = secondarySoundPath;
    }

    // *** Variable methods

    /**
     * Set the duration
     *
     * @param duration Duration of the sound in seconds
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
     * @param soundPath Full path to the sound file
     */
    public void setSound(String soundPath) {
        this.soundPath = soundPath;
    }

    /**
     * Get the sound to be used at the beginning of the sequence
     *
     * @return Full path to the sound file
     */
    public String getSound() {
        return soundPath;
    }

    // *** Utility methods
    @Override
    public String toString() {
        return  "dur: " + String.format("%6d", duration) +
                "\t\tsoundPath: " + soundPath +
                "\t\t(defaultSoundPath: " + primarySoundPath +
                "\t\tsecondarySoundPath: " + secondarySoundPath + ")";
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        SubSequence ss1 = new SubSequence("first");
        System.out.println(ss1);

        SubSequence ss2 = new SubSequence("last");
        System.out.println(ss2);

        SubSequence ss3 = new SubSequence(50);
        System.out.println(ss3);
    }
}