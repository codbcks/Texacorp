package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class IntroController {

  @FXML private Button btnStart;

  @FXML private Button btnEasy;
  @FXML private Button btnMedium;
  @FXML private Button btnHard;
  @FXML private Button btnTwoMin;
  @FXML private Button btnFourMin;
  @FXML private Button btnSixMin;

  @FXML
  private void startGame() throws IOException {
    // This is the entry point for the game
    App.setRoot(SceneManager.AppUI.ROOM);
  }

  @FXML
  private void setEasy() throws IOException {
    // TODO
  }

  @FXML
  private void setMedium() throws IOException {
    // TODO
  }

  @FXML
  private void setHard() throws IOException {
    // TODO
  }

  @FXML
  private void setTwo() throws IOException {
    // TODO
  }

  @FXML
  private void setFour() throws IOException {
    // TODO
  }

  @FXML
  private void setSix() throws IOException {
    // TODO
  }
}
