package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.controllers.InGameController;

public class ChallengeTimer {
  private static long startTime;
  private static long endTime;
  private static Label timerLabel;
  private static boolean timerActive;

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

            if (timerActive) {
              timerActive = false;
              ((InGameController)(SceneManager.getController(SceneManager.AppUI.IN_GAME))).showLose();
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

  // This method resets the timer
  public static void cancelTimer() {
    // Game state finish time represents how much time was left on the clock when the player wins.
    long remainingTime = endTime - System.currentTimeMillis();
    GameState.formattedFinishTime =
        String.format(
            "%02d:%02d", (((remainingTime) / 1000) / 60), (((remainingTime) / 1000) % 60));

    // TimerActive = false breaks the timer loop
    timerActive = false;
  }

  public static boolean isTimerActive() {
    return timerActive;
  }
}
