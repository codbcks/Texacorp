package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room2Controller extends Room {
  /** This class is the controller for Room 2 in the Escaipe game. */
  @FXML private ImageView lightOverlay;

  @FXML private ImageView laser;
  @FXML private Rectangle clockPromptTrigger;
  @FXML private Rectangle notesPromptTrigger;
  @FXML private ImageView imgWhiteboard;
  @FXML private Rectangle exit;

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
    mouseInteract(exit);

    // initialize lighting animations
    initializeLightAnim(lightOverlay, "middleRoomShadow", true);
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
      App.setInterface(SceneManager.AppInterface.WIN);
    } else {
      SceneManager.addToLogEnviroMessage(
          new ChatMessage("user", "It'd take a very powerful saw to cut this shutdown open..."));
      SceneManager.updateChat();
    }
  }

  /**
   * Handles clicking on the clock prompt.
   *
   * @param event The mouse event.
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  public void clickWhiteboard(MouseEvent event) throws IOException {
    imgWhiteboard.setVisible(true);
  }

  @FXML
  public void clickOffWhiteboard(MouseEvent event) throws IOException {
    imgWhiteboard.setVisible(false);
  }

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
