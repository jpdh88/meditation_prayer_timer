import java.util.ArrayList;

/**
 * Represents a Sequence object. This object will be used to control a meditation session.
 */
public class Sequence {
    // VARIABLES
    ArrayList<SubSequence> subSequences = new ArrayList<SubSequence>();

    // METHODS
    // *** Constructors
    /**
     * Empty constructor: Creates a default sequence
     */
    public Sequence() {
        subSequences.add( new SubSequence("first"));
        subSequences.add( new SubSequence("last"));
    }

    // *** Variable methods
    public void addSubSequence(int duration) {
        subSequences.add(subSequences.size() - 1, new SubSequence(duration));
    }

    // *** Utility methods
    /**
     * to String method
     * @return A representation of the object in String form
     */
    @Override
    public String toString() {
        String output = "Sequence Object:";
        for (SubSequence item: subSequences) {
            output += "\n - " + item;
        }
        return output;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        Sequence my_sequence = new Sequence();
        my_sequence.addSubSequence(500);
        my_sequence.addSubSequence(100);
        System.out.println(my_sequence);
    }
}
