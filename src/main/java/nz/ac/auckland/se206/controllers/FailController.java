package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class FailController {

  @FXML private Button winExitButton;
  @FXML private Button winMainMenuButton;
  @FXML private Button loseExitButton;
  @FXML private Button loseMainMenuButton;

  @FXML
  public void clickReturnMainMenu(ActionEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.INTRO);
    // return game variables to original states
  }

  @FXML
  public void clickExit(ActionEvent event) {
    System.exit(0);
  }
}
