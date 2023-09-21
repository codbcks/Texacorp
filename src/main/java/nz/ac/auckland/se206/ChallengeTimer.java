package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ChallengeTimer {
  private static long TIMELIMIT;
  private static boolean timerActive = false;
  private static long timePassed = 0;
  private static long startTime;
  private static Label currentLabelTimer;

  // This method starts the challenge timer.
  public static void startTimer() {

    // Initializing variables...
    timerActive = true;
    startTime = System.currentTimeMillis();
    TIMELIMIT = GameState.timeSetting;
    /* Multithreading is used so that the timer task doesn't
    cause the GUI to slow down or freeze.

    The timer continuously checks whether it has expired, and on each
    iteration it updates the GUI timer according to currentLabelTimer.
    */
    Task<Void> timerTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            while ((timePassed < TIMELIMIT) && (timerActive)) {
              timePassed = (System.currentTimeMillis() - startTime);
              // Adding GUI updates to GUI queue.
              Platform.runLater(
                  () -> {
                    currentLabelTimer.setText(
                        // Calculating time formatting
                        String.format(
                            "%02d:%02d",
                            (((TIMELIMIT - timePassed) / 1000) / 60),
                            (((TIMELIMIT - timePassed) / 1000) % 60)));
                  });
              // Thread sleeps to conserve resources while the timer is not incrementing
              try {
                Thread.sleep(64);
              } catch (Exception e) {
                System.out.println("ERROR: Exception in TimedScenario.startTimer!");
                e.printStackTrace();
              }
            }

            // Lose case
            if (timerActive) {
              App.setRoot(SceneManager.AppUI.LOSE);
            }

            timePassed = 0;
            return null;
          }
        };

    Thread timerThread = new Thread(timerTask);

    // Exception handelling for when there is no timer GUI element currently specified.
    if (currentLabelTimer == null) {
      System.out.println("WARNING: No timer assigned to TimedScenario.currentLabelTimer!");
    } else {
      timerThread.start();
    }
  }

  // This method resets the timer
  public static void cancelTimer() {
    // Game state finish time represents how much time was left on the clock when the player wins.
    GameState.finishTime = (int) (TIMELIMIT - (System.currentTimeMillis() - startTime)) / 1000;
    System.out.println(GameState.finishTime);
    // TimerActive = false breaks the timer loop
    timerActive = false;
    timePassed = 0;
    // Reset GUI label
    initializeLabel();
  }

  // getRemainingTime returns how long is left on the clock.
  public static long getRemainingTime() {
    // Calculating remaining time on clock.
    return TIMELIMIT - timePassed;
  }

  public static void setCurrentLabelTimer(Label newLabelTimer) {
    // Setting the timer GUI element and initializing it with 0.
    currentLabelTimer = newLabelTimer;
    initializeLabel();
  }

  public static boolean isTimerActive() {
    return timerActive;
  }

  private static void initializeLabel() {
    // The following code adds the timer GUI reset to the GUI queue
    Platform.runLater(
        () -> {
          currentLabelTimer.setText(
              String.format(
                  // Formatting time limit
                  "%02d:%02d",
                  (((TIMELIMIT - timePassed) / 1000) / 60),
                  (((TIMELIMIT - timePassed) / 1000) % 60)));
        });
  }
}
