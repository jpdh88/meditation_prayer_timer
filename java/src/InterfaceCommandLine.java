import java.util.ArrayList;
import java.util.Scanner;

/**
 * A command-line interface for creating Sequence Objects and running them
 *
 * @author Joseph Haley
 */
public class InterfaceCommandLine {
    // The default Sequence
    private static Sequence userSequence = new Sequence();
    // For user input with the keyboard
    private static Scanner keybIn = new Scanner(System.in);
    private static int userInput;
    private static int userInput2;

    public static void editSequence() {
        boolean doneLvl2 = false;
        while (!doneLvl2) {
            System.out.println("+--------------------------------------------------");
            System.out.println(userSequence.drawSequenceLine());
            System.out.println("+--------------------------------------------------");
            System.out.println("++ What would you like to do?");
            System.out.println("\t1) Add a SubSequence to the end");
            System.out.println("\t2) Add a SubSequence at a location");
            System.out.println("\t3) Edit a SubSequence");
            System.out.println("\t4) Swap two SubSequences");
            System.out.println("\t5) Delete a SubSequence");
            System.out.println("\t6) Change Primary Sound");
            System.out.println("\t7) Change Secondary Sound");
            System.out.println("\t0) Exit");
            userInput = keybIn.nextInt();

            switch (userInput) {
                case 0:
                    doneLvl2 = true;
                    break;
                case 1: // Add at end
                    System.out.println("+++ How many seconds do you want it to be?");
                    int duration = keybIn.nextInt();

                    userSequence.addSubSequence(duration);
                    System.out.println("[Sequence added.]");
                    break;
                case 2: // Add at location
                    boolean doneCase2 = false;
                    while (!doneCase2) {
                        System.out.println("+++ How many seconds do you want it to be?");
                        int duration2 = keybIn.nextInt();
                        System.out.println("+++ At what location?");
                        int location = keybIn.nextInt();

                        int numSubSequences = userSequence.getNumSubSequences();
                        switch (location) {
                            case 0:
                                doneCase2 = true;
                                break;
                            case 1:
                                System.out.println("[You can't add a sequence before the first one.]");
                                break;
                            default:
                                if (location > numSubSequences) {
                                    userSequence.addSubSequence(duration2);
                                } else {
                                    userSequence.addSubSequence(duration2, location - 1);
                                }
                                doneCase2 = true;
                        }
                    }
                    break;
                case 3: // Edit
                    boolean doneCase3 = false;
                    while (!doneCase3) {
                        System.out.println("+++ What SubSequence would you like to edit?");
                        int whichSubSequence = keybIn.nextInt();

                        int numSubSequences = userSequence.getNumSubSequences();
                        if (whichSubSequence == numSubSequences) {
                            System.out.println("You can't edit the final sequence.");
                        } else if (whichSubSequence == 0) {
                            doneCase3 = true;
                        } else if (whichSubSequence < 0 || whichSubSequence > numSubSequences) {
                            System.out.println("Invalid choice.");
                        } else {
                            System.out.println("+++ What new duration do you want to give it?");
                            int duration3 = keybIn.nextInt();

                            userSequence.editSubSequence(whichSubSequence - 1, duration3);
                            doneCase3 = true;
                        }
                    }
                    break;
                case 4: // Swap
                    boolean doneCase4 = false;
                    while (!doneCase4) {
                        System.out.println("+++ Which are the two SubSequences you would like to swap?");
                        int swapSequence1 = keybIn.nextInt();
                        int swapSequence2 = keybIn.nextInt();

                        if (swapSequence1 == 0 || swapSequence2 == 0) {
                            doneCase4 = true;
                        } else {
                            userSequence.swapSubSequences(swapSequence1 - 1, swapSequence2 - 1);
                            doneCase4 = true;
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Main method for this interface
     * @param args
     */
    public static void main(String[] args) {
        // Sentinel variable
        boolean doneProgram = false;

        while (!doneProgram) {
            System.out.println("+ What would you like to do? (Enter 0 at any time to exit)");
            System.out.println("\t1) See my session");
            System.out.println("\t2) Edit my session");
            int userChoice = keybIn.nextInt();

            switch (userChoice) {
                case 0:
                    doneProgram = true;
                    break;
                case 1:
                    System.out.println(userSequence.drawSequenceLine());
                    break;
                case 2:
                    editSequence();
                default:
                    System.out.println("[- Incorrect choice.]");
            }
        }

    }
}
