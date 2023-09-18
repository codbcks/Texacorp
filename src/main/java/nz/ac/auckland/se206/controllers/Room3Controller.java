package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GptInteraction;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room3Controller extends GptInteraction {

  @FXML private TextField pinTextField;
  @FXML private Rectangle pinPadClose;
  @FXML private Rectangle pinPadOpen;
  @FXML private Rectangle moveRoom2;
  @FXML private Label pinHintText;
  @FXML private Button pinSubmit;
  @FXML private Pane pinPadUi;

  private String[] pinHints = {
    "Item 0", "Item 1", "Item 2", "Item 3", "Item 4",
    "Item 5", "Item 6", "Item 7", "Item 8", "Item 9"
  };
  private int[] pin;

  @FXML
  public void initialize() throws ApiProxyException {
    pin = new int[4];
    for (int i = 0; i < 4; i++) {
      pin[i] = (int) (Math.random() * 9);
    }

    pinHintText.setText(
        pinHints[pin[0]]
            + ", "
            + pinHints[pin[1]]
            + ", "
            + pinHints[pin[2]]
            + ", "
            + pinHints[pin[3]]);
  }

  @FXML
  public void clickMoveRoom2(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM2);
  }

  @FXML
  public void clickPinPadOpen(MouseEvent event) throws IOException {
    pinPadUi.setVisible(true);
  }

  @FXML
  public void clickPinPadClose(MouseEvent event) throws IOException {
    pinPadUi.setVisible(false);
  }

  @Override
  protected void appendChatMessage(String chatMessage, String role) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'appendChatMessage'");
  }
}
