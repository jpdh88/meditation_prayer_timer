import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

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
    private static Sound mainSound;
    /** Sound Object for intervening Intervals **/
    private static Sound intervalSound;

    // METHODS
    // *** Constructor(s)
    /**
     * Empty constructor: Creates the default Sequence (just first and last Intervals)
     *  Time format: 15m * 60s * 1000ms = 15m
     */
    public Sequence() {
        intervalArrayList.add( new Interval(15, true));
        intervalArrayList.add( new Interval(0, true));
        mainSound = new Sound(true);
        intervalSound = new Sound(false);
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
        return intervalSound.getSoundName();
    }

    /**
     * Sets the sound name associated with the Secondary Intervals
     * @param soundName The name of the sound (must be in the Sound.soundList)
     */
    public void setSecondarySound(String soundName) {
        intervalSound.setSound(soundName);
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

    // *** Manipulation method(s)
    /**
     * Creates a new Interval object and inserts it at index location in intervalArrayList
     * @param index The location in intervalArrayList where the new Interval object is to be added
     * @param duration The length of the new Interval object in minutes
     */
    public void addInterval(double duration, int index) {
        intervalArrayList.add(index, new Interval(duration, false));
    }
    /**
     * Creates a new Interval object and inserts it at the second-to-last spot
     * @param duration The duration of the new Interval in minutes
     */
    public void addInterval(double duration) {
        if (duration >= 0) {
            this.addInterval(duration, intervalArrayList.size() - 1);
        } else {
            System.out.println("Sequence Class Error: Duration must be a positive integer.");
        }
    }

    /**
     * Edit the duration of a particular Interval in the intervalArrayList
     *  - The last Interval in the array cannot be changed
     * @param index The index of the Subsequence in intervalArrayList that is being being edited
     * @param newDuration The new duration of the Interval in milliseconds
     */
    public void editInterval (int index, int newDuration) {
        if (index == intervalArrayList.size() - 1) {
            System.out.println("Sequence Class Error: You can't edit that one.");
        } else if (index < 0 || index >= intervalArrayList.size()){
            System.out.println("Sequence Class Error: Index out of range.");
        } else {
            intervalArrayList.get(index).setDuration(newDuration);
        }
    }

    /**
     * Swap two Interval objects' positions
     * @param index1 One of the Interval objects to be swapped
     * @param index2 The other Interval object to be swapped
     */
    public void swapIntervals (int index1, int index2) {
        if (    index1 == 0 ||
                index1 == intervalArrayList.size() - 1 ||
                index2 == 0 ||
                index2 == intervalArrayList.size() - 1 ) {
            System.out.println("Sequence Class Error: You can't use swap with the first or last Interval.");
        } else if ( index1 >= intervalArrayList.size() ||
                    index2 >= intervalArrayList.size() ) {
            System.out.println("Sequence Class Error: One or more indexes are out of range.");
        } else {
            Collections.swap(intervalArrayList, index1, index2);
        }
    }

    /**
     * Deletes an Interval object from the intervalArrayList
     *  - The first or last Interval object cannot be deleted
     * @param interval The index of the Interval object in intervalArrayList that is being being removed
     */
    public void deleteInterval(int interval) {
        if (interval < 0 || interval >= intervalArrayList.size()) {
            System.out.println("Sequence Class Error: Index out of range.");
        } else if (interval == 0 || (interval == intervalArrayList.size() - 1)){
            System.out.println("Sequence Class Error: You can't remove the first or last Interval.");
        } else {
            intervalArrayList.remove(interval);
        }

    }

    // *** Utility method()
    /**
     * toString method
     * @return Returns a String of the Object's variables
     */

    @Override
    public String toString() {
        String output = "Sequence Object:";
        for (Interval item: intervalArrayList) {
            output += "\n - " + item;
        }
        return output;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        /*
        Sequence my_sequence = new Sequence();
        my_sequence.addInterval(500);
        my_sequence.addInterval(15);
        my_sequence.addInterval(259);
        System.out.println(my_sequence);
        my_sequence.removeInterval(2);
        System.out.println(my_sequence);
        my_sequence.editInterval(4, 900);
        System.out.println(my_sequence);

         */
    }
}
