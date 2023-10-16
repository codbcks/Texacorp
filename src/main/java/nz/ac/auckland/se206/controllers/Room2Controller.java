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

  @FXML private ImageView lightOverlay;
  @FXML private ImageView laser;
  @FXML private Rectangle clockPromptTrigger;
  @FXML private Rectangle notesPromptTrigger;
  @FXML private ImageView imgWhiteboard;

  @FXML
  public void initialize() throws ApiProxyException {

    mouseInteract(clockPromptTrigger);
    mouseInteract(notesPromptTrigger);

    // initialize lighting animations
    initializeLightAnim(lightOverlay, "middleRoomShadow", true);
  }

  public void lightsOff() {
    lightsOff.playFromStart();
  }

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

  @FXML
  public void clickExit(MouseEvent event) throws IOException {
    // If the player has all the items, go to the win screen
    if (App.topBarController.hasItem(TopBarController.Item.SAW_FIXED)) {
      ChallengeTimer.cancelTimer();
      App.setRoot(SceneManager.AppUI.WIN);
    }
  }

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
    SceneManager.addToLogEnviroClick(
        new ChatMessage("user", "This clock is stuck on 0, it must be broken."));
    SceneManager.updateChat();
  }

  @FXML
  public void notesPrompt(MouseEvent event) throws IOException {
    SceneManager.addToLogEnviroClick(new ChatMessage("user", "6 blank sticky notes... weird."));
    SceneManager.updateChat();
  }
}
