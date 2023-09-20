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

  @FXML private Rectangle moveRoom1;
  @FXML private Rectangle moveRoom3;
  @FXML private Rectangle lightOverlay;
  @FXML private ImageView sawBody;

  private Timeline lightsOff;
  private Timeline lightsOn;

  @FXML
  public void initialize() throws ApiProxyException {

    lightsOff =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setOpacity(0.3);
                }),
            new KeyFrame(
                Duration.seconds(0.25),
                e -> {
                  lightOverlay.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.5),
                e -> {
                  lightOverlay.setOpacity(0.3);
                }),
            new KeyFrame(
                Duration.seconds(0.75),
                e -> {
                  lightOverlay.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(1.0),
                e -> {
                  lightOverlay.setOpacity(0.3);
                }));

    lightsOn =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.0),
                e -> {
                  lightOverlay.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.25),
                e -> {
                  lightOverlay.setOpacity(0.3);
                }),
            new KeyFrame(
                Duration.seconds(0.5),
                e -> {
                  lightOverlay.setOpacity(0);
                }),
            new KeyFrame(
                Duration.seconds(0.75),
                e -> {
                  lightOverlay.setOpacity(0.3);
                }),
            new KeyFrame(
                Duration.seconds(1.0),
                e -> {
                  lightOverlay.setOpacity(0);
                }));
  }

  public void lightsOff() {
    lightsOff.playFromStart();
  }

  public void lightsOn() {
    lightsOn.playFromStart();
  }

  @FXML
  public void clickMoveRoom3(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM3);
  }

  @FXML
  public void clickMoveRoom1(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM1);
  }

  @FXML
  public void clickSawBody(MouseEvent event) throws IOException {
    if (GameState.isGPTRunning) {
      // App.topBarController.giveItem(TopBarController.Item.SAW_BODY);
      System.out.println("sawbody collected");
    }
  }
}
