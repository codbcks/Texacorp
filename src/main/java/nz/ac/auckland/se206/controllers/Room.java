package nz.ac.auckland.se206.controllers;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public abstract class Room {

  protected Timeline lightsOff;
  protected Timeline lightsOn;
  protected Image lightsOffOverlay;
  protected Image lightsOnOverlay;

  protected void initializeLightAnim(ImageView img, String name, boolean isLightOn) {
    lightsOffOverlay = new Image("/images/" + name + "-lightsOff.png");
    lightsOnOverlay = new Image("/images/" + name + "-lightsOn.png");

    getLightAnim(img, name, true);
    getLightAnim(img, name, false);

    if (isLightOn) {
      img.setImage(lightsOnOverlay);
    } else {
      img.setImage(lightsOffOverlay);
    }
  }

  private void getLightAnim(ImageView img, String name, boolean turnOn) {

    Image fromState;
    Image toState;

    if (turnOn) {
      fromState = lightsOffOverlay;
      toState = lightsOnOverlay;
    } else {
      fromState = lightsOnOverlay;
      toState = lightsOffOverlay;
    }

    // Returns the light on / off
    Timeline lightAnim =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> img.setImage(toState)),
            new KeyFrame(Duration.seconds(0.2), e -> img.setImage(fromState)),
            new KeyFrame(Duration.seconds(0.4), e -> img.setImage(toState)),
            new KeyFrame(Duration.seconds(0.6), e -> img.setImage(fromState)),
            new KeyFrame(Duration.seconds(1.2), e -> img.setImage(toState)));

    if (turnOn) {
      lightsOn = lightAnim;
    } else {
      lightsOff = lightAnim;
    }
  }

  protected static KeyFrame getTranslateKeyFrame(
      double xDist, double yDist, Node nodeToMove, double durationMillis, double startDelayMillis) {
    return new KeyFrame(
        Duration.millis(startDelayMillis),
        e -> {
          TranslateTransition tempTransition =
              new TranslateTransition(Duration.millis(durationMillis), nodeToMove);
          tempTransition.setByX(xDist);
          tempTransition.setByY(yDist);
          tempTransition.setInterpolator(Interpolator.LINEAR);
          tempTransition.play();
        });
  }
}
