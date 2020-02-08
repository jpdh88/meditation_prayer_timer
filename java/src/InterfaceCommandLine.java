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

    /**
     * Method for error messages: ensures a consistent format for error messages
     * @param message The error message to be displayed to the user
     * @return The error message to be displayed to the user
     */
    public static String errorMsg(String message) {
        return  "\toops[" +
                message +
                "]";
    }

    /**
     * Method for prompts: ensures a consistent format for prompt messages based on the menu level they're at
     * @param message The prompt message to be displayed to the user
     * @param level The level of the menu that the prompt is in
     * @return The formatted prompt message
     */
    public static String prompt(String message, int level) {
        final String levelSymbol = "+";
        String levelIndicator = "";

        switch (level) {
            case 1:
                levelIndicator = levelSymbol;
                break;
            case 2:
                levelIndicator = levelSymbol + levelSymbol;
                break;
            case 3:
                levelIndicator = levelSymbol + levelSymbol + levelSymbol;
                break;
            case 4:
                levelIndicator = levelSymbol + levelSymbol + levelSymbol;
                break;
            default:
                System.out.println("InterfaceCommandLine error: programmer should add another level to prompt method");
        }
        return levelIndicator + " " + message;
    }

    /**
     * Method for editing a meditation session (i.e. a Sequence object)
     * Presents and controls the command line user interface
     */
    public static void editSequence() {
        boolean doneLvl2 = false;
        while (!doneLvl2) {
            System.out.println("+--------------------------------------------------");
            System.out.println(userSequence.drawSequenceLine());
            System.out.println("+--------------------------------------------------");
            System.out.println(prompt("What would you like to do?", 2));
            System.out.println("\t1) ADD a SubSequence to the end");
            System.out.println("\t2) ADD a SubSequence at a location");
            System.out.println("\t3) EDIT a SubSequence");
            System.out.println("\t4) SWAP two SubSequences");
            System.out.println("\t5) DELETE a SubSequence");
            System.out.println("\t6) Change Main Sounds");
            System.out.println("\t7) Change Secondary Sounds");
            System.out.println("\t0) Exit");
            int userInput = keybIn.nextInt();

            switch (userInput) {
                case 0:
                    doneLvl2 = true;
                    break;
                case 1: // Add at end
                    boolean doneCase1 = false;
                    while (!doneCase1) {
                        System.out.println(prompt("ADD - How many seconds do you want it to be?", 3));
                        int duration = keybIn.nextInt();

                        if (duration < 0) {
                            System.out.println(errorMsg("Duration must be 0 or greater."));
                        } else {
                            userSequence.addSubSequence(duration);
                            doneCase1 = true;
                        }
                    }
                    break;
                case 2: // Add at location
                    boolean doneCase2 = false; // sentinel value
                    while (!doneCase2) {
                        int numSubSequences = userSequence.getNumSubSequences();

                        System.out.println(prompt("ADD - How many seconds do you want it to be?", 3));
                        int duration2 = keybIn.nextInt();

                        // If there are only two SubSequences in the Sequence, there is no need to ask for a location
                        int location;
                        if (numSubSequences == 2) {
                            location = 2; //
                        } else {
                            System.out.println(prompt("ADD - At what location?", 3));
                            location = keybIn.nextInt();
                        }

                        // Evaluate the inputted location
                        switch (location) {
                            case 0:
                                doneCase2 = true;
                                break;
                            case 1:
                                System.out.println(errorMsg("You can't add a sequence before the first one"));
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
                        System.out.println(prompt("EDIT - What SubSequence would you like to edit?", 3));
                        int whichSubSequence = keybIn.nextInt();

                        int numSubSequences = userSequence.getNumSubSequences();
                        if (whichSubSequence == numSubSequences) {
                            System.out.println(errorMsg("You can't edit the final sequence."));
                        } else if (whichSubSequence == 0) {
                            doneCase3 = true;
                        } else if (whichSubSequence < 0 || whichSubSequence > numSubSequences) {
                            System.out.println(errorMsg("Invalid choice."));
                        } else {
                            System.out.println(prompt("EDIT - What new duration do you want to give it?", 3));
                            int duration3 = keybIn.nextInt();

                            userSequence.editSubSequence(whichSubSequence - 1, duration3);
                            doneCase3 = true;
                        }
                    }
                    break;
                case 4: // Swap
                    switch (userSequence.getNumSubSequences()) {
                        case 2:
                        case 3:
                            System.out.println(errorMsg("There are not enough SubSequences for SWAP."));
                            break;
                        default:
                            boolean doneCase4 = false;
                            while (!doneCase4) {
                                System.out.println(prompt("SWAP - Which are the two SubSequences you would like to swap?", 3));
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
                    break;
                case 5: // DELETE
                    switch (userSequence.getNumSubSequences()) {
                        case 2:
                            System.out.println(errorMsg("There are no SubSequences to DELETE."));
                            break;
                        default:
                            boolean doneCase5 = false;
                            while (!doneCase5) {
                                System.out.println(prompt("DELETE - Which SubSequence would you like to delete?", 3));
                                int whichSubSequence = keybIn.nextInt();

                                if (whichSubSequence == 0) {
                                    doneCase5 = true;
                                } else if (whichSubSequence < 0 || whichSubSequence > userSequence.getNumSubSequences()) {
                                    System.out.println(errorMsg("Invalid choice."));
                                } else if (whichSubSequence == 1 || whichSubSequence == userSequence.getNumSubSequences()) {
                                    System.out.println(errorMsg("You can't remove the first or last sequence."));
                                } else {
                                    userSequence.deleteSubSequence(whichSubSequence - 1);
                                    doneCase5 = true;
                                }
                            }
                    }
                    break;
//                case 6:
//                    System.out.println("\tCurrent sound: " + userSequence.get);
//                    break;
            }
        }
    }

    /**
     * Main method for the Command Line interface
     * @param args
     */
    public static void main(String[] args) {
        // Sentinel variable
        boolean doneProgram = false;

        while (!doneProgram) {
            System.out.println(prompt("What would you like to do? (Enter 0 at any time to exit)", 1));
            System.out.println("\t1) See my session");
            System.out.println("\t2) Edit my session");
            int userChoice = keybIn.nextInt();

            switch (userChoice) {
                case 0:
                    doneProgram = true;
                    break;
                case 1:
                    System.out.println("+--------------------------------------------------");
                    System.out.println(userSequence.drawSequenceLine());
                    System.out.println("+--------------------------------------------------");
                    break;
                case 2:
                    editSequence();
                default:
                    System.out.println("[- Invalid choice.]");
            }
        }

    }
}
