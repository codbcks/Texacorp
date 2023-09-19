package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room3Controller {

  @FXML private Pane pinPadUi;
  @FXML private Rectangle moveRoom2;
  @FXML private Rectangle pinTextFieldBackground;
  @FXML private Rectangle pinPadClose;
  @FXML private Rectangle pinPadOpen;
  @FXML private Label pinTextField;
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

  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;

  private Color pinPadDark;
  private Color pinPadLight;
  private Color pinPadCorrectDark;
  private Color pinPadCorrectLight;
  private String pinPadDefaultMessage = "ENTER PIN TO UNLOCK BATTERY STATION";
  private String pinPadIncorrectMessage = "PIN INCORRECT!";
  private String pinPadCorrectMessage = "PIN CORRECT! UNLOCKING...";
  private String pinPadResolvedMessage = "STATION UNLOCKED";
  private boolean pinPadReady;
  private Timeline resetPinPad;
  private Timeline resolvePinPad;

  private String[] pinHints = {
    "Item 0", "Item 1", "Item 2", "Item 3", "Item 4",
    "Item 5", "Item 6", "Item 7", "Item 8", "Item 9"
  };

  private String pin;

  @FXML
  public void initialize() throws ApiProxyException {

    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));

    /* >-------- PIN + PIN PAD CREATION -------< */

    /* Generating pseudo-random 4 digit pin */
    pin = Integer.toString((int) (Math.random() * 9999));

    /* Setting text relating to 4 objects that can help the player guess the pin */
    pinHintText.setText(
        pinHints[pin.charAt(0) - 48]
            + "\n"
            + pinHints[pin.charAt(1) - 48]
            + "\n"
            + pinHints[pin.charAt(2) - 48]
            + "\n"
            + pinHints[pin.charAt(3) - 48]);

    pinPadReady = true;

    /* Pin pad colours are stored so that they can be used locally */
    pinPadDark = new Color(0.125, 0.0, 0.03125, 1.0);
    pinPadLight = new Color(1.0, 0.0, 0.03125, 1.0);
    pinPadCorrectDark = new Color(0.0, 0.125, 0.03125, 1.0);
    pinPadCorrectLight = new Color(0.0, 1.0, 0.03125, 1.0);

    /* Regardless of the initial colour in SceneBuilder, the pin pad text box uses the default colours */
    pinTextField.setTextFill(pinPadLight);
    pinTextFieldBackground.setFill(pinPadDark);

    /* >-------- PIN PAD ANIMATION + EVENT TIMELINES -------< */

    /* Animation and event timeline for entering the wrong pin */
    resetPinPad =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  pinTextField.setText(pinPadIncorrectMessage);
                  pinTextFieldBackground.setFill(pinPadLight);
                  pinTextField.setTextFill(pinPadDark);
                }),
            new KeyFrame(
                Duration.seconds(0.25),
                e -> {
                  pinTextFieldBackground.setFill(pinPadDark);
                  pinTextField.setTextFill(pinPadLight);
                }),
            new KeyFrame(
                Duration.seconds(0.5),
                e -> {
                  pinTextFieldBackground.setFill(pinPadLight);
                  pinTextField.setTextFill(pinPadDark);
                }),
            new KeyFrame(
                Duration.seconds(0.75),
                e -> {
                  pinTextFieldBackground.setFill(pinPadDark);
                  pinTextField.setTextFill(pinPadLight);
                }),
            new KeyFrame(
                Duration.seconds(1.0),
                e -> {
                  pinTextFieldBackground.setFill(pinPadLight);
                  pinTextField.setTextFill(pinPadDark);
                }),
            new KeyFrame(
                Duration.seconds(1.25),
                e -> {
                  pinTextFieldBackground.setFill(pinPadDark);
                  pinTextField.setTextFill(pinPadLight);
                  pinTextField.setText(pinPadDefaultMessage);
                  pinPadReady = true;
                }));

    /* Animation and event timeline for entering the right pin */
    resolvePinPad =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  pinTextField.setText(pinPadCorrectMessage);
                  pinTextField.setTextFill(pinPadCorrectLight);
                  pinTextFieldBackground.setFill(pinPadCorrectDark);
                }),
            new KeyFrame(
                Duration.seconds(0.75),
                e -> {
                  pinPadUi.setVisible(false);
                  pinTextField.setText(pinPadResolvedMessage);
                }));
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
    Button eventSource = (Button) event.getSource();
    String pressedDigit = eventSource.getText();
    if ((pinPadReady) && (pinTextField.getText().length() < 4)) {
      pinTextField.setText(pinTextField.getText() + pressedDigit);
    } else if ((pinPadReady) && (pinTextField.getText().length() > 4)) {
      pinTextField.setText(pressedDigit);
    }
  }

  @FXML
  public void pinRemoveClick(MouseEvent event) throws IOException {
    int tempPinLen = pinTextField.getText().length();
    if ((pinPadReady) && (tempPinLen > 1) && (tempPinLen <= 4)) {
      pinTextField.setText(pinTextField.getText().substring(0, tempPinLen - 1));
    } else if ((pinPadReady) && (tempPinLen == 1)) {
      pinTextField.setText(pinPadDefaultMessage);
    }
  }

  @FXML
  public void pinSubmitClick(MouseEvent event) throws IOException {
    if (pinPadReady) {
      pinPadReady = false;
      if (pinTextField.getText().equals(pin)) {
        resolvePinPad.playFromStart();
      } else {
        resetPinPad.playFromStart();
      }
    }
  }
}
