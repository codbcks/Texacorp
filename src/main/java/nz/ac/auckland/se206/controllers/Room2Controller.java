package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class Room2Controller {

  @FXML private Rectangle moveRoom1;
  @FXML private Rectangle moveRoom3;

  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;

  @FXML
  public void clickMoveRoom1(MouseEvent event) throws IOException {

    unsetSubScenes();
    ((Room1Controller) SceneManager.getController(SceneManager.AppUI.ROOM1)).setSubScenes();
    App.setRoot(SceneManager.AppUI.ROOM1);
  }

  @FXML
  public void clickMoveRoom3(MouseEvent event) throws IOException {
    unsetSubScenes();
    ((Room3Controller) SceneManager.getController(SceneManager.AppUI.ROOM3)).setSubScenes();
    App.setRoot(SceneManager.AppUI.ROOM3);
  }

  @FXML
  public void setSubScenes() {
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));
  }

  @FXML
  public void unsetSubScenes() {
    topBar.setRoot(new Region());
    bottomBar.setRoot(new Region());
  }
}
