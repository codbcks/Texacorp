package nz.ac.auckland.se206;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HintSystem {

  private int hintCounter = 0;
  private final ExecutorService executorService;
  private Difficulty currentDifficulty;
  private static final int MAX_HINTS_MED = 5;

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
  }

  public HintSystem(Difficulty difficulty) {
    this.executorService = Executors.newSingleThreadExecutor();
    this.currentDifficulty = difficulty;
  }

  public boolean processRequest(String response) {
    if (response.startsWith("HINT #") && canGiveHint()) {
      hintCounter++;
      return true;
    }
    return false;
  }

  public boolean canGiveHint() {
    switch (currentDifficulty) {
      case EASY:
        return true;
      case MEDIUM:
        return hintCounter < MAX_HINTS_MED;
      case HARD:
      default:
        return false;
    }
  }

  public int getHintCount() {
    return hintCounter;
  }

  public void shutdown() {
    executorService.shutdown();
  }
}
