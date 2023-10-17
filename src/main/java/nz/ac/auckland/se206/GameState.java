package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
/**
 * This class represents the current state of the game. It contains various static fields that store
 * information about the game, such as the current difficulty, whether the laser is active, and how
 * much time the player has left.
 */
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
   * been given to the player. isRiddleExpected is only used when the player checks the 3D printing
   * terminal the first time and is simply because GPT is slightly erratic in giving (or attempting
   * to give) a hint instead of a riddle.
   */
  public static boolean isRiddleActive = false;

  public static boolean isRiddleExpected = false;

  /**
   * Method to reset all game variables to their default values. This is called when the player
   * restarts the game.
   */
  public static void resetGameVariables() {
    isFirstTime = true;
    isRiddleActive = false;
    isRiddleExpected = false;
    isRoom1Solved = false;
    isRoom2Solved = false;
    isRoom3Solved = false;
    isGameOffline = false;
    currentDifficulty = Difficulty.EASY;
    timeSetting = 360000;
  }

  /** Enum representing the three difficulty levels of the game. */
  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  /** Indicates whether the laser is currently active. */
  public static boolean isLaserActive = false;

  /** Indicates whether the player has asked the riddle for the first time. */
  public static boolean isFirstTimeForRiddle = true;

  /** Indicates whether the player has solved the riddle. */
  public static boolean isRiddleSolved = false;

  /** Indicates whether the player has obtained the password. */
  public static boolean isPasswordObtained = false;

  /** Indicates how much time the player had when they finished the game. */
  public static String formattedFinishTime;

  /** Points to the relevant room GUI timer label. */
  public static Label roomTimerLabel;

  /** Number of hints remaining. */
  public static int hintsRemaining = 0;

  /** Indicates whether GPT is currently running. */
  public static boolean isGPTRunning = false;

  /** Indicates which difficulty the player has clicked */
  private static Difficulty currentDifficulty;

  // Setters and getters for difficulty
  public static Difficulty getCurrentDifficulty() {
    return currentDifficulty;
  }

  public static void setDifficulty(Difficulty difficulty) {
    currentDifficulty = difficulty;
  }

  /** Points to the relevent chat GUI timer label */
  public static Label chatTimerLabel;

  /** Indicates how much time the player has chosen to play the game. */
  public static int timeSetting;

  /** Triggered when GPT fails to load. */
  public static boolean isGameOffline = false;
}
