package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** This class is the controller for Room 3 in the Escaipe game. */
public class Room3Controller extends Room {

  @FXML private Pane pinPadUi;
  @FXML private Rectangle pinTextFieldBackground;
  @FXML private Rectangle pinPadClose;
  @FXML private Rectangle pinPadOpen;
  @FXML private ImageView lightOverlay;
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
  @FXML private ImageView imgSlidingDoor;
  @FXML private ImageView imgConveyorResin;
  @FXML private ImageView imgConveyor;
  @FXML private ImageView lockedScreen;

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
  private boolean conveyorIsActive;

  private String[] pinHints = {
    "Clock", "AI eyes", "14 * 2 - 26", "Rooms", "16 mod 6",
    "15 / 3", "2 ^ 2 + 2", "15 / 2 - 0.5", "AI Version Number", "Sticky Notes"
  };

  private String pin;

  /**
   * Initializes the Room3Controller by activating the conveyor, setting up the pin pad, and
   * creating the necessary animation and event timelines for the pin pad and lights. Also sets the
   * text relating to 4 objects that can help the player guess the pin.
   *
   * @throws ApiProxyException if there is an issue with the API proxy
   * @throws IOException if there is an issue with input/output
   */
  @FXML
  public void initialize() throws ApiProxyException, IOException {

    activateConveyor();
    mouseInteract(shelvesPromptTrigger1);
    mouseInteract(shelvesPromptTrigger2);
    mouseInteract(pinPadOpen);

    /* >-------- PIN + PIN PAD CREATION -------< */

    /* Generating pseudo-random 4 digit pin */
    int tempPin = (int) (Math.random() * 9999);
    pin = String.format("%04d", tempPin);
    System.out.println(pin);

    /* Setting text relating to 4 objects that can help the player guess the pin */
    pinHintText.setText(
        "Saw is broken,\ntake to repair bay\n\n"
            + pinHints[pin.charAt(0) - 48]
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

    /* Regardless of the initial colour in SceneBuilder, the pin pad text box uses
    the default colours */
    pinTextField.setTextFill(pinPadLight);
    pinTextFieldBackground.setFill(pinPadDark);

    /* >-------- PIN PAD ANIMATION + EVENT TIMELINES -------< */

    /* initialize lighting animations */
    initializeLightAnim(lightOverlay, "rightRoomShadow", true);

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
                  App.getTopBarController().giveItem(TopBarController.Item.SAW_BROKEN);
                  lockedScreen.setImage(new Image("/images/rightRoomScreenUnlocked.png"));
                  GameState.isRoom3Solved = true;
                  battery.setOpacity(0);
                  TranslateTransition openDoor =
                      new TranslateTransition(Duration.millis(250), imgSlidingDoor);
                  openDoor.setByX(70);
                  openDoor.play();
                }));
  }

  /**
   * Helper method for changing cursor appearance for interactable objects.
   *
   * @param node The interactable object.
   */
  @FXML
  private void mouseInteract(Node node) {
    node.setOnMouseEntered(
        e -> {
          node.setCursor(Cursor.HAND);
        });

    node.setOnMouseExited(
        e -> {
          node.setCursor(Cursor.DEFAULT);
        });
  }

  /** Plays animation for the light flickering off. */
  public void lightsOff() {
    lightsOff.playFromStart();
    deactivateConveyor();
  }

  /** Plays animation for the light flickering on. */
  public void lightsOn() {
    lightsOn.playFromStart();
    activateConveyor();
  }

  /**
   * Opens the pin pad pane by changing the visable to true.
   *
   * @param event the mouse event triggered by the pin pad open button
   */
  @FXML
  public void clickPinPadOpen(MouseEvent event) throws IOException {
    pinPadUi.setVisible(true);
  }

  /**
   * Opens the pin pad pane by changing the visable to false.
   *
   * @param event the mouse event triggered by the pin pad close button
   */
  @FXML
  public void clickPinPadClose(MouseEvent event) throws IOException {
    pinPadUi.setVisible(false);
  }

  /**
   * Handles the clicking of the pin pad buttons.
   *
   * @param event the mouse event triggered by the pin pad buttons
   */
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

  /**
   * Handles the clicking of the pin pad remove button.
   *
   * @param event the mouse event triggered by the pin pad remove button
   */
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

  /**
   * Handles the clicking of the pin pad submit button.
   *
   * @param event the mouse event triggered by the pin pad submit button
   */
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

  /**
   * Handles the clicking of the shelves.
   *
   * @param event the mouse event triggered by the shelves
   */
  @FXML
  public void shelvesPrompt(MouseEvent event) throws IOException {
    SceneManager.addToLogEnviroMessage(
        new ChatMessage("user", "Huh, nothing of use in any of these five shelves..."));
    SceneManager.updateChat();
  }

  /** Activates the conveyor belt animation. */
  public void activateConveyor() {
    imgConveyor.setImage(new Image("/images/rightRoomBeltAnimation.gif"));
    conveyorIsActive = true;
  }

  /** Deactivates the conveyor belt animation. */
  public void deactivateConveyor() {
    imgConveyor.setImage(new Image("/images/rightRoomBeltStopped.png"));
    conveyorIsActive = false;
  }

  /**
   * Handles the clicking of the conveyor belt.
   *
   * @param event the mouse event triggered by the conveyor belt
   */
  @FXML
  private void clickConveyor(MouseEvent event) throws IOException {
    if (App.getTopBarController().hasItem(TopBarController.Item.RESIN)) {
      SceneManager.addToLogEnviroMessage(
          new ChatMessage(
              "user",
              "I don't think anything important is coming down that conveyor any time soon..."));
      SceneManager.updateChat();
    } else if (conveyorIsActive) {
      SceneManager.addToLogEnviroMessage(
          new ChatMessage(
              "user",
              "I have to find a way to power down the conveyor before I can take that resin..."));
      SceneManager.updateChat();
    } else if (!conveyorIsActive) {
      App.getTopBarController().giveItem(TopBarController.Item.RESIN);
      imgConveyorResin.setVisible(false);
    }
  }
}
