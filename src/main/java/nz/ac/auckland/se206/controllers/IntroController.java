package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class IntroController {

  @FXML private Button btnStart;

  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnTwoMin;
  @FXML private Button btnFourMin;
  @FXML private Button btnSixMin;

  @FXML
  public void initialize() throws ApiProxyException {
    btnEasy.setTextFill(Color.GREEN);
    btnSixMin.setTextFill(Color.GREEN);
    GameState.timeSetting = 360000;
    GameState.currentDifficulty = GameState.Difficulty.EASY;
  }

  @FXML
  private void startGame() throws IOException {
    // This is the entry point for the game
    App.setRoot(SceneManager.AppUI.ROOM1);
    ChallengeTimer.startTimer();

    if (GameState.currentDifficulty == GameState.Difficulty.EASY) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .removeHintCounter();
    } else if (GameState.currentDifficulty == GameState.Difficulty.HARD) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .setHintCounter(0);
    }

    App.bottomBarController.giveBackstory();
  }

  @FXML
  private void setEasy() throws IOException {
    btnEasy.setTextFill(Color.GREEN);
    btnMedium.setTextFill(Color.BLACK);
    btnHard.setTextFill(Color.BLACK);
    GameState.currentDifficulty = GameState.Difficulty.EASY;
  }

  @FXML
  private void setMedium() throws IOException {
    btnEasy.setTextFill(Color.BLACK);
    btnMedium.setTextFill(Color.ORANGE);
    btnHard.setTextFill(Color.BLACK);
    GameState.currentDifficulty = GameState.Difficulty.MEDIUM;
  }

  @FXML
  private void setHard() throws IOException {
    btnEasy.setTextFill(Color.BLACK);
    btnMedium.setTextFill(Color.BLACK);
    btnHard.setTextFill(Color.RED);
    GameState.currentDifficulty = GameState.Difficulty.HARD;
  }

  @FXML
  private void setSix() throws IOException {
    btnSixMin.setTextFill(Color.GREEN);
    btnFourMin.setTextFill(Color.BLACK);
    btnTwoMin.setTextFill(Color.BLACK);
    GameState.timeSetting = 360000;
  }

  @FXML
  private void setFour() throws IOException {
    btnSixMin.setTextFill(Color.BLACK);
    btnFourMin.setTextFill(Color.ORANGE);
    btnTwoMin.setTextFill(Color.BLACK);
    GameState.timeSetting = 240000;
  }

  @FXML
  private void setTwo() throws IOException {
    btnSixMin.setTextFill(Color.BLACK);
    btnFourMin.setTextFill(Color.BLACK);
    btnTwoMin.setTextFill(Color.RED);
    GameState.timeSetting = 120000;
  }
}
