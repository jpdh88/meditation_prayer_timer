import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

/**
 * Represents a Sequence object. This object will be used to set up a meditation session.
 *  - Essentially, a Sequence object will be an array of Intervals.
 *  - The first Interval must use the primary sound
 *  - The last Interval must use the primary sound and be of length 0
 *  - Indexing arrays in this object follows the Java default (beginning at 0), so user interface must be formatted
 *      accordingly (by subtracting 1 before it is passed to any Sequence method)
 * @author Joseph Haley
 */
public class Sequence {
    // VARIABLES
    /** Array of Intervals **/
    private ArrayList<Interval> intervalArrayList = new ArrayList<>();
    /** Sound Object for first and last Intervals (the "Main" Intervals) **/
    private Sound mainSound;
    /** Sound Object for intervening Intervals **/
    private Sound secondarySound;
    /** Prefix and Suffix for .prof files **/
    private String prefix = "profile-";
    private String suffix = ".prof";
    /** The profiles available to the user (max. 11 profiles)**/
    private boolean[] isAProfile = new boolean[11]; // [0] is for default; user gets [1-10]; true means profile exists
    /** The default profile **/
    private int defaultProfile;

    // METHODS
    // *** Constructor(s)
    /**
     * Empty constructor: Creates the default Sequence (just first and last Intervals)
     *  Time format: 15m * 60s * 1000ms = 15m
     */
    public Sequence() {
        // Build the list of profiles
        rebuildProfiles();

        defaultProfile = getDefaultProfileConfig();

        // Load the default profile
        loadProgramDefaultProfile();

        // Overwrite whatever needs to be overwritten
        if (defaultProfile != 0) {
            loadProfile(defaultProfile);
        }

    }

    // *** Variable method(s)
    /**
     * Gets the sound associated with the Main Intervals (the first and last Intervals)
     * @return The name of the sound that will be played
     */
    public String getMainSoundName() {
        return mainSound.getSoundName();
    }

    /**
     * Sets the sound name associated with the Main Intervals
     * @param soundName The name of the sound (must be in the Sound.soundList)
     */
    public void setMainSound(String soundName) {
        mainSound.setSound(soundName);
    }

    /**
     * Gets the sound associated with the Secondary Intervals
     * @return The name of the sound that will be played
     */
    public String getSecondarySoundName() {
        return secondarySound.getSoundName();
    }

    /**
     * Sets the sound name associated with the Secondary Intervals
     * @param soundName The name of the sound (must be in the Sound.soundList)
     */
    public void setSecondarySound(String soundName) {
        secondarySound.setSound(soundName);
    }

    /**
     * Get an ordered array of Interval objects (an ARRAY, not an array list)
     * @return The ordered array of Interval objects
     */
    public Interval[] getSequenceArray() {
        return intervalArrayList.toArray(new Interval[intervalArrayList.size()]);
    }

    /**
     * Get the number of Interval objects in the array
     * @return The number of Interval objects in the array
     */
    public int getNumIntervals() {
        return intervalArrayList.size();
    }

    /**
     * Get the total duration of all intervals in the Sequence object
     * @return the total duration of all intervals in the Sequence object
     */
    public double getTotalDuration() {
        double totalDuration = 0;

        for (Interval eachInterval: getSequenceArray()) {
            totalDuration += eachInterval.getDuration();
        }

        return totalDuration;
    }
    /**
     * Gets the default profile
     * @return the default profile
     */
    public int getDefaultProfile() {
        return defaultProfile;
    }

    /**
     * Sets the default profile (also updates the configuration file)
     * @param index the new default profile
     */
    public void setDefaultProfile(int index) {
        defaultProfile = index;

        // We also update the config file
        setDefaultProfileConfig(index);
    }

    // *** Manipulation method(s)
    /**
     * Creates a new Interval object and inserts it at index location in intervalArrayList
     * @param index The location in intervalArrayList where the new Interval object is to be added
     * @param duration The length of the new Interval object in minutes
     */
    public void addInterval(double duration, int index) {
        intervalArrayList.add(index, new Interval(duration));
    }
    /**
     * Creates a new Interval object and inserts it at the last spot
     * @param duration The duration of the new Interval in minutes
     */
    public boolean addInterval(double duration) {
        boolean actionPerformed = false;

        if (duration >= 0) {
            this.addInterval(duration, intervalArrayList.size());
            actionPerformed = true;
        } else {
            System.out.println("Sequence Class Error: Duration must be a positive integer.");
        }

        return actionPerformed;
    }

    /**
     * Edit the duration of a particular Interval in the intervalArrayList
     *  - The last Interval in the array cannot be changed
     * @param index The index of the Subsequence in intervalArrayList that is being being edited
     * @param newDuration The new duration of the Interval in milliseconds
     * @return Whether the edit action was performed or not
     */
    public boolean editInterval (int index, int newDuration) {
        boolean actionPerformed = false;

        if (index < 0 || index >= intervalArrayList.size()){
            System.out.println("Sequence Class Error: Index out of range.");
        } else {
            intervalArrayList.get(index).setDuration(newDuration);
            actionPerformed = true;
        }

        return actionPerformed;
    }

    /**
     * Swap two Interval objects' positions
     * @param index1 One of the Interval objects to be swapped
     * @param index2 The other Interval object to be swapped
     * @return Whether the swap was performed or not
     */
    public boolean swapIntervals (int index1, int index2) {
        boolean actionPerformed = false;

        if ( index1 >= intervalArrayList.size() ||
                index2 >= intervalArrayList.size() ) {
            System.out.println("Sequence Class Error: One or more indexes are out of range.");
        } else {
            Collections.swap(intervalArrayList, index1, index2);
            actionPerformed = true;
        }

        return actionPerformed;
    }
    /**
     * Moves an Interval object left in the array
     * @param index the index of the Interval object
     * @return Whether the move was performed or not
     */
    public void moveIntervalLeft(int index) {
        swapIntervals(index, index - 1);
    }
    /**
     * Moves an Interval object right in the array
     * @param index the index of the Interval object
     * @return Whether the move was performed or not
     */
    public void moveIntervalRight(int index) {
        swapIntervals(index, index + 1);
    }

    /**
     * Deletes an Interval object from the intervalArrayList
     *  - The first or last Interval object cannot be deleted
     * @param interval The index of the Interval object in intervalArrayList that is being being removed
     * @return Whether the delete action was performed or not
     */
    public boolean deleteInterval(int interval) {
        boolean actionPerformed = false;

        if (interval < 0 || interval >= intervalArrayList.size()) {
            System.out.println("Sequence Class Error: Index out of range.");
        } else if (intervalArrayList.size() == 1) {
            System.out.println("Sequence Class Error: You need at least one Interval.");
        } else {
            intervalArrayList.remove(interval);
            actionPerformed = true;
        }

        return actionPerformed;
    }

    // *** Profile method(s)
    /**
     * Gets the default profile from the config.prof file
     * @return the default profile
     */
    public int getDefaultProfileConfig() {
        try (InputStream input = new FileInputStream("config.prof")) {

            Properties property = new Properties();

            // load a properties file
            property.load(input);

            // Is there a default profile?
            try {
                defaultProfile = Integer.parseInt(property.getProperty("default-profile"));
            } catch (Exception e) { // there is no default profile: use the program default
                defaultProfile = 0;
            }

        } catch (IOException io) {
            // Loading failed (for some reason)
            System.out.println("Sequence Class Error: problem reading file");
        }

        return defaultProfile;
    }
    /**
     * Sets the default profile in the config.prof file
     * @param index the new default profile
     */
    public void setDefaultProfileConfig(int index) {
        try (OutputStream output = new FileOutputStream("config.prof")) {

            Properties property = new Properties();

            // set the properties values
            property.setProperty("default-profile", index + "");

        } catch (IOException io) {
            System.out.println("Sequence Class Error: problem reading file");
        }
    }

    /**
     * Rebuilds the boolean array of isAProfile (to reflect any changes that might have been made)
     */
    public void rebuildProfiles() {
        // Determine whether profiles exist
        int profileNum = 1;

        do {
            try (InputStream input = new FileInputStream(prefix + profileNum + suffix)) {
                // This profile exists
                isAProfile[profileNum] = true;
            } catch (IOException io) {
                // This profile does not exist
                isAProfile[profileNum] = false;
            }
            profileNum++;
        } while (profileNum < isAProfile.length); // stop trying after a certain point (max # of profiles)
    }

    /**
     * Get a list of all the profiles currently exist (i.e. there is a .prof file for it)
     * @return a list of all the profiles currently exist (i.e. there is a .prof file for it)
     */
    public boolean[] getProfiles() {
        rebuildProfiles();

        return isAProfile;
    }

    /**
     * Get the date a profile was saved
     * @param index The index of the profile
     * @return The date the profile was saved
     */
    public String getProfileDateSaved(int index) {
        String profileDate = "";

        try (InputStream input = new FileInputStream(prefix + index + suffix)) {

            Properties property = new Properties();

            // load a properties file
            property.load(input);

            // get the date the profile was saved
            profileDate = property.getProperty("date_saved");

        } catch (IOException ex) {
            // Loading failed (for some reason)
            System.out.println("Sequence Class Error: problem reading file");
            ex.printStackTrace();
        }

        return profileDate;
    }

    /**
     * Saves a sequence to a profile
     * (Resource: https://mkyong.com/java/java-properties-file-examples/)
     * @param index The profile number that the sequence will be written to
     * @return Whether the profile was saved or not
     */
    public boolean saveProfile(int index) {
        boolean wasSaved = false;

        if (index > 0) { // Can't change the default profile
            try (OutputStream output = new FileOutputStream(prefix + index + suffix)) {

                Properties property = new Properties();

                // set the properties values
                Date todaysDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ww 'at' HH:mm");
                property.setProperty("date_saved", "" + dateFormat.format(todaysDate)); // the date this profile was saved
                property.setProperty("main_sound", mainSound.getSoundName()); // the main sound
                property.setProperty("secondary_sound", secondarySound.getSoundName()); // the secondary sound

                property.setProperty("num_intervals", Double.toString(intervalArrayList.size()));
                for (int intervalIndex = 0; intervalIndex < intervalArrayList.size(); intervalIndex++) {
                    property.setProperty("interval-" + intervalIndex, Double.toString(intervalArrayList.get(intervalIndex).getDuration()));
                }

                // save properties to project root folder
                property.store(output, null);

                wasSaved = true;
            } catch (IOException io) {
                System.out.println("Sequence Class Error: problem reading file");
            }
        }

        // rebuild the list of profiles
        rebuildProfiles();

        return wasSaved;
    }

    /**
     * Loads a sequence from a profile
     * @param index The number of the profile (corresponds to a position in the array returned by getProfileList)
     * @return Whether the profile was loaded or not
     */
    public boolean loadProfile(int index) {
        boolean wasLoaded = false;

        if (isAProfile[index]) {
            // This profile exists so we can load from it
            try (InputStream input = new FileInputStream(prefix + index + suffix)) {

                Properties property = new Properties();

                //load a properties file from class path, inside static method
                property.load(input);

                // Set the properties
                //  - sounds
                mainSound.setSound(property.getProperty("main_sound"));
                secondarySound.setSound(property.getProperty("secondary_sound"));
                //  - intervals
                int numIntervals = Integer.parseInt(property.getProperty("num_intervals"));
                intervalArrayList.clear(); // clear the list of intervals
                for (int intervalIndex = 0; intervalIndex < numIntervals; intervalIndex++) {
                    double duration = Double.parseDouble(property.getProperty("interval-" + intervalIndex));
                    intervalArrayList.add(intervalIndex, new Interval(duration));
                }

                wasLoaded = true;
            } catch (IOException ex) {
                // Loading failed (for some reason)
                System.out.println("Sequence Class Error: problem reading file");
                ex.printStackTrace();
            }
        } else if (index == 0) {
            loadProgramDefaultProfile();
        }

        return wasLoaded;
    }

    /**
     * Loads the default sequence with the default values (i.e. turns it into a new sequence)
     * @return Whether the profile was loaded or not (I'm assuming that this can't fail)
     */
    public boolean loadProgramDefaultProfile() {
        intervalArrayList.clear(); // clear the list of intervals
        intervalArrayList.add( new Interval(30));
        mainSound = new Sound(true);
        secondarySound = new Sound(false);

        return true;
    }

    /**
     * Delete a profile from the disk
     * @param index The profile to be deleted
     * @return Whether the profile was deleted or not
     */
    public boolean deleteProfile(int index) {
        boolean wasDeleted = false;

        if (index != 0) { // Can't delete the first profile (it doesn't exist / its the default)
            File fileToDelete = new File(prefix + index + suffix);

            if (fileToDelete.delete()) {
                wasDeleted = true;
            } else {
                System.out.println("Sequence Class Error: file not deleted (might not exist)");
            }

            // rebuild the list of profiles
            rebuildProfiles();
        }
        return wasDeleted;
    }

    // *** Utility method(s)
    /**
     * Plays the main sound
     */
    public void playMainSound() {
            Sound.playSound(mainSound.getSoundStream());
    }
    /**
     * Plays the secondary sound
     */
    public void playSecondarySound() {
            Sound.playSound(secondarySound.getSoundStream());
    }
    /**
     * Plays the main sound using JavaFX tools
     */
    public void playMainSoundJFX() {
        Sound.playSoundJFX(mainSound.getSoundName());
    }
    /**
     * Plays the secondary sound using JavaFX tools
     */
    public void playSecondarySoundJFX() {
        Sound.playSoundJFX(secondarySound.getSoundName());
    }

    /**
     * toString method
     * @return Returns a String of the Object's variables
     */
    @Override
    public String toString() {
        String output = "Sequence Object:";
        output += "\n - mainSound = " + mainSound.getSoundName();
        output += "\n - secondarySound = " + secondarySound.getSoundName();
        output += "\n - Intervals:";
        for (Interval item: intervalArrayList) {
            output += "\n\t- " + item;
        }
        return output;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        Sequence sequence = new Sequence();

        for (Interval each: sequence.getSequenceArray()) {
            System.out.println(each);
        }

//        sequence.addInterval(30);
//        sequence.addInterval(15);
//        sequence.addInterval(12);
//        sequence.addInterval(40);
//
//        for (boolean each: sequence.getProfiles()) {
//            System.out.println(each);
//        }
//
//        // sequence.saveSequenceToFile(1);
//        sequence.saveProfile(8);
//        sequence.loadProfile(2);
//        System.out.println(sequence);
//        sequence.loadProgramDefaultProfile();
//        System.out.println(sequence);
//
//        for (boolean each: sequence.getProfiles()) {
//            System.out.println(each);
//        }

//        for (String each: sequence.getProfiles()) {
//            System.out.println(each);
//        }
    }
}
