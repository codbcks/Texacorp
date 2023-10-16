package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room2Controller {

  @FXML private ImageView lightOverlay;
  @FXML private ImageView laser;
  @FXML private Rectangle clockPromptTrigger;
  @FXML private Rectangle notesPromptTrigger;
  @FXML private ImageView imgWhiteboard;

  private Timeline lightsOff;
  private Timeline lightsOn;
  private Image lightsOffOverlay;
  private Image lightsOnOverlay;

  @FXML
  public void initialize() throws ApiProxyException {

    mouseInteract(clockPromptTrigger);
    mouseInteract(notesPromptTrigger);

    lightsOnOverlay = new Image("/images/middleRoomShadow-lightsOn.png");
    lightsOffOverlay = new Image("/images/middleRoomShadow-lightsOff.png");

    // Set up lights off animation
    lightsOff =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setImage(lightsOffOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.2),
                e -> {
                  lightOverlay.setImage(lightsOnOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.4),
                e -> {
                  lightOverlay.setImage(lightsOffOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.6),
                e -> {
                  lightOverlay.setImage(lightsOnOverlay);
                }),
            new KeyFrame(
                Duration.seconds(1.2),
                e -> {
                  lightOverlay.setImage(lightsOffOverlay);
                }));
    // Set up lights on animation
    lightsOn =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setImage(lightsOnOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.2),
                e -> {
                  lightOverlay.setImage(lightsOffOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.4),
                e -> {
                  lightOverlay.setImage(lightsOnOverlay);
                }),
            new KeyFrame(
                Duration.seconds(0.6),
                e -> {
                  lightOverlay.setImage(lightsOffOverlay);
                }),
            new KeyFrame(
                Duration.seconds(1.2),
                e -> {
                  lightOverlay.setImage(lightsOnOverlay);
                }));
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
