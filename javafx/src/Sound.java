import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Sound object. A Sound object stores the sound to be used in an Interval, information related to that
 * sound, a list of all the available sounds, etc.
 *
 * re: playing sounds (re: playing sounds (https://stackoverflow.com/questions/44348971/playing-sound-from-jar-file)
 *
 * @author Joseph Haley
 */
public class Sound {
    // VARIABLES
    /** Directory where all the sounds are located */
    private static String soundsDir = "resources/sounds/";
    /** List of sounds available to the user */
    private static Map<String, String> soundList = new HashMap<String, String>(); // this map is populated in the Constructor
    /** The name of the sound chosen for this Sound object */
    private String soundName; // represents a key in the soundList Map
    /** The AudioInputStream object of the chosen sound */
    private static AudioInputStream audioInputStream;
    /** The clip that will be played */
    private static Clip clip;


    // METHODS
    // *** Static methods
    /**
     * Creates list of available sounds
     */
    private static void populateSoundList() {
        soundList.put("Meditation Bell 1", soundsDir + "meditation_bell_low.wav");
        soundList.put("Meditation Bell 2", soundsDir + "meditation_bell_high.wav");
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
     * Get the names of the available sounds
     * @return An array of Strings containing the names of available sounds
     */
    public static String[] getSoundList() {
        Set<String> soundListSet = soundList.keySet();
        String[] soundListArray = new String[soundListSet.size()];
        soundListSet.toArray(soundListArray);
        Arrays.sort(soundListArray);

        return soundListArray;
    }

    /**
     * Gets the index location of a name of a sound
     * @param soundName The name of the sound
     * @return The index of the sound name
     */
    public static int getSoundIndex(String soundName) {
        String[] soundList = getSoundList();

        int index;
        for (index = 0; index < soundList.length; index++) {
            if (soundName.equals(soundList[index])) {
                // This is the index we want, so break
                break;
            }
        }

        return index;
    }

    /**
     * Gets the full path associated with a sound's name from the soundList
     * @param soundName The name of the sound
     * @return the full path associated with the sound's name
     */
    public static String getPathFromSoundList(String soundName) {
        return soundList.get(soundName);
    }

    /**
     * Plays a sound
     * @param soundName the name of the sound
     */
    public static void playSound(String soundName) {
        Thread soundThread = new Thread() {
            public void run() {
                // Get the audio from the file
                try {
                    // Convert the file path string to a URL
                    System.out.println(Sound.getPathFromSoundList(soundName));
                    URL sound = getClass().getResource(Sound.getPathFromSoundList(soundName));
                    System.out.println(sound);

                    // Get audio input stream from the file
                    audioInputStream = AudioSystem.getAudioInputStream(sound);

                    // Get clip resource
                    clip = AudioSystem.getClip();

                    // Open clip from audio input stream
                    clip.open(audioInputStream);
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    e.printStackTrace();
                }

                clip.start();
            }
        };
        soundThread.start();
    }

    // *** Constructors
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
     * @param isFirstOrLast Whether the Sound object will be associated with a Main or an Interval Interval
     */
    public Sound(boolean isFirstOrLast) {
        populateSoundList();
        if (isFirstOrLast == true) { // is a first or last Interval
            setSound("Meditation Bell 1");
        } else { // is an intervening Interval
            setSound("Meditation Bell 2");
        }
    }

    // *** Values Methods
    /**
     * Sets the chosen sound
     */
    public void setSound(String soundName) {
        if (soundList.containsKey(soundName)) {
            setSoundName(soundName);
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

    // *** Utility Methods

    /**
     * Returns a String with information about the Sound object
     * @return Information about the Sound object
     */
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
        Sound mySound = new Sound("Meditation Bell 1");
        System.out.println(mySound.getSoundList());
        System.out.println(mySound.getSoundName().toString());
//        System.out.println(mySound.getSoundStream());
//        System.out.println(mySound.getSoundDuration());
//        AudioInputStream mystream = mySound.getSoundStream();
        // mySound.playSound();
        System.out.println(Sound.getSoundIndex("Church Bell 2"));

        //System.out.println(mySound.directory);
    }
}
