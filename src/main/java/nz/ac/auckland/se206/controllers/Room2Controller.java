package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room2Controller {

  @FXML private Rectangle lightOverlay;
  @FXML private ImageView sawBody;
  @FXML private ImageView laser;
  @FXML private Rectangle clockPromptTrigger;
  @FXML private Rectangle notesPromptTrigger;

  private Timeline lightsOff;
  private Timeline lightsOn;

  @FXML
  public void initialize() throws ApiProxyException {
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

  public void lightsOff() {
    lightsOff.playFromStart();
  }

  public void lightsOn() {
    lightsOn.playFromStart();
  }

  @FXML
  public void clickSawBody(MouseEvent event) throws IOException {
    // If the gpt is running/room turned off, give the player the saw body when clicked
    if (GameState.isGPTRunning) {
      App.topBarController.giveItem(TopBarController.Item.SAW_BODY);
      sawBody.setVisible(false);
    } else {
      SceneManager.appendChatMessage(
          "I'm pretty sure that 7 deadly lasers is above the recommended daily allowance.", "user");
    }
  }

  @FXML
  public void clickExit(MouseEvent event) throws IOException {
    // If the player has all the items, go to the win screen
    if (App.topBarController.hasItem(TopBarController.Item.SAW_BODY)
        && App.topBarController.hasItem(TopBarController.Item.SAW_BLADE)
        && App.topBarController.hasItem(TopBarController.Item.SAW_BATTERY)) {
      App.setRoot(SceneManager.AppUI.WIN);
    }
  }

  @FXML
  public void clockPrompt(MouseEvent event) throws IOException {
    SceneManager.appendChatMessage("This clock is stuck on 0, it must be broken.", "user");
  }

  @FXML
  public void notesPrompt(MouseEvent event) throws IOException {
    SceneManager.appendChatMessage("6 blank sticky notes... weird.", "user");
  }
}
