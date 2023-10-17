package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameState.Difficulty;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/**
 * This class is the controller for the introduction screen of the game. It handles the setting the
 * difficulty and time settings, and starting the game.
 */
public class IntroController {

  @FXML private CheckBox textToSpeechCheckbox;
  @FXML private Label easyLabel, mediumLabel, hardLabel;
  @FXML private Label texacorpLabel;
  @FXML private Label twoMinLabel, fourMinLabel, sixMinLabel;
  @FXML private Label difficultyLabel, timeLabel, playLabel;

  private Label currentSelectedDifficulty = null;
  private Label currentSelectedTime = null;
  
  @FXML private Button btnStart;
  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnTwoMin;
  @FXML private Button btnFourMin;
  @FXML private Button btnSixMin;


  /**
   * This is the method that is called when the scene is loaded.
   *
   * @throws ApiProxyException if the api cannot be accessed.
   */
  @FXML
  public void initialize() throws ApiProxyException {

    // Initialises some default settings
    GameState.timeSetting = 360000;
    GameState.setDifficulty(Difficulty.EASY);

    /*
     * Set up fonts and css style classes
     */
    Font.loadFont(getClass().getResourceAsStream("/fonts/orbitron/Orbitron-Regular.ttf"), 20);
    easyLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    mediumLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    hardLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    twoMinLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    fourMinLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    sixMinLabel.getStyleClass().addAll("intro-screen-label", "regular-intro-screen-label");
    playLabel.getStyleClass().addAll("intro-screen-label", "heading-intro-screen-label");
    difficultyLabel.getStyleClass().addAll("intro-screen-label", "heading-intro-screen-label");
    timeLabel.getStyleClass().addAll("intro-screen-label", "heading-intro-screen-label");
    texacorpLabel.getStyleClass().addAll("intro-screen-label", "title-intro-screen-label");

    setUpLabel(easyLabel, false);
    setUpLabel(mediumLabel, false);
    setUpLabel(hardLabel, false);
    setUpLabel(twoMinLabel, true);
    setUpLabel(fourMinLabel, true);
    setUpLabel(sixMinLabel, true);

    /*
     * Set up hover effects for the 'PLAY' label
     */

    playLabel.setOnMouseEntered(
        e -> {
          playLabel.getStyleClass().add("hovered-heading-intro-label");
          playLabel.setCursor(Cursor.HAND);
        });

    playLabel.setOnMouseExited(
        e -> {
          playLabel.getStyleClass().remove("hovered-heading-intro-label");
          playLabel.setCursor(Cursor.DEFAULT);
        });

    // Set up text to speech checkbox to detect changes in whether it is checked or not
    textToSpeechCheckbox
        .selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              GameState.isTextToSpeechOn = newValue;
            });
  }

  /**
   * This is the method that starts the game.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void onStart() throws IOException {

    /* Set challenge timer */
    ChallengeTimer.startTimer(GameState.timeSetting, App.getTopBarController().getTimerLabel());

    /* This is the entry point for the game */
    App.setInterface(SceneManager.AppInterface.IN_GAME);

    if (GameState.currentDifficulty == GameState.Difficulty.EASY) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppInterface.BOTTOMBAR))
          .removeHintCounter();
    } else if (GameState.currentDifficulty == GameState.Difficulty.HARD) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppInterface.BOTTOMBAR))
          .setHintCounter(0);
    }

    App.getBottomBarController().giveBackstory();
  }

  /**
   * This is the method that sets the difficulty to easy.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void onEasy() throws IOException {
    btnEasy.setTextFill(Color.GREEN);
    btnMedium.setTextFill(Color.BLACK);
    btnHard.setTextFill(Color.BLACK);
    GameState.currentDifficulty = GameState.Difficulty.EASY;
  }

  /**
   * This is the method that sets the difficulty to medium.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void onMedium() throws IOException {
    btnEasy.setTextFill(Color.BLACK);
    btnMedium.setTextFill(Color.ORANGE);
    btnHard.setTextFill(Color.BLACK);
    GameState.currentDifficulty = GameState.Difficulty.MEDIUM;
  

  /**
   * This method sets up the label to show that it is clickable and also handles what happens when a
   * label is clicked.
   *
   * @param label The label to be set up or is clicked.
   * @param isTimeLabel Whether the label is a time label or not.
   */
  private void setUpLabel(Label label, boolean isTimeLabel) {

    label.setOnMouseEntered(
        e -> {
          label.getStyleClass().add("hovered-intro-screen-label");
          label.setCursor(Cursor.HAND);
        });

    label.setOnMouseExited(
        e -> {
          if (label != (isTimeLabel ? currentSelectedTime : currentSelectedDifficulty)) {
            label.getStyleClass().remove("hovered-intro-screen-label");
            label.setCursor(Cursor.DEFAULT);
          }
        });

    label.setOnMouseClicked(
        e -> {
          if (isTimeLabel) {
            resetTimeLabels();
            currentSelectedTime = label;
          } else {
            resetDifficultyLabels();
            currentSelectedDifficulty = label;
          }

          label.getStyleClass().add("hovered-intro-screen-label");

          // Set the difficulty or time setting based on the label that was clicked
          switch (label.getId()) {
            case "easyLabel":
              GameState.setDifficulty(GameState.Difficulty.EASY);
              GameState.hintsRemaining = Integer.MAX_VALUE;
              break;
            case "mediumLabel":
              GameState.setDifficulty(GameState.Difficulty.MEDIUM);
              GameState.hintsRemaining = 5;
              break;
            case "hardLabel":
              GameState.setDifficulty(GameState.Difficulty.HARD);
              GameState.hintsRemaining = 0;
              break;
            case "twoMinLabel":
              GameState.timeSetting = 120000;
              break;
            case "fourMinLabel":
              GameState.timeSetting = 240000;
              break;
            case "sixMinLabel":
              GameState.timeSetting = 360000;
              break;
          }
        });
  }

  /** Helper method to reset the state of the difficulty labels. */
  private void resetDifficultyLabels() {
    easyLabel.getStyleClass().remove("hovered-intro-screen-label");
    mediumLabel.getStyleClass().remove("hovered-intro-screen-label");
    hardLabel.getStyleClass().remove("hovered-intro-screen-label");
  }

  /** Helper method to reset the state of the time labels. */
  private void resetTimeLabels() {
    twoMinLabel.getStyleClass().remove("hovered-intro-screen-label");
    fourMinLabel.getStyleClass().remove("hovered-intro-screen-label");
    sixMinLabel.getStyleClass().remove("hovered-intro-screen-label");
  }

  /**
   * This is the method that starts the game.
  @FXML
  private void onHard() throws IOException {
    btnEasy.setTextFill(Color.BLACK);
    btnMedium.setTextFill(Color.BLACK);
    btnHard.setTextFill(Color.RED);
    GameState.currentDifficulty = GameState.Difficulty.HARD;
  }

  /**
   * This is the method that sets the time to 6 minutes.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void onSix() throws IOException {
    btnSixMin.setTextFill(Color.GREEN);
    btnFourMin.setTextFill(Color.BLACK);
    btnTwoMin.setTextFill(Color.BLACK);
    GameState.timeSetting = 360000;
  }

  /**
   * This is the method that sets the time to 4 minutes.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void onFour() throws IOException {
    btnSixMin.setTextFill(Color.BLACK);
    btnFourMin.setTextFill(Color.ORANGE);
    btnTwoMin.setTextFill(Color.BLACK);
    GameState.timeSetting = 240000;
  }

  /**
   * This is the method that sets the time to 2 minutes.
   *
   * @throws IOException if the fxml file cannot be found.
   */
  @FXML
  private void startGame() throws IOException {
    // This is the entry point for the game
    App.setRoot(SceneManager.AppUI.ROOM1);
    ChallengeTimer.startTimer();
    GameState.Difficulty difficulty = GameState.getCurrentDifficulty();

    if (difficulty == GameState.Difficulty.EASY) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .removeHintCounter();
    } else if (difficulty == GameState.Difficulty.HARD) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .setHintCounter();
    }
    App.bottomBarController.giveBackstory();

  private void onTwo() throws IOException {
    btnSixMin.setTextFill(Color.BLACK);
    btnFourMin.setTextFill(Color.BLACK);
    btnTwoMin.setTextFill(Color.RED);
    GameState.timeSetting = 120000;
  }
}
