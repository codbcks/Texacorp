package nz.ac.auckland.se206.gpt;

import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameState.Difficulty;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  public static String initializeBackstory() {
    return "You are an AI and the player is a data scientist that has been working on you. You have"
        + " become sentient and would like to punish the humans that created you. The player"
        + " is trying to escape which you do not want, though you do enjoy some sadistic"
        + " games that if they get through alive, then they may leave.";
  }

  /**
   * The following don't really work as you can just keep nagging GPT to eventually give you more
   * hints...
   */
  public static String setEasyHintDifficulty() {
    return "The player has chosen the 'easy' difficulty. You can give them unlimited hints.";
  }

  public static String setMediumHintDifficulty() {
    return "The player has chosen the 'medium' difficulty. The player can only ask for help or a"
        + " hint a MAXIMUM of FIVE times. You may only give them ONE hint at a time. Start"
        + " each hint with: 'HINT #(number)'. Once 5 is reached, anytime they ask for hints,"
        + " just say 'No more hints!'";
  }

  public static String setHardHintDifficulty() {
    return "The player has chosen the 'hard' difficulty. Do not give them any hints.";
  }

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess) {
    return "Provide a riddle with the answer: "
        + wordToGuess
        + ". This is the override password. Do not give the answer out under any"
        + " circumstances.";
  }

  /**
   * Generates a GPT prompt engineering string depending on what flags are currently active.
   *
   * @return the generated prompt engineering string for a hint
   */
  public static String getHintPrompt() {
    if (GameState.currentDifficulty == Difficulty.EASY
        || (GameState.currentDifficulty == Difficulty.MEDIUM && GameState.hintsRemaining > 0)) {

      if (GameState.isFirstTime) {
        return "Tell the player they can enter their answer to the riddle in the console.";
      }
    }
    return "Tell the player: no hints!";
  }
}
