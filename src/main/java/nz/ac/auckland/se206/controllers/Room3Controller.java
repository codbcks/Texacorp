package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GptInteraction;
import nz.ac.auckland.se206.SceneManager;

public class Room3Controller extends GptInteraction {

  @FXML private Rectangle moveRoom2;

  @FXML
  public void clickMoveRoom2(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM2);
  }

  @Override
  protected void appendChatMessage(String chatMessage, String role) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'appendChatMessage'");
  }
}
