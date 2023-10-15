package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/**
 * This class is the controller for the in-game scene. It controls the movement of the room slider
 * and the display of the top and bottom bars.
 */
public class InGameController {
  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;
  @FXML private SubScene room1;
  @FXML private SubScene room2;
  @FXML private SubScene room3;
  @FXML private Pane roomSlider;
  @FXML private ImageView leftArrow;
  @FXML private ImageView rightArrow;

  private Boolean moveEnabled = true;
  private int currentRoom = 2;

  @FXML
  public void initialize() throws ApiProxyException {
    topBar.setRoot(SceneManager.getInterface(SceneManager.AppInterface.TOPBAR));
    bottomBar.setRoot(SceneManager.getInterface(SceneManager.AppInterface.BOTTOMBAR));
    room1.setRoot(SceneManager.getInterface(SceneManager.AppInterface.ROOM1));
    room2.setRoot(SceneManager.getInterface(SceneManager.AppInterface.ROOM2));
    room3.setRoot(SceneManager.getInterface(SceneManager.AppInterface.ROOM3));
  }

  @FXML
  public void moveLeft(MouseEvent event) throws IOException {
    if (currentRoom != 1) {
      currentRoom--;
      moveRoomSlider(App.ROOM_WIDTH);
    }
  }

  @FXML
  public void moveRight(MouseEvent event) throws IOException {
    if (currentRoom != 3) {
      currentRoom++;
      moveRoomSlider(-App.ROOM_WIDTH);
    }
  }

  public void moveRoomSlider(int distance) {
    if (moveEnabled) {
      setMoveEnable(false);
      TranslateTransition slide = new TranslateTransition(Duration.millis(400), roomSlider);
      slide.setByX(distance);

      Timeline moveRooms =
          new Timeline(
              new KeyFrame(
                  Duration.seconds(0.0),
                  e -> {
                    slide.play();
                  }),
              new KeyFrame(
                  Duration.seconds(0.4),
                  e -> {
                    setMoveEnable(true);
                  }));

      moveRooms.play();
    }
  }

  private void setMoveEnable(boolean condition) {
    moveEnabled = condition;

    if (!condition) {
      leftArrow.setVisible(false);
      rightArrow.setVisible(false);
    } else {
      if (currentRoom != 1) {
        leftArrow.setVisible(true);
      }
      if (currentRoom != 3) {
        rightArrow.setVisible(true);
      }
    }
  }
}
