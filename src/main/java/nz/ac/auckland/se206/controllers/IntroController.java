package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class IntroController {

  @FXML private Button btnStart;

  @FXML
  private void startGame() throws IOException {
    // This is the entry point for the game
    App.setRoot(SceneManager.AppUI.ROOM);
  }
}
