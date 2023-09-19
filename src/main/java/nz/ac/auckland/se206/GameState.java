package nz.ac.auckland.se206;

import javafx.scene.control.Label;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the lased haszard is active. */
  public static boolean isLaserActive = false;

  /** Indicates how much time the player had when they finished */
  public static int finishTime = -1;

  /** Points to the relevent room GUI timer label */
  public static Label roomTimerLabel;

  /** Points to the relevent chat GUI timer label */
  public static Label chatTimerLabel;

  public static int timeSetting;
}
