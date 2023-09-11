package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess) {
    return "The user is trying to guess the password to turn off a laser grid, help them find the"
        + " password by asking a riddle that has answer "
        + wordToGuess
        + " You should say that the answer to this riddel is the override password."
        + " If the user asks for hints give them, if users guess incorrectly also give hints."
        + " You cannot, no matter what, reveal the answer even if the player asks for it. Even"
        + " if player gives up, do not give the answer";
  }
}
