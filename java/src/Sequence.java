import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Represents a Sequence object. This object will be used to set up a meditation session.
 *  - Essentially, a Sequence object will be an array of SubSequences.
 *  - The first SubSequence must use the primary sound
 *  - The last SubSequence must use the primary sound and be of length 0
 *  - Indexing arrays in this object follows the Java default (beginning at 0), so user interface must be formatted
 *      accordingly (by subtracting 1 before it is passed to any Sequence method)
 * @author Joseph Haley
 */
public class Sequence {
    // VARIABLES
    /** Array of SubSequences **/
    private ArrayList<SubSequence> subSequenceArray = new ArrayList<>();
    /** Sound Object for first and last SubSequences (the "Main" SubSequences) **/
    private Sound mainSound;
    /** Sound Object for intervening SubSequences **/
    private Sound secondarySound;

    // METHODS
    // *** Constructors
    /**
     * Empty constructor: Creates the default Sequence (just first and last SubSequences)
     */
    public Sequence() {
        subSequenceArray.add( new SubSequence(1800, true));
        subSequenceArray.add( new SubSequence(0, false));
        mainSound = new Sound(true);
        secondarySound = new Sound(false);
    }

    // *** Values methods

    /**
     * Gets the sound associated with the Main SubSequences (the first and last SubSequences)
     * @return The name of the sound that will be played
     */
    public String getMainSound() {
        return mainSound.getSoundName();
    }

    /**
     * Sets the sound name associated with the Main SubSequences
     * @param soundName The name of the sound (must be in the Sound.soundList)
     */
    public void setMainSound(String soundName) {
        mainSound.setSound(soundName);
    }

    /**
     * Gets the sound associated with the Secondary SubSequences
     * @return The name of the sound that will be played
     */
    public String getSecondarySound() {
        return secondarySound.getSoundName();
    }

    /**
     * Sets the sound name associated with the Secondary SubSequences
     * @param soundName The name of the sound (must be in the Sound.soundList)
     */
    public void setSecondarySound(String soundName) {
        secondarySound.setSound(soundName);
    }

    /**
     * Gets the full path associated with a sound's name from the master soundList
     * @param soundName The name of the sound
     * @return the full path associated with the sound's name
     */
    public String getPathFromSoundList(String soundName) {
        // we could get this from either the mainSound or the secondary Sound
        // all we want is the full path of a sound, which is always the same for every instance of a Sound object
        return mainSound.getPathFromSoundList(soundName);
    }

    /**
     * Gets the list of available sounds
     * @return A Set object of sound names
     */
    public String[] getSoundList() {
        return mainSound.getSoundList();
    }

    /**
     * Get the SubSequenceArray
     * @return The subSequenceArray
     */
    public ArrayList getSequenceArray() {
        return subSequenceArray;
    }

    /**
     * Get the number of SubSequences in the array
     * @return The number of SubSequences in the array
     */
    public int getNumSubSequences() {
        return subSequenceArray.size();
    }

    // *** Manipulation methods
    /**
     * Creates a new SubSequence and inserts it at index location in subSequenceArray
     * @param index The location in subSequenceArray Where the new SubSequence is to be added
     * @param duration The length of the new SubSequence
     */
    public void addSubSequence(int duration, int index) {
        subSequenceArray.add(index, new SubSequence(duration, false));
    }
    /**
     * Creates a new SubSequence and inserts it into the second-to-last spot
     * @param duration The duration of the new SubSequence
     */
    public void addSubSequence(int duration) {
        if (duration >= 0) {
            this.addSubSequence(duration, subSequenceArray.size() - 1);
        } else {
            System.out.println("Sequence Class Error: Duration must be a positive integer.");
        }
    }

    /**
     * Edit the duration of a particular SubSequence in the subSequenceArray
     *  - The last SubSequence in the array cannot be changed
     * @param index The index of the Subsequence in subSequenceArray that is being being edited
     * @param newDuration The new duration of the SubSequence
     */
    public void editSubSequence (int index, int newDuration) {
        if (index == 0 || index == subSequenceArray.size() - 1) {
            System.out.println("Sequence Class Error: You can't edit the first or last SubSequence.");
        } else if (index < 0 || index >= subSequenceArray.size()){
            System.out.println("Sequence Class Error: Index out of range.");
        } else {
            subSequenceArray.get(index).setDuration(newDuration);
        }
    }

    /**
     * Swap two SubSequences' positions
     * @param index1 One of the SubSequences to be swapped
     * @param index2 One of the SubSequences to be swapped
     */
    public void swapSubSequences (int index1, int index2) {
        if (    index1 == 0 ||
                index1 == subSequenceArray.size() - 1 ||
                index2 == 0 ||
                index2 == subSequenceArray.size() - 1 ) {
            System.out.println("Sequence Class Error: You can't use swap with the first or last SubSequence.");
        } else if ( index1 >= subSequenceArray.size() ||
                    index2 >= subSequenceArray.size() ) {
            System.out.println("Sequence Class Error: One or more indexes are out of range.");
        } else {
            Collections.swap(subSequenceArray, index1, index2);
        }
    }

    /**
     * Deletes a SubSequence from the subSequenceArray
     *  - The first or last SubSequence cannot be deleted
     * @param subSequence The index of the Subsequence in subSequenceArray that is being being removed
     */
    public void deleteSubSequence(int subSequence) {
        if (subSequence < 0 || subSequence >= subSequenceArray.size()) {
            System.out.println("Sequence Class Error: Index out of range.");
        } else if (subSequence == 0 || (subSequence == subSequenceArray.size() - 1)){
            System.out.println("Sequence Class Error: You can't remove the first or last SubSequence.");
        } else {
            subSequenceArray.remove(subSequence);
        }

    }

    // *** Utility methods
    /**
     * Returns a visual representation of a Sequence object in a String
     * @return Visual representation of the Sequence (in String form)
     */
    public String printSequenceLine () {
        String line1 = "| Your session:\t\t";
        String line2 = "| SubSequence #:\t";
        /** ArrayList of SubSequence objects **/
        ArrayList<SubSequence> subSequenceArrayList = new ArrayList<>(this.getSequenceArray());
        /** Convert ArrayList into an Array **/
        SubSequence[] subSequenceArray = subSequenceArrayList.toArray(new SubSequence[subSequenceArrayList.size()]);

        int counter = 1;
        for (SubSequence subSequence: subSequenceArray) { // iterate through each member of the array
            int duration = subSequence.getDuration(); // the SubSequence's duration
            String dashes = new String(new char[(duration / 60)]).replace("\0", "-");
            String spaces = new String(new char[(duration / 60)]).replace("\0", " ");

            if (subSequence.getIsFirstOrLast()) {
                line1 += "O" + dashes;
            } else{
                line1 += "o" + dashes;
            }
            line2 += counter + spaces;

            counter++;

        }
        return line1 + "\n" + line2;
    }
    /**
     * toString method
     * @return Returns a String of the Object's variables
     */
    @Override
    public String toString() {
        String output = "Sequence Object:";
        for (SubSequence item: subSequenceArray) {
            output += "\n - " + item;
        }
        return output;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        /*
        Sequence my_sequence = new Sequence();
        my_sequence.addSubSequence(500);
        my_sequence.addSubSequence(15);
        my_sequence.addSubSequence(259);
        System.out.println(my_sequence);
        my_sequence.removeSubSequence(2);
        System.out.println(my_sequence);
        my_sequence.editSubSequence(4, 900);
        System.out.println(my_sequence);

         */
    }
}
