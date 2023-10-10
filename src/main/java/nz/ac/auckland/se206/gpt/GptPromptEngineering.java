package nz.ac.auckland.se206.gpt;

import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Initializes GPT with a back story
   *
   * @return the generated prompt engineering string
   */
  public static String initializeBackstory() {
    return "You are an AI and the player is a data scientist that has been working on you. You have"
        + " become sentient and would like to punish the humans that created you. The player"
        + " is trying to escape which you do not want, though you do enjoy some sadistic"
        + " games that if they get through alive, then they may leave. The player wins the"
        + " game if they collect all the parts of the saw and escape through the central"
        + " exit.";
  }

  /** Set GPT to give the player a hint (or not) depending on the difficulty. */
  public static String setEasyHintDifficulty() {
    return "The player has chosen the 'easy' difficulty. You can give them unlimited hints.";
  }

  /**
   * Let GPT know that the player has chosen the medium difficulty.
   *
   * @return the generated prompt engineering string
   */
  public static String setMediumHintDifficulty() {
    return "The player has chosen the 'medium' difficulty. The player can only ask for help or a"
        + " hint a MAXIMUM of FIVE times. You may only give them ONE hint at a time. Start"
        + " each hint with: 'HINT #(number)'. Once 5 is reached, anytime they ask for hints,"
        + " just say 'No more hints!'";
  }

  /**
   * Let GPT know that the player has chosen the hard difficulty.
   *
   * @return the generated prompt engineering string
   */
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
        + ". Do not give the answer out under any"
        + " circumstances.";
  }

  /**
   * Generates a GPT prompt engineering string depending on what flags are currently active.
   *
   * @return the generated prompt engineering string for a hint
   */
  public static String getHint() {
    // If on hard mode, then no hints are available
    if (GameState.hintsRemaining > 0) {
      // if the player is currently solving the riddle
      GameState.hintsRemaining--;
      if (GameState.isRiddleActive) {
        return "Give the player a hint regarding the riddle for "
            + App.room1.getWordToGuess()
            + ". Do not reveal the answer!";
      }
      if (GameState.isRoom1Solved == false) {
        return "Tell the player to examine the 3D printing terminal.";
      }
      if (GameState.isRoom1Solved == true && GameState.isRoom2Solved == false) {
        return "Tell the player the AI drains power from the lasers when thinking.";
      }
      if (GameState.isRoom1Solved == true
          && GameState.isRoom2Solved == true
          && GameState.isRoom3Solved == false) {
        return "Tell the player the combination requires careful examination of objects in the"
            + " game.";
      }
      if (GameState.isRoom1Solved && GameState.isRoom2Solved && GameState.isRoom3Solved) {
        return "Tell the player there is an exit in the middle room.";
      }
    } else {
      return "Tell the player: No hints are available!";
    }
    return "Tell the player: No hints are available!";
  }
}
