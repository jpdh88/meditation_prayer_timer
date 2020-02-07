import java.util.ArrayList;

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

    // METHODS
    // *** Constructors
    /**
     * Empty constructor: Creates the default Sequence (just first and last SubSequences)
     */
    public Sequence() {
        subSequenceArray.add( new SubSequence("first"));
        subSequenceArray.add( new SubSequence("last"));
    }

    // *** Values methods

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
        subSequenceArray.add(index, new SubSequence(duration));
    }
    /**
     * Creates a new SubSequence and inserts it into the second-to-last spot
     * @param duration The duration of the new SubSequence
     */
    public void addSubSequence(int duration) {
        this.addSubSequence(duration, subSequenceArray.size() - 1);
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
            System.out.println("SubSequence Class Error: You can't edit that SubSequence");
        }
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

    // *** Utility methods
    /**
     * Returns a visual representation of a Sequence object in a String
     * @return Visual representation of the Sequence (in String form)
     */
    public String drawSequenceLine () {
        String line1 = "| Your session:\t\t";
        String line2 = "| SubSequence #:\t";
        /** ArrayList of SubSequence objects **/
        ArrayList<SubSequence> subSequenceArrayList = new ArrayList<>(this.getSequenceArray());
        /** Convert ArrayList into an Array **/
        SubSequence[] subSequenceArray = subSequenceArrayList.toArray(new SubSequence[subSequenceArrayList.size()]);

        int counter = 1;
        for (SubSequence item: subSequenceArray) { // iterate through each member of the array
            int sound = item.getSound(); // the SubSequence's sound
            int duration = item.getDuration(); // the SubSequence's duration
            String dashes = new String(new char[(duration / 60)]).replace("\0", "-");
            String spaces = new String(new char[(duration / 60)]).replace("\0", " ");

            switch(sound) {
                case 0:
                    line1 += "O" + dashes;
                    line2 += counter + spaces;
                    break;
                case 1:
                    line1 += "o" + dashes;
                    line2 += counter + spaces;
                    break;
            }
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
