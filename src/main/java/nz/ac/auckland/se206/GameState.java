package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether text to speech is on or not. */
  public static boolean isTextToSpeechOn = false;

  /**
   * The following indicate what stage the game is at and are used to determine what hints to
   * provide at the various stages of the game.
   */
  public static boolean isFirstTime = true;

  public static boolean isRoom1Solved = false;

  public static boolean isRoom2Solved = false;

  public static boolean isRoom3Solved = false;

  /**
   * The next two variables are used to determine whether the riddle is active and whether it has
   * been given to the player. isRiddleGiven is only used when the player checks the 3D printing
   * terminal the first time and is simply because GPT is slightly erratic in giving (or attempting
   * to give) a hint instead of a riddle.
   */
  public static boolean isRiddleActive = false;

  public static boolean isRiddleGiven = false;

  /**
   * Method to reset all game variables to their default values. This is called when the player
   * restarts the game.
   */
  public static void resetGameVariables() {
    isFirstTime = true;
    isRiddleActive = false;
    isRiddleGiven = false;
    isRoom1Solved = false;
    isRoom2Solved = false;
    isRoom3Solved = false;
  }

  /** Indicates how much time the player had when they finished */
  public static int finishTime = -1;

  /** Points to the relevent room GUI timer label */
  public static Label roomTimerLabel;

  /** Number of hints remaining for medium difficulty */
  public static int hintsRemaining = 0;

  /** Indicates whether gpt is running */
  public static boolean isGPTRunning = false;

  /** Indicates which difficulty the player has clicked */
  private static Difficulty currentDifficulty;

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  // Setters and getters for difficulty
  public static Difficulty getCurrentDifficulty() {
    return currentDifficulty;
  }

  public static void setDifficulty(Difficulty difficulty) {
    currentDifficulty = difficulty;
  }

  /** Points to the relevent chat GUI timer label */
  public static Label chatTimerLabel;

  public static int timeSetting;
}
