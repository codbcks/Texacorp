package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;

public class FailController {

  @FXML private Button menuBtn;

  @FXML
  public void menu(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.INTRO);
    App.resetGame();
    GameState.resetGameVariables();
  }
}
