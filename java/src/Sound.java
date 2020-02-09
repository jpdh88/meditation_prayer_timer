import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Sound object. A Sound object stores the sound to be used in a SubSequence, information related to that
 * sound, a list of all the available sounds, etc.
 *
 * @author Joseph Haley
 */
public class Sound {
    // VARIABLES
    /** Directory where all the sounds are located **/
    private String soundsDir = "src/resources/sounds/";
    /** List of sounds available to the user **/
    private Map<String, String> soundList = new HashMap<String, String>(); // this map is populated in the Constructor
    /** The name of the sound chosen for this Sound object **/
    private String soundName; // represents a key in the soundList Map
    /** The AudioInputStream object of the chosen sound **/
    private AudioInputStream soundStream;
    /** The AudioFormat object of the chosen sound **/
    private AudioFormat soundStreamFormat; // for getting information about the sound file


    // METHODS
    // *** Constructors

    /**
     * Helper method for Constructors
     */
    private void populateSoundList() {
        soundList.put("Default Low", soundsDir + "meditation_bell_low.wav");
        soundList.put("Default High", soundsDir + "meditation_bell_high.wav");
        soundList.put("Church Bell 1", soundsDir + "church_bell_low.wav");
        soundList.put("Church Bell 2", soundsDir + "church_bell_high.wav");
        soundList.put("Church Bell 3", soundsDir + "church_bell_low_2.wav");
        soundList.put("Church Bells", soundsDir + "pealing_bells.wav");
        soundList.put("Birds", soundsDir + "birds.wav");
        soundList.put("Seashore", soundsDir + "seashore.wav");
        soundList.put("Cafe", soundsDir + "cafe.wav");
        soundList.put("Gibberish", soundsDir + "gibberish.wav");
    }
    /**
     * Constructor method when a soundName is passed to it
     * @param soundName the name of the sound chosen for this Sound object (must be one of the keys in the soundList Map
     */
    public Sound(String soundName) {
        populateSoundList();
        setSound(soundName);
    }

    /**
     * Constructor method for when defaults are set up
     * @param isFirstOrLast Whether the Sound object will be associated with a Main or a Secondary SubSequence
     */
    public Sound(boolean isFirstOrLast) {
        populateSoundList();
        if (isFirstOrLast == true) { // is a first or last SubSequence
            setSound("Default Low");
        } else { // is an intervening SubSequence
            setSound("Default High");
        }
    }

    // *** Values Methods
    /**
     * Sets the chosen sound
     */
    public void setSound(String soundName) {
        if (soundList.containsKey(soundName)) {
            setSoundName(soundName);
            try {
                setSoundStream(soundName);
            } catch (Exception e) {
                System.out.println("Sound Class Exception in setSound method\n\t" + e);
            }
        } else {
            System.out.println("Sound Class Error: that sound does not exist.");
        }
    }

    /**
     * Sets the soundName according to the sound chosen by setSound
     * @param soundName The name of the sound file chosen
     */
    private void setSoundName(String soundName) {
        if (soundList.containsKey(soundName)) {
            this.soundName = soundName;
        } else {
            System.out.println("Sound Class Error: that sound name does not exist in the list of sounds.");
        }
    }
    /**
     * Gets the chosen sound's name
     * @return The name of the chosen sound
     */
    public String getSoundName() {
        return soundName;
    }
    /**
     * Sets the soundStream according to the soundName given
     * @param soundName
     */
    private void setSoundStream(String soundName) throws Exception {
        try {
            File soundFile = new File(soundList.get(soundName));
            soundStream = AudioSystem.getAudioInputStream(soundFile);
            soundStreamFormat = soundStream.getFormat();
        } catch (Exception e) {
            System.out.println("Sound Class Exception in setSoundStream method\n\t" + e);
        }
    }

    /**
     * Gets the soundStream
     * @return the soundStream AudioInputStream object
     */
    public AudioInputStream getSoundStream() {
        return soundStream;
    }
    /**
     * Get the names of the available sounds
     * @return A Set containing the names of available sounds
     */
    public String[] getSoundList() {
        Set<String> soundListSet = soundList.keySet();
        String[] soundListArray = new String[soundListSet.size()];
        Arrays.sort(soundListArray);

        return soundListSet.toArray(soundListArray);
    }

    /**
     * Gets the duration of the chosen sound
     * @return Duration of the sound in seconds
     */
    public double getSoundDuration() {
        long frames = soundStream.getFrameLength();
        double soundDuration = (frames+0.0) / soundStreamFormat.getFrameRate();

        return soundDuration;
    }

    // *** Utility Methods
    @Override
    public String toString() {
        return "Sound Object: " + soundsDir + soundName;
    }

    /**
     * MAIN FUNCTION FOR TESTING
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("---------------TESTING----------------");
        Sound mySound = new Sound("Birds");
        System.out.println(mySound.getSoundList());
        System.out.println(mySound.getSoundName().toString());
        System.out.println(mySound.getSoundStream());
        System.out.println(mySound.getSoundDuration());
    }
}
