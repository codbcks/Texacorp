package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.controllers.TopBarController.Item;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room3Controller {

  @FXML private Pane pinPadUi;
  @FXML private Rectangle moveRoom2;
  @FXML private Rectangle pinTextFieldBackground;
  @FXML private Rectangle pinPadClose;
  @FXML private Rectangle pinPadOpen;
  @FXML private Rectangle lightOverlay;
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
  @FXML private ImageView battery;
  @FXML private Rectangle shelvesPromptTrigger1;
  @FXML private Rectangle shelvesPromptTrigger2;
  @FXML private Rectangle whiteboardPromptTrigger;

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
  private Timeline lightsOff;
  private Timeline lightsOn;

  private String[] pinHints = {
    "Clock", "AI Eyes", "3D Printers", "Rooms", "16 mod 6",
    "Shelves", "Sticky Note", "Lasers", "AI Version Number", "(3 ^ 3) - 18"
  };

  private String pin;

  @FXML
  public void initialize() throws ApiProxyException, IOException {

    /* >-------- PIN + PIN PAD CREATION -------< */

    /* Generating pseudo-random 4 digit pin */
    int tempPin = (int) (Math.random() * 9999);
    pin = String.format("%04d", tempPin);
    System.out.println(pin);

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
                  ((TopBarController) SceneManager.getController(SceneManager.AppUI.TOPBAR))
                      .giveItem(Item.SAW_BATTERY);
                  GameState.isRoom3Solved = true;
                  battery.setOpacity(0);
                }));

    lightsOff =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.3)));

    lightsOn =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.0)));
  }

  public void lightsOff() {
    lightsOff.playFromStart();
  }

  public void lightsOn() {
    lightsOn.playFromStart();
  }

  @FXML
  public void clickMoveRoom2(MouseEvent event) throws IOException {
    unsetSubScenes();
    // changes root to room2 and sets subscenes
    ((Room2Controller) SceneManager.getController(SceneManager.AppUI.ROOM2)).setSubScenes();
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
    // Get the button that was clicked
    Button eventSource = (Button) event.getSource();
    // Get the text of the button that was clicked
    String pressedDigit = eventSource.getText();
    // If the pin pad is ready and the text field is not full, add the pressed digit to the text
    if ((pinPadReady) && (pinTextField.getText().length() < 4)) {
      pinTextField.setText(pinTextField.getText() + pressedDigit);
    } else if ((pinPadReady) && (pinTextField.getText().length() > 4)) {
      pinTextField.setText(pressedDigit);
    }
  }

  @FXML
  public void pinRemoveClick(MouseEvent event) throws IOException {
    // If the pin pad is ready and the text field is not empty, remove the last digit from the text
    int tempPinLen = pinTextField.getText().length();
    if ((pinPadReady) && (tempPinLen > 1) && (tempPinLen <= 4)) {
      pinTextField.setText(pinTextField.getText().substring(0, tempPinLen - 1));
    } else if ((pinPadReady) && (tempPinLen == 1)) {
      pinTextField.setText(pinPadDefaultMessage);
    }
  }

  @FXML
  public void pinSubmitClick(MouseEvent event) throws IOException {
    // If the pin pad is ready, check if the pin is correct
    if (pinPadReady) {
      pinPadReady = false;
      if (pinTextField.getText().equals(pin)) {
        resolvePinPad.playFromStart();

      } else {
        resetPinPad.playFromStart();
      }
    }
  }

  @FXML
  public void setSubScenes() {
    // sets subscenes
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));
  }

  @FXML
  public void unsetSubScenes() {
    // unsets subscenes
    topBar.setRoot(new Region());
    bottomBar.setRoot(new Region());
  }

  @FXML
  public void shelvesPrompt(MouseEvent event) throws IOException {
    SceneManager.appendChatMessage("Huh, nothing of use in any of these five shelves...", "user");
  }

  @FXML
  public void whiteboardPrompt(MouseEvent event) throws IOException {
    SceneManager.appendChatMessage(
        "Oh thank god, the escape drill class forgot to wipe down this whiteboard... Find saw"
            + " battery... Find saw motor... Create saw blade. Got it.",
        "user");
  }
}
