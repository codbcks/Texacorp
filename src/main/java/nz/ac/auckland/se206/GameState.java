package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
/**
 * This class represents the current state of the game. It contains various static fields that store
 * information about the game, such as the current difficulty, whether the laser is active, and how
 * much time the player has left.
 */
public class GameState {

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

  /** Number of hints remaining for medium difficulty. */
  public static int hintsRemaining = 5;

  /** Indicates whether GPT is currently running. */
  public static boolean isGPTRunning = false;

  /** Indicates which difficulty the player has selected. */
  public static Difficulty currentDifficulty;

  /** Points to the relevant chat GUI timer label. */
  public static Label chatTimerLabel;

  /** Indicates how much time the player has chosen to play the game. */
  public static int timeSetting;
}
