package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** This class is the controller for Room 3 in the Escaipe game. */
public class Room3Controller {

  @FXML private Pane pinPadUi;
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
  @FXML private ImageView imgSlidingDoor;
  @FXML private ImageView imgConveyor;
  @FXML private ImageView imgConveyorResin;

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
  private boolean conveyorIsActive;
  private long conveyorFrameRate = 4;

  private String[] pinHints = {
    "Clock", "AI Eyes", "3D Printers", "Rooms", "16 mod 6",
    "Shelves", "Sticky Note", "Lasers", "AI Version Number", "(3 ^ 3) - 18"
  };

  private String pin;

  /**
   * Initializes the Room3Controller by activating the conveyor, setting up the pin pad, and creating
   * the necessary animation and event timelines for the pin pad and lights. Also sets the text relating
   * to 4 objects that can help the player guess the pin.
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

    /* Regardless of the initial colour in SceneBuilder, the pin pad text box uses
    the default colours */
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
                  App.getTopBarController().giveItem(TopBarController.Item.SAW_BROKEN);
                  battery.setOpacity(0);
                  TranslateTransition openDoor =
                      new TranslateTransition(Duration.millis(250), imgSlidingDoor);
                  openDoor.setByX(70);
                  openDoor.play();
                }));

    /* Animation and event timeline for turning off the lights */
    lightsOff =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  deactivateConveyor();
                }),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.3)));

    /* Animation and event timeline for turning on the lights */
    lightsOn =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(
                Duration.seconds(1.2),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  activateConveyor();
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

  /** Turns off the lights. */
  public void lightsOff() {
    lightsOff.playFromStart();
  }

  /** Turns on the lights. */
  public void lightsOn() {
    lightsOn.playFromStart();
  }

  /**
   * Opens the pin pad.
   *
   * @param event the mouse event triggered by the pin pad open button
   */
  @FXML
  public void clickPinPadOpen(MouseEvent event) throws IOException {
    pinPadUi.setVisible(true);
  }

  /**
   * Closes the pin pad.
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

    // Create a new task to move the conveyor belt and resin image
    Task<Void> conveyorMovementTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {

            // Initialize variables
            int conveyorFrame = 1;
            conveyorIsActive = true;

            // Loop through conveyor frames while the conveyor is active
            while (conveyorIsActive) {

              // Reset conveyor frame to 1 if it reaches 5
              if (conveyorFrame == 5) {
                conveyorFrame = 1;
              }

              // Update conveyor image to the current frame
              imgConveyor.setImage(
                  new Image("/images/rightRoomBelt-Frame" + conveyorFrame + ".png"));

              // Move resin image down the conveyor belt
              if (imgConveyorResin.getLayoutY() <= 0) {
                // If resin reaches the end of the conveyor, reset its position to the start
                Platform.runLater(
                    () -> {
                      imgConveyorResin.setLayoutX(830);
                      imgConveyorResin.setLayoutY(235);
                    });
              } else if (imgConveyorResin.getLayoutX() > 295) {
                // If resin is still on the conveyor, move it to the left
                Platform.runLater(
                    () -> {
                      imgConveyorResin.setLayoutX(imgConveyorResin.getLayoutX() - 4);
                    });
              } else {
                // If resin reaches the end of the conveyor, move it down
                Platform.runLater(
                    () -> {
                      imgConveyorResin.setLayoutY(imgConveyorResin.getLayoutY() - 4);
                    });
              }

              // Increment conveyor frame and wait for a few milliseconds
              conveyorFrame++;
              try {
                Thread.sleep(conveyorFrameRate);
              } catch (Exception e) {
                System.err.println("ERROR: Exception in Room1Controller.dropSaw!");
              }
            }

            return null;
          }
        };

    // Create a new thread to run the conveyor movement task
    Thread conveyorMovementThread = new Thread(conveyorMovementTask);
    conveyorMovementThread.start();
  }

  /** Deactivates the conveyor belt animation. */
  public void deactivateConveyor() {
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
      // ADD PLAYER ALREADY HAS ITEM CODE
    } else if (conveyorIsActive) {
      // ADD PLAYER CANNOT ACCESS CONVEYOR HINT
    } else if (!conveyorIsActive) {
      App.getTopBarController().giveItem(TopBarController.Item.RESIN);
      imgConveyorResin.setVisible(false);
    }
  }
}
