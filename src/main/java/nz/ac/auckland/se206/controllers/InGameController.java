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
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class InGameController {
  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;
  @FXML private SubScene room1;
  @FXML private SubScene room2;
  @FXML private SubScene room3;
  @FXML private Pane roomSlider;
  @FXML private ImageView leftArrow;
  @FXML private ImageView rightArrow;
  @FXML private Pane loseScreen;

  Boolean moveEnabled = true;
  int currentRoom = 2;

  @FXML
  public void initialize() throws ApiProxyException {
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));
    room1.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM1));
    room2.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM2));
    room3.setRoot(SceneManager.getUI(SceneManager.AppUI.ROOM3));
  }

  @FXML
  public void clickBackToMenu(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.INTRO);
    ChallengeTimer.cancelTimer();
    App.resetGame();
  }

  public void showLose() {
    loseScreen.setVisible(true);
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
