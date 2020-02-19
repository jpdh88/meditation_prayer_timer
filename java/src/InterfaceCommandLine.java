import java.util.Scanner;

/**
 * A command-line interface for creating Sequence Objects and running them
 * Note: any interaction w/ user = seconds; any interaction w/ Objects = milliseconds
 *
 * @author Joseph Haley
 */
public class InterfaceCommandLine {
    // The default Sequence
    private static Sequence userSequence = new Sequence();
    // For user input with the keyboard
    private static Scanner keybIn = new Scanner(System.in);
    // Minimum length of an Interval
    private static int minIntervalLength = 30; // in seconds

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
     * Prints a numbered list of available sounds, for use in selecting items from this list
     *  - used in order to avoid repeated code
     * @param soundList The array of sounds to be displayed
     * @return A numbered list of available sounds in a String.
     */
    public static String getFormattedSoundList(String[] soundList) {
        int soundNum = 1; // Give each array item a number
        String getFormattedSoundList = "";
        for (String soundName: soundList) {
            if (soundName == userSequence.getMainSoundName() && soundName == userSequence.getSecondarySoundName()) {
                getFormattedSoundList += "\t" + String.format("%2d", soundNum) + ") " + soundName + " (Current Main and Interval Sound)" + "\n";
            } else if (soundName == userSequence.getMainSoundName()) {
                getFormattedSoundList += "\t" + String.format("%2d", soundNum) + ") " + soundName + " (Current Main Sound)" + "\n";
            } else if (soundName == userSequence.getSecondarySoundName()) {
                getFormattedSoundList += "\t" + String.format("%2d", soundNum) + ") " + soundName + " (Current Interval Sound)" + "\n";
            } else {
                getFormattedSoundList += "\t" + String.format("%2d", soundNum) + ") " + soundName + "\n";
            }
            soundNum++;
        }

        return getFormattedSoundList;
    }

    /**
     * Method for helping with selection of Main or Interval sound (using this saves having to duplicate the loop)
     * @param soundType Either "Main Sound" or "Interval Sound"
     * @return The name of the sound chosen
     */
    public static String selectSound(String soundType) {
        String[] soundList = Sound.getSoundList();

        // Print list of sounds
        System.out.println("\tSound List:");
        System.out.print(getFormattedSoundList(soundList));
        System.out.println("\t---");
        System.out.println("\t 0) Exit");

        // Selection loop
        String soundName = userSequence.getMainSoundName(); // initialize the choice with the current soundName
        boolean doneCase6 = false;
        while (!doneCase6) {
            System.out.println(prompt("Which sound do you want as the " + soundType + "?", 3));
            int soundChoice = keybIn.nextInt();

            if (soundChoice == 0) { // exit without making a choice
                doneCase6 = true;
            } else if (soundChoice > 0 && soundChoice <= soundList.length) {
                soundName = soundList[soundChoice - 1];
                doneCase6 = true;
            } else {
                System.out.println(errorMsg("That is not a valid option."));
            }
        }

        return soundName;
    }

    /**
     * Method for editing a meditation session (i.e. a Sequence object)
     * Presents and controls the command line user interface
     */
    public static void editSequence() {
        boolean doneLvl2 = false;
        while (!doneLvl2) {
            System.out.println("+--------------------------------------------------");
            System.out.println(userSequence.getSequenceLine());
            System.out.println("+--------------------------------------------------");
            System.out.println(prompt("What would you like to do?", 2));
            System.out.println("\t1) ADD an Interval to the end");
            System.out.println("\t2) ADD an Interval at a location");
            System.out.println("\t3) EDIT an Interval");
            System.out.println("\t4) SWAP two Intervals");
            System.out.println("\t5) DELETE an Interval");
            System.out.println("\t6) CHANGE Main Sound");
            System.out.println("\t7) CHANGE Interval Sound");
            System.out.println("\t8) PLAY a sound");
            System.out.println("\t---");
            System.out.println("\t0) Exit");
            int userInput = keybIn.nextInt();

            switch (userInput) {
                case 0:
                    doneLvl2 = true;
                    break;
                case 1: // Add at end
                    while (true) {
                        System.out.println(prompt("ADD - How many seconds do you want it to be?", 3));
                        int duration = keybIn.nextInt();

                        if (duration < minIntervalLength) {
                            System.out.println(errorMsg("Duration must be " + minIntervalLength + " seconds or greater."));
                        } else {
                            userSequence.addInterval(duration * 1000);
                            break;
                        }
                    }
                    break;
                case 2: // Add at location
                    boolean doneCase2 = false; // sentinel value
                    while (!doneCase2) {
                        int duration2;
                        int numIntervals = userSequence.getNumIntervals();

                        // Get and validate the number of seconds
                        while (true) {
                            System.out.println(prompt("ADD - How many seconds do you want it to be?", 3));
                            duration2 = keybIn.nextInt();

                            if (duration2 < minIntervalLength) {
                                System.out.println(errorMsg("Duration must be " + minIntervalLength + " seconds or greater."));
                            } else {
                                break;
                            }
                        }

                        // Get and validate the location of the Interval
                        int location;
                        if (numIntervals == 2) { // If there are only two Intervals in the Sequence,
                                                    //  there is no need to ask for a location
                            location = 2; //
                        } else {
                            while (true) {
                                System.out.println(prompt("ADD - At what location?", 3));
                                location = keybIn.nextInt();

                                if (location == 1) {
                                    System.out.println(errorMsg("You can't add a sequence before the first one"));
                                } else if (location < 0) {
                                    System.out.println(errorMsg("That's an invalid option."));
                                } else {
                                    break;
                                }
                            }
                        }

                        // Evaluate the inputted location
                        if (location == 0) {
                            break;
                        } else {
                            if (location > numIntervals) { // add new Interval to the end if location > number of Intervals
                                userSequence.addInterval(duration2 * 1000);
                            } else {
                                userSequence.addInterval(duration2 * 1000, location - 1);
                            }
                            break;
                        }
                    }
                    break;
                case 3: // Edit
                    while (true) {
                        System.out.println(prompt("EDIT - What Interval would you like to edit?", 3));
                        int whichInterval = keybIn.nextInt();

                        int numIntervals = userSequence.getNumIntervals();

                        if (whichInterval == numIntervals) {
                            System.out.println(errorMsg("You can't edit the final sequence."));
                        } else if (whichInterval == 0) {
                            break;
                        } else if (whichInterval < 0 || whichInterval > numIntervals) {
                            System.out.println(errorMsg("Invalid choice."));
                        } else {
                            System.out.println(prompt("EDIT - What new duration do you want to give it?", 3));
                            int duration3 = keybIn.nextInt();

                            userSequence.editInterval(whichInterval - 1, duration3 * 1000);
                            break;
                        }
                    }
                    break;
                case 4: // Swap
                    switch (userSequence.getNumIntervals()) {
                        case 2:
                        case 3:
                            System.out.println(errorMsg("There are not enough Intervals for SWAP."));
                            break;
                        default:
                            boolean doneCase4 = false;
                            while (!doneCase4) {
                                System.out.println(prompt("SWAP - Which are the two Intervals you would like to swap?", 3));
                                int swapSequence1 = keybIn.nextInt();
                                int swapSequence2 = keybIn.nextInt();

                                if (swapSequence1 == 0 || swapSequence2 == 0) {
                                    doneCase4 = true;
                                } else {
                                    userSequence.swapIntervals(swapSequence1 - 1, swapSequence2 - 1);
                                    doneCase4 = true;
                                }
                            }
                            break;
                    }
                    break;
                case 5: // DELETE
                    switch (userSequence.getNumIntervals()) {
                        case 2:
                            System.out.println(errorMsg("There are no Intervals to DELETE."));
                            break;
                        default:
                            boolean doneCase5 = false;
                            while (!doneCase5) {
                                System.out.println(prompt("DELETE - Which Interval would you like to delete?", 3));
                                int whichInterval = keybIn.nextInt();

                                if (whichInterval == 0) {
                                    doneCase5 = true;
                                } else if (whichInterval < 0 || whichInterval > userSequence.getNumIntervals()) {
                                    System.out.println(errorMsg("Invalid choice."));
                                } else if (whichInterval == 1 || whichInterval == userSequence.getNumIntervals()) {
                                    System.out.println(errorMsg("You can't remove the first or last sequence."));
                                } else {
                                    userSequence.deleteInterval(whichInterval - 1);
                                    doneCase5 = true;
                                }
                            }
                    }
                    break;
                case 6: // CHANGE the main sound
                    userSequence.setMainSound(selectSound(("Main Sound")));
                    break;
                case 7: // CHANGE the secondary sound
                    userSequence.setSecondarySound(selectSound(("Secondary Sound")));
                    break;
                case 8: // PLAY a sound
                    String[] soundList = Sound.getSoundList();
                    System.out.println("\tSound List:");
                    System.out.print(getFormattedSoundList(soundList));
                    System.out.println("\t---");
                    System.out.println("\t 0) Exit");

                    while (true) {
                        System.out.println(prompt("Which sound would you like to play?", 3));
                        int whichSound = keybIn.nextInt();

                        if (whichSound == 0) {
                            break;
                        } else if (whichSound > 0 && whichSound <= soundList.length) {
                            try {
                                String soundPath = Sound.getPathFromSoundList(soundList[whichSound - 1]);
                                System.out.print("\tPlaying sound...");
                                Sound.playSound(Sound.createSoundStream(soundPath));
                                System.out.println("Done.");
                            } catch (Exception e) {
                                System.out.println("InterfaceCommandLine Class Error: " + e);
                            }
                        } else {
                            System.out.println(errorMsg("That is not a valid choice."));
                        }
                    }

                    break;
                default:
                    System.out.println(errorMsg("That's not a valid choice."));
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
            System.out.println(prompt("What would you like to do?", 1));
            System.out.println("\t1) See my session");
            System.out.println("\t2) Edit session details");
            System.out.println("\t3) Start my session (not done)");
            System.out.println("\t4) Save my session (not done)");
            System.out.println("\t5) Load a session (not done)");
            System.out.println("\t---");
            System.out.println("\t0) Exit");
            int userChoice = keybIn.nextInt();

            switch (userChoice) {
                case 0:
                    doneProgram = true;
                    break;
                case 1:
                    System.out.println("+--------------------------------------------------");
                    System.out.println(userSequence.getSequenceLine());
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
