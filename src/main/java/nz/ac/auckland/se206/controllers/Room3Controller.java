package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
  @FXML private GridPane pinPad;
  @FXML private Button pinDigit0;
  @FXML private Button pinDigit1;
  @FXML private Button pinDigit2;
  @FXML private Button pinDigit3;
  @FXML private Button pinDigit4;
  @FXML private Button pinDigit5;
  @FXML private Button pinDigit6;
  @FXML private Button pinDigit7;
  @FXML private Button pinDigit8;
  @FXML private Button pinDigit9;
  @FXML private Button pinRemove;
  @FXML private Button pinSubmit;
  @FXML private Pane pinPadUi;
  private boolean pinPadReady = true;

  private String[] pinHints = {
    "Item 0", "Item 1", "Item 2", "Item 3", "Item 4",
    "Item 5", "Item 6", "Item 7", "Item 8", "Item 9"
  };
  private String pin;

  @FXML
  public void initialize() throws ApiProxyException {
    pin = Integer.toString((int) (Math.random() * 9999));

    pinHintText.setText(
        pinHints[pin.charAt(0) - 48]
            + "\n"
            + pinHints[pin.charAt(1) - 48]
            + "\n"
            + pinHints[pin.charAt(2) - 48]
            + "\n"
            + pinHints[pin.charAt(3) - 48]);
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

  @FXML
  public void pinDigitClick(MouseEvent event) throws IOException {
    if ((pinPadReady) && (pinTextField.getLength() < 4)) {
      Button eventSource = (Button) event.getSource();
      String pressedDigit = eventSource.getText();
      pinTextField.appendText(pressedDigit);
    }
  }

  @FXML
  public void pinRemoveClick(MouseEvent event) throws IOException {
    if ((pinPadReady) && (pinTextField.getLength() > 0)) {
      pinTextField.setText(pinTextField.getText(0, pinTextField.getLength() - 1));
    }
  }

  @FXML
  public void pinSubmitClick(MouseEvent event) throws IOException {
    if (pinPadReady) {
      pinPadReady = false;
      if (pinTextField.getText().equals(pin)) {
        pinTextField.setText("Password Correct! Unlocking...");
      } else {
        pinTextField.setText("Password Incorrect!");
      }
    }
  }

  @Override
  protected void appendChatMessage(String chatMessage, String role) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'appendChatMessage'");
  }
}
