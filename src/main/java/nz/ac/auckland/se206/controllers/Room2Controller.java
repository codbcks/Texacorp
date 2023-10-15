package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** This class is the controller for Room 2 in the Escaipe game. */
public class Room2Controller {

  // FXML elements
  @FXML private Rectangle lightOverlay;
  @FXML private ImageView sawBody;
  @FXML private ImageView laser;
  @FXML private Rectangle clockPromptTrigger;
  @FXML private Rectangle notesPromptTrigger;

  // Animation timelines
  private Timeline lightsOff;
  private Timeline lightsOn;

  /**
   * Initializes the Room2Controller by setting up mouse interaction for FXML elements and creating
   * animations for turning the lights on and off.
   *
   * @throws ApiProxyException if there is an issue with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {

    // Set up mouse interaction for FXML elements
    mouseInteract(clockPromptTrigger);
    mouseInteract(notesPromptTrigger);
    mouseInteract(sawBody);
    mouseInteract(laser);

    // Set up lights off animation
    lightsOff =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  laser.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.2),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  laser.setOpacity(1);
                }),
            new KeyFrame(
                Duration.seconds(0.4),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  laser.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.6),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  laser.setOpacity(1);
                }),
            new KeyFrame(
                Duration.seconds(1.2),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  laser.setOpacity(0);
                }));
    // Set up lights on animation
    lightsOn =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  laser.setOpacity(1);
                }),
            new KeyFrame(
                Duration.seconds(0.2),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  laser.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.4),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  laser.setOpacity(1);
                }),
            new KeyFrame(
                Duration.seconds(0.6),
                e -> {
                  lightOverlay.setOpacity(0.3);
                  laser.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(1.2),
                e -> {
                  lightOverlay.setOpacity(0.0);
                  laser.setOpacity(1);
                }));
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

  /**
   * Handles clicking on the saw body.
   *
   * @param event The mouse event.
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  public void clickSawBody(MouseEvent event) throws IOException {
    // If the gpt is running/room turned off, give the player the saw body when clicked
    if (GameState.isGPTRunning) {
      App.getTopBarController().giveItem(TopBarController.Item.SAW_BODY);
      sawBody.setVisible(false);
    } else {
      SceneManager.addToLogEnviroMessage(
          new ChatMessage(
              "user",
              "I'm pretty sure that 7 deadly lasers is above the recommended daily allowance."));
      SceneManager.updateChat();
    }
  }

  /**
   * Handles clicking on the exit.
   *
   * @param event The mouse event.
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  public void clickExit(MouseEvent event) throws IOException {
    // If the player has all the items, go to the win screen
    if (App.getTopBarController().hasItem(TopBarController.Item.SAW_FIXED)) {
      ChallengeTimer.cancelTimer();
      App.setRoot(SceneManager.AppInterface.WIN);
    }
  }

  /**
   * Handles clicking on the clock prompt.
   *
   * @param event The mouse event.
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  public void clockPrompt(MouseEvent event) throws IOException {
    SceneManager.addToLogEnviroMessage(
        new ChatMessage("user", "This clock is stuck on 0, it must be broken."));
    SceneManager.updateChat();
  }

  /**
   * Handles clicking on the notes prompt.
   *
   * @param event The mouse event.
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  public void notesPrompt(MouseEvent event) throws IOException {
    SceneManager.addToLogEnviroMessage(new ChatMessage("user", "6 blank sticky notes... weird."));
    SceneManager.updateChat();
  }
}
