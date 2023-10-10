package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameState.Difficulty;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class IntroController {

  @FXML private CheckBox textToSpeechCheckbox;
  @FXML private Label easyLabel, mediumLabel, hardLabel;
  @FXML private Label texacorpLabel;
  @FXML private Label twoMinLabel, fourMinLabel, sixMinLabel;
  @FXML private Label difficultyLabel, timeLabel, playLabel;

  private Label currentSelectedDifficulty = null;
  private Label currentSelectedTime = null;

  /**
   * This is the method that is called when the scene is loaded.
   *
   * @throws ApiProxyException if the api cannot be accessed.
   */
  @FXML
  public void initialize() throws ApiProxyException {

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

    GameState.timeSetting = 360000;
    GameState.setDifficulty(Difficulty.EASY);

    textToSpeechCheckbox
        .selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              GameState.isTextToSpeechOn = newValue;
            });

    setUpLabel(easyLabel, false);
    setUpLabel(mediumLabel, false);
    setUpLabel(hardLabel, false);
    setUpLabel(twoMinLabel, true);
    setUpLabel(fourMinLabel, true);
    setUpLabel(sixMinLabel, true);
  }

  private void setUpLabel(Label label, boolean isTimeLabel) {

    label.setOnMouseEntered(
        e -> {
          label.getStyleClass().add("hovered-intro-screen-label");
        });

    label.setOnMouseExited(
        e -> {
          if (label != (isTimeLabel ? currentSelectedTime : currentSelectedDifficulty)) {
            label.getStyleClass().remove("hovered-intro-screen-label");
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

  private void resetDifficultyLabels() {
    easyLabel.getStyleClass().remove("hovered-intro-screen-label");
    mediumLabel.getStyleClass().remove("hovered-intro-screen-label");
    hardLabel.getStyleClass().remove("hovered-intro-screen-label");
  }

  private void resetTimeLabels() {
    twoMinLabel.getStyleClass().remove("hovered-intro-screen-label");
    fourMinLabel.getStyleClass().remove("hovered-intro-screen-label");
    sixMinLabel.getStyleClass().remove("hovered-intro-screen-label");
  }

  /**
   * This is the method that starts the game.
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
          .setHintCounter(0);
    }

    App.bottomBarController.giveBackstory();
  }
}
