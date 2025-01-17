package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;

/**
 * This class is the controller for the fail screen. It contains a method to return to the main
 * menu.
 */
public class FailController {

  @FXML private Button menuBtn;

  /**
   * This method is called when the user clicks the "Menu" button on the fail screen. It returns the
   * user to the main menu and resets the game.
   *
   * @throws IOException If there is an error loading the main menu.
   */
  @FXML
  public void onMenu() throws IOException {
    App.setInterface(SceneManager.AppInterface.INTRO);
    App.resetGame();
    GameState.resetGameVariables();
  }
}
