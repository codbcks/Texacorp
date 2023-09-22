package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
public class GameState {

  /** Indicates what stage the game is at. */
  public static boolean isLaserActive = false;

  public static boolean isFirstTime = true;

  public static boolean isRiddleSolved;

  public static boolean isPasswordObtained = false;

  /** Indicates how much time the player had when they finished */
  public static int finishTime = -1;

  /** Points to the relevent room GUI timer label */
  public static Label roomTimerLabel;

  /** Number of hints remaining for medium difficulty */
  public static int hintsRemaining = 5;

  public static boolean isGPTRunning = false;

  public static Difficulty currentDifficulty;

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  /** Points to the relevent chat GUI timer label */
  public static Label chatTimerLabel;

  public static int timeSetting;
}
