package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class Room2Controller {

  @FXML private Rectangle moveRoom1;
  @FXML private Rectangle moveRoom3;

  @FXML
  public void clickMoveRoom3(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM3);
  }

  @FXML
  public void clickMoveRoom1(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM1);
  }
}
