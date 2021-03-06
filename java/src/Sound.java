import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Sound object. A Sound object stores the sound to be used in an Interval, information related to that
 * sound, a list of all the available sounds, etc.
 *
 * @author Joseph Haley
 */
public class Sound {
    // VARIABLES
    /** Directory where all the sounds are located **/
    private static String soundsDir = "src/resources/sounds/";
    /** List of sounds available to the user **/
    private static Map<String, String> soundList = new HashMap<String, String>(); // this map is populated in the Constructor
    /** The name of the sound chosen for this Sound object **/
    private String soundName; // represents a key in the soundList Map
    /** The AudioInputStream object of the chosen sound **/
    private AudioInputStream soundStream;
    /** The AudioFormat object of the chosen sound **/
    private AudioFormat soundStreamFormat; // for getting information about the sound file
    /** length of a sound clip **/
    public static final long CLIP_DURATION_MS = 15000; // in milliseconds


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
     * Plays a sound using JavaFX tools
     * @param soundName the name of the sound
     */
    public static void playSoundJFX(String soundName) {
        String musicFile = Sound.getPathFromSoundList(soundName);

        Media sound = new Media(new File(musicFile).toURI().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(sound);

        Thread soundThread = new Thread() {
            public void run() {
                mediaPlayer.play();
            }
        };

        soundThread.start();
    }

    /**
     * Plays an AudioInputStream (sound file)
     *  - From: https://stackoverflow.com/questions/577724/trouble-playing-wav-in-java/577926#577926
     *  Also looked at these:
     *      - From: https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
     *      - From: https://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
     *      - From: https://stackoverflow.com/tags/javasound/info
     *
     * @param audioInputStream The sound stream to be played
     */
    public static void playSound(AudioInputStream audioInputStream) {
        /**
         * Checks the status of a playing sound file for if it is playing or not
         */
        class AudioListener implements LineListener {

            // sentinel flag: has the sound stopped playing?
            private boolean done = false;

            // checks to see if the sound is still playing; if its not, it will set done = true
            @Override public synchronized void update(LineEvent event) {
                LineEvent.Type eventType = event.getType();
                if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }

            // constantly checks for a change in the sentinel flag to determine if the sound has finished
            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) { wait(); }
            }
        }

        // Start the anonymous thread
        Thread soundThread = new Thread() {
            public void run() {
                // Instantiate the Audio Listener
                AudioListener listener = new AudioListener();

                try {
                    Clip clip = null;
                    try {
                        clip = AudioSystem.getClip();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                    clip.addLineListener(listener);
                    try {
                        clip.open(audioInputStream);
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        clip.start(); // play the sound
                        try {
                            listener.waitUntilDone(); // start the Audio Listener
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        clip.close();
                    }
                } finally {
                    try {
                        audioInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
     * Gets the soundStream
     * @return the soundStream AudioInputStream object
     */
    public AudioInputStream getSoundStream() {
        return soundStream;
    }

    /**
     * Gets the duration of the chosen sound
     * @return Duration of the sound in milliseconds
     */
    public double getSoundDuration() {
        long frames = soundStream.getFrameLength();
        double soundDuration = (frames+0.0) / soundStreamFormat.getFrameRate();

        return soundDuration * 1000;
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
        System.out.println(mySound.getSoundStream());
        System.out.println(mySound.getSoundDuration());
        AudioInputStream mystream = mySound.getSoundStream();
        // mySound.playSound();
        System.out.println(Sound.getSoundIndex("Church Bell 2"));
    }
}
