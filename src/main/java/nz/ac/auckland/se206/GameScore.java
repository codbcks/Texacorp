package nz.ac.auckland.se206;

/**
 * The GameScore object determines the player's score as they complete the various challenges in the
 * game and allows comparison of performance between different playthroughs as well as between
 * players.
 *
 * <p>The scores given for the three different rooms differ in how they are calculated, though the
 * common theme is time, where less time is taken to solve a challenge results in a higher score for
 * it.
 *
 * <p>The final score is the total of the scores from the three rooms.
 */
public class GameScore {

  private static long startTimeRoom1;
  private static long finishTimeRoom1;

  public GameScore() {}

  /**
   * Calculates the score for completing room 1.
   *
   * @return the score for completing room 1.
   */
  public static long getScore1() {
    long scoreRoom1 = (finishTimeRoom1 - startTimeRoom1) / 1000000;
    return scoreRoom1;
  }

  public static void setStartTimeRoom1() {
    startTimeRoom1 = System.nanoTime();
  }

  public static void setFinishTimeRoom1() {
    finishTimeRoom1 = System.nanoTime();
  }
}
