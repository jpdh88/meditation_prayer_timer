import java.util.ArrayList;

/**
 * Represents a Sequence object. This object will be used to set up a meditation session.
 *  - Essentially, a Sequence object will be an array of SubSequences.
 *  - The first SubSequence must use the primary sound
 *  - The last SubSequence must use the primary sound and be of length 0
 * @author Joseph Haley
 */
public class Sequence {
    // VARIABLES
    /** Array of SubSequences **/
    private ArrayList<SubSequence> subSequenceArray = new ArrayList<>();

    // METHODS
    // *** Constructors
    /**
     * Empty constructor: Creates the default Sequence (just first and last SubSequences)
     */
    public Sequence() {
        subSequenceArray.add( new SubSequence("first"));
        subSequenceArray.add( new SubSequence("last"));
    }

    // *** Variable methods

    /**
     * Get the SubSequenceArray
     * @return The subSequenceArray
     */
    public ArrayList getSequenceArray() {
        return subSequenceArray;
    }
    /**
     * Creates a new SubSequence and inserts it at index location in subSequenceArray
     * @param index The location in subSequenceArray Where the new SubSequence is to be added
     * @param duration The length of the new SubSequence
     */
    public void addSubSequence(int index, int duration) {
        subSequenceArray.add(index, new SubSequence(duration));
    }
    /**
     * Creates a new SubSequence and inserts it into the second-to-last spot
     * @param duration The duration of the new SubSequence
     */
    public void addSubSequence(int duration) {
        this.addSubSequence(subSequenceArray.size() - 1, duration);
    }

    /**
     * Deletes a SubSequence from the subSequenceArray
     *  - The first or last SubSequence cannot be deleted
     * @param subSequence The index of the Subsequence in subSequenceArray that is being being removed
     */
    public void removeSubSequence(int subSequence) {
        if (subSequence >= subSequenceArray.size() - 1) {
            System.out.println("Invalid index.");
        } else {
            subSequenceArray.remove(subSequence);
        }

    }

    /**
     * Edit the duration of a particular SubSequence in the subSequenceArray
     *  - The last SubSequence in the array cannot be changed
     * @param index The index of the Subsequence in subSequenceArray that is being being edited
     * @param newDuration The new duration of the SubSequence
     */
    public void editSubSequence (int index, int newDuration) {
        if (index < subSequenceArray.size() - 1) {
            subSequenceArray.get(index).setDuration(newDuration);
        } else {
            System.out.println("You cannot edit that SubSequence");
        }
    }

    // *** Utility methods
    /**
     * toString method
     * @return A representation of the object in String form
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
