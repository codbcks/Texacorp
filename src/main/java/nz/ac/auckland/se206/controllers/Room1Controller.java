package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room1Controller {
  @FXML private Rectangle triggerConsole;
  @FXML private Rectangle moveRoom2;
  @FXML private Rectangle lightOverlay;
  @FXML private Rectangle printerPromptTrigger;

  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;

  private Timeline lightsOff;
  private Timeline lightsOn;

  private static String wordToGuess;
  private static String wordList;

  @FXML
  public void initialize() throws ApiProxyException {
    setSubScenes();

    wordToGuess = getRandomWord();

    lightsOff =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.3)));

    lightsOn =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.0)));
  }

  public void lightsOff() {
    lightsOff.playFromStart();
  }

  public void lightsOn() {
    lightsOn.playFromStart();
  }

  private String getRandomWord() {
    wordList = "star,laser,satellite,cat,potato,computer,mouse,pyramid,phone,camera";
    // Splitting the list into an array of individual entries
    return wordList.split(",")[(int) (Math.random() * 5)];
  }

  // Generate a riddle using GPT and set the word to guess
  @FXML
  public void generateRiddle(MouseEvent event) throws ApiProxyException {
    String riddle = GptPromptEngineering.getRiddleWithGivenWord(wordToGuess);
    App.bottomBarController.appendChatMessage(riddle, "assistant");
  }

  @FXML
  public void clickTriggerConsole(MouseEvent event) throws IOException {
    try {
      App.bottomBarController.runGpt(
          // runGpt is a method in the parent class, it returns the GPT response for the input.
          new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord(wordToGuess)), false);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void clickMoveRoom2(MouseEvent event) throws IOException {

    unsetSubScenes();
    ((Room2Controller) SceneManager.getController(SceneManager.AppUI.ROOM2)).setSubScenes();
    App.setRoot(SceneManager.AppUI.ROOM2);
  }

  @FXML
  public void setSubScenes() {
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));
  }

  @FXML
  public void unsetSubScenes() {
    topBar.setRoot(new Region());
    bottomBar.setRoot(new Region());
  }

  /* ------- NOTE: This is how we will be animating items into the inventory --------

    TranslateTransition translate = new TranslateTransition();
    translate.setByX((INVX + (INVGAPX * invIndex)) - imageToMove.getLayoutX());
    translate.setByY(INVY - imageToMove.getLayoutY());
    translate.setDuration(Duration.millis(600));
    translate.setNode(imageToMove);
    translate.play();

  */

  @FXML
  public void printerPrompt(MouseEvent event) throws ApiProxyException {
    ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
        .appendChatMessage(
            "Two 3D printers loaded with high-tensile steel. Pefect for producing a durable saw"
                + " blade.",
            "user");
  }
}
