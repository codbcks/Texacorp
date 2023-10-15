package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

/**
 * This class represents a timer for a challenge in the game. It uses multithreading to avoid
 * slowing down or freezing the GUI. The timer continuously checks whether it has expired, and on
 * each iteration it updates the GUI timer according to currentLabelTimer.
 */
public class ChallengeTimer {
  private static long startTime;
  private static long endTime;
  private static Label timerLabel;
  private static boolean timerActive;

  /**
   * Starts the timer for the challenge.
   *
   * @param timeLimit the time limit for the challenge in milliseconds
   * @param newTimerLabel the label to display the timer on
   */
  public static void startTimer(long timeLimit, Label newTimerLabel) {

    // Initializing variables
    startTime = System.currentTimeMillis();
    endTime = startTime + timeLimit;
    timerLabel = newTimerLabel;
    timerActive = true;

    /* Multithreading is used so that the timer task doesn't
    cause the GUI to slow down or freeze.
    The timer continuously checks whether it has expired, and on each
    iteration it updates the GUI timer according to currentLabelTimer.
    */

    Task<Void> countDown =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {

            while ((System.currentTimeMillis() < endTime) && (timerActive)) {

              long remainingTime = endTime - System.currentTimeMillis();

              Platform.runLater(
                  () -> {
                    timerLabel.setText(
                        String.format(
                            "%02d:%02d",
                            (((remainingTime) / 1000) / 60), (((remainingTime) / 1000) % 60)));
                  });

              Thread.sleep(1000);
            }

            return null;
          }
        };

    if (timerLabel == null) {
      System.err.println("WARNING: No timer assigned to TimedScenario.currentLabelTimer!");
    } else {
      (new Thread(countDown)).start();
    }
  }

  /** Cancels the timer for the challenge. */
  public static void cancelTimer() {
    // Game state finish time represents how much time was left on the clock when the player wins.
    long remainingTime = endTime - System.currentTimeMillis();
    GameState.formattedFinishTime =
        String.format(
            "%02d:%02d", (((remainingTime) / 1000) / 60), (((remainingTime) / 1000) % 60));

    // TimerActive = false breaks the timer loop
    timerActive = false;
  }

  /**
   * Returns whether the timer for the challenge is active.
   *
   * @return true if the timer is active, false otherwise
   */
  public static boolean isTimerActive() {
    return timerActive;
  }
}
