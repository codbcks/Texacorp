package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

/**
 * This class is the controller for the win screen. It contains a method to handle the menu button
 * click event. The menu button click event will take the user back to the intro screen and reset
 * the game.
 */
public class WinController {

  @FXML private Button menuBtn;

  /**
   * This method handles the menu button click event. It takes the user back to the intro screen and
   * resets the game.
   *
   * @param event The mouse event triggered by the user clicking the menu button.
   * @throws IOException If there is an error loading the intro screen.
   */
  @FXML
  public void clickMenu() throws IOException {
    App.setRoot(SceneManager.AppInterface.INTRO);
    App.resetGame();
  }
}
