package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the lased haszard is active. */
  public static boolean isLaserActive = false;

  public static boolean isFirstTime = true;

  public static boolean isPasswordObtained = false;

  /** Indicates how much time the player had when they finished */
  public static int finishTime = -1;

  /** Points to the relevent room GUI timer label */
  public static Label roomTimerLabel;

  /** Number of hints remaining for medium difficulty */
  public static int hintsRemaining = 5;

  public static Difficulty currentDifficulty = Difficulty.MEDIUM;

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }
}
