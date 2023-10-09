package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class InGameController {
  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;
  @FXML private SubScene room1;
  @FXML private SubScene room2;
  @FXML private SubScene room3;
  @FXML private Pane roomSlider;

  @FXML
  public void initialize() throws ApiProxyException {
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));
    room1.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM1));
    room2.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM2));
    room3.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM3));
  }
}
