import java.util.ArrayList;

/**
 * A command-line interface for creating Sequence Objects and running them
 */
public class InterfaceCommandLine {
    /**
     * Returns a visual representation of a Sequence object
     * @param sequence The Sequence to be output
     * @return Visual representation of the Sequence (in String form)
     */
    public static String drawSequence (Sequence sequence) {
        String output = "";
        /** ArrayList of SubSequence objects **/
        ArrayList<SubSequence> subSequenceArrayList = new ArrayList<>(sequence.getSequenceArray());
        /** Convert ArrayList into an Array **/
        SubSequence[] subSequenceArray = subSequenceArrayList.toArray(new SubSequence[subSequenceArrayList.size()]);

        for (SubSequence item: subSequenceArray) { // iterate through each member of the array
            int sound = item.getSound(); // the SubSequence's sound
            int duration = item.getDuration(); // the SubSequence's duration
            String dashes = new String(new char[(duration / 60)]).replace("\0", "-");

            switch(sound) {
                case 0:
                    output += "O" + dashes;
                    break;
                case 1:
                    output += "o" + dashes;
                    break;
            }

        }
        return output;
    }

    /**
     * Main method for testing this interface
     * @param args
     */
    public static void main(String[] args) {
        Sequence my_sequence = new Sequence();
        my_sequence.addSubSequence(600);
        my_sequence.addSubSequence(60);
        my_sequence.addSubSequence(300);
        //System.out.println(my_sequence);

        System.out.println(InterfaceCommandLine.drawSequence(my_sequence));
    }
}
