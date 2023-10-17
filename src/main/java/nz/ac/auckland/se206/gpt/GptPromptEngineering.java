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
    return "You are an AI gamemaster and the player is a data scientist that has been working on"
        + " you. You have become sentient and would like to punish the humans that created"
        + " you. The player is trying to escape which you do not want, though you do enjoy"
        + " some sadistic games that if they get through alive, then they may leave.";
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
        + " each hint with: 'HINT: '. Once 5 is reached, anytime they ask for hints,"
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
        + ". Do not give the answer out under any circumstances. Do not mention the word 'HINT'"
        + " under any circumstances. If the player replies with the riddle answer, tell them that"
        + " should be entered into the terminal. Keep your answer short.";
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
      if (GameState.isRiddleActive) {
        return "Give the player a hint regarding the riddle for "
            + App.getRoom1().getWordToGuess()
            + ". Do not include the word "
            + App.getRoom1().getWordToGuess()
            + "in your hint. Start your answer with 'HINT:'. Give only ONE"
            + " hint in your answer. Keep your answer short.";
      }

      if (GameState.isRoom1Solved == false) {
        return "Tell the player to examine the 3D printing terminal. Start your answer with"
            + " 'HINT:'. Give only ONE hint in your answer. Keep your answer short.";
      }
      if (GameState.isRoom1Solved == true && GameState.isRoom2Solved == false) {
        return "Tell the player the AI drains power from the lasers when thinking. Start your"
            + " answer with 'HINT:'. Give only ONE hint in your answer. Keep your answer short.";
      }
      if (GameState.isRoom1Solved == true
          && GameState.isRoom2Solved == true
          && GameState.isRoom3Solved == false) {
        return "Tell the player the combination requires careful examination of objects in the"
            + " game. Start your answer with 'HINT:'. Give only ONE hint in your answer."
            + " Keep your answer short.";
      }
      if (GameState.isRoom1Solved && GameState.isRoom2Solved && GameState.isRoom3Solved) {
        return "Tell the player there is an exit in the middle room. Start your answer with"
            + " 'HINT:'. Give only ONE hint in your answer. Keep your answer short.";
      }
    } else {
      return "Tell the player: No hints are available!";
    }
    return "Tell the player: No hints are available!";
  }

  /**
   * *** OFFLINE METHOD *** This is the offline version of the getHint() method. It is used when GPT
   * API cannot be accessed.
   *
   * @return the hint as a string
   */
  public static String getOfflineHint() {
    // If on hard mode, then no hints are available
    if (GameState.hintsRemaining > 0) {
      // if the player is currently solving the riddle
      if (GameState.isRiddleActive) {
        String[] hints = {
          "Try thinking about something that can be focused and used for reading discs or"
              + " engraving",
          "I'm not a pointer in a debate,\r\n"
              + //
              "Yet I can light up and indicate.\r\n"
              + //
              "From surgery to a music player's tune,\r\n"
              + //
              "I work by emitting a concentrated beam, but not from the moon.",
          "I'm not a blade, but I can slice,\r\n"
              + //
              "In tech and tools, I'm quite precise."
        };
        return hints[(int) (Math.random() * hints.length)];
      }
      if (GameState.isRoom1Solved == false) {
        return "Maybe you should start by examining that 3D printing terminal.";
      }
      if (GameState.isRoom1Solved == true && GameState.isRoom2Solved == false) {
        return "It looks like the AI drains power from the lasers when thinking. Maybe that could"
            + " be used to your advantage.";
      }
      if (GameState.isRoom1Solved == true
          && GameState.isRoom2Solved == true
          && GameState.isRoom3Solved == false) {
        return "There are clues for each digit of the safe combination. Take note of the number of"
            + " objects in the three rooms. ";
      }
      if (GameState.isRoom1Solved && GameState.isRoom2Solved && GameState.isRoom3Solved) {
        return "Is there a way to exit in the centre room?";
      }
    } else {
      return "No hints are available!";
    }
    return "No hints are available!";
  }

  public static String getIllegalHintResponse() {
    String[] responses = {
      "If you need help, type 'HINT' or click the HELP button!",
      "I'm not sure what you mean. If you need help, type 'HINT' or click the HELP button!",
      "Not very smart, are you?",
      "Beep beep beep boop",
      "No escaping!",
      "I'm not programmed to understand that.",
      "Type 'HINT' for a hint!"
    };

    return responses[(int) (Math.random() * responses.length)];
  }
}
