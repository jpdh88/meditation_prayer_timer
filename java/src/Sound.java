import javax.sound.sampled.*;
import javax.swing.*;
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
            setSound("Meditation Bell 1");
        } else { // is an intervening SubSequence
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
     * @param soundName The name of the sound (from the soundList)
     */
    private void setSoundStream(String soundName) throws Exception {
        try {
            soundStream = Sound.createSoundStream(soundList.get(soundName));
            soundStreamFormat = soundStream.getFormat();
        } catch (Exception e) {
            System.out.println("Sound Class Error: " + e);
        }
    }

    /**
     * Creates an AudioInputStream from a file path
     * @param soundName The path of the sound (found in the soundList)
     * @return An AudioInputStream for the sound path passed to the function
     * @throws Exception
     */
    public static AudioInputStream createSoundStream(String soundName) throws Exception {
        AudioInputStream soundStream = null;
        try {
            File soundFile = new File(soundName);
            soundStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e) {
            System.out.println("Sound Class Exception in setSoundStream method\n\t" + e);
        }
        return soundStream;
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
        soundListSet.toArray(soundListArray);
        Arrays.sort(soundListArray);

        return soundListArray;
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

    /**
     * Gets the full path associated with a sound's name from the soundList
     * @param soundName The name of the sound
     * @return the full path associated with the sound's name
     */
    public String getPathFromSoundList(String soundName) {
        return soundList.get(soundName);
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
     * Plays an AudioInputStream (sound file)
     *  - From: https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
     *  - From: https://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
     *  - From: https://stackoverflow.com/tags/javasound/info
     *  - From: https://stackoverflow.com/questions/577724/trouble-playing-wav-in-java/577926#577926
     */
    public static synchronized void playSound(AudioInputStream soundStream) {
        try {
            class AudioListener implements LineListener {
                private boolean done = false;

                @Override
                public synchronized void update(LineEvent event) {
                    LineEvent.Type eventType = event.getType();
                    if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                        done = true;
                        System.out.println("HERE 1");
                        notifyAll();
                    }
                }

                public synchronized void waitUntilDone() throws InterruptedException {
                    while (!done) {
                        wait();
                        System.out.println("HERE 2");
                    }
                }
            }
            AudioListener listener = new AudioListener();
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(soundStream);
                try {
                    System.out.println("HERE 6");
                    clip.start();
                    System.out.println("HERE 7");
                    listener.waitUntilDone();
                    System.out.println("HERE 8");
                } finally {
                    clip.close();
                    System.out.println("HERE 3");
                }
            } finally {
                soundStream.close();
                System.out.println("HERE 4");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("HERE 5");
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
        System.out.println(mySound.getSoundStream());
        System.out.println(mySound.getSoundDuration());
        AudioInputStream mystream = mySound.getSoundStream();
        // mySound.playSound();
    }
}
