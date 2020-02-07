/**
 * Represents a SubSequence object.
 * @author Joseph Haley
 */
public class SubSequence {
    // VARIABLES
    /**
     * Duration of the sequence
     **/
    private int duration; // in seconds
    /**
     * The sound associated w/ the SubSequence
     */
    private int sound; // represents index of array of file paths
    /**
     * Sounds that can be associated w/ the SubSequence
     **/
    String[] soundList = {"Primary sound", "Secondary sound", "other sound", "another sound"};

    // METHODS
    // *** Constructors
    /**
     * Constructor: Used to create the first or last SubSequence
     */
    public SubSequence(String firstOrLast) {
        switch (firstOrLast) {
            case "first":
               duration = 1800;
               sound = 0; // a default SubSequence has a length of 30 minutes
               break;
            case "last":
                duration = 0;
                sound = 0;
                break;
        }
    }

    /**
     * Constructor: Used to create intervening SubSequences
     */
    public SubSequence(int duration) {
        this.duration = duration;
        sound = 1;
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
     * @param sound Full path to the sound file
     */
    public void setSound(int sound) {
        this.sound = sound;
    }

    /**
     * Get the sound to be used at the beginning of the sequence
     *
     * @return Full path to the sound file
     */
    public int getSound() {
        return sound;
    }

    // *** Utility methods
    @Override
    public String toString() {
        return  "dur: " + String.format("%6d", duration) +
                "\t\tsound: " + sound +
                "\t\t(soundPath: " + soundList[sound] + ")";
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