package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room1Controller {
  @FXML private Rectangle triggerConsole;
  @FXML private Rectangle moveRoom2;
  @FXML private Rectangle lightOverlay;

  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;
  @FXML private Label terminalInstructionsLabel;
  @FXML private Label terminalLabel;
  @FXML private Pane terminalPane;
  @FXML private Pane terminalWrapperPane;
  @FXML private PasswordField riddleAnswerEntry;

  private Timeline lightsOff;
  private Timeline lightsOn;

  private static String wordToGuess;
  private static String wordList;

  @FXML
  public void initialize() throws ApiProxyException {
    setSubScenes();

    riddleAnswerEntry.getStyleClass().add("riddle-answer-entry");
    terminalInstructionsLabel.getStyleClass().add("terminal-label");
    terminalLabel.getStyleClass().add("terminal-label");

    wordToGuess = getRandomWord();

    terminalWrapperPane.setOnMouseClicked(
        event -> {
          double x = event.getX();
          double y = event.getY();

          if (x < terminalPane.getLayoutX()
              || x > terminalPane.getLayoutX() + terminalPane.getWidth()
              || y < terminalPane.getLayoutY()
              || y > terminalPane.getLayoutY() + terminalPane.getHeight()) {
            hideTerminal();
          }
        });

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
    if (GameState.isFirstTime) {
      try {
        App.bottomBarController.runGpt(
            // runGpt is a method in the parent class, it returns the GPT response for the input.
            new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord(wordToGuess)),
            false);
      } catch (ApiProxyException e) {
        e.printStackTrace();
      }
      GameState.isFirstTime = false;
    } else if (!GameState.isFirstTime && !GameState.isPasswordObtained) {
      showTerminal();
    } else {
      return;
    }
  }

  @FXML
  private void hideTerminal() {
    terminalWrapperPane.setVisible(false);
    terminalPane.setVisible(false);
    TranslateTransition translateTransition =
        new TranslateTransition(Duration.millis(1000), terminalPane);
    translateTransition.setByY(120);
    terminalPane.setTranslateY(0);
  }

  @FXML
  private void showTerminal() {
    terminalWrapperPane.setVisible(true);
    terminalPane.setVisible(true);
    App.textToSpeech.speak("The... password... is...");
    TranslateTransition translateTransition =
        new TranslateTransition(Duration.millis(1000), terminalPane);
    translateTransition.setByY(-120);
    translateTransition.play();
    riddleAnswerEntry.requestFocus();
  }

  @FXML
  private void submitGuess(ActionEvent event) {
    String guess = riddleAnswerEntry.getText();
    if (guess.equalsIgnoreCase(wordToGuess)) {
      App.bottomBarController.appendChatMessage("Success!", "user");
      hideTerminal();
    } else {
      App.bottomBarController.appendChatMessage("Declined!", "assistant");
      riddleAnswerEntry.clear();
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
}
