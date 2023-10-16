package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class Intro2Controller {

  @FXML private TextArea introductoryText;
  @FXML private Label continueLabel;
  @FXML private ImageView background;
  @FXML private ImageView speakerImage;

  private String message;

  public void initialize() throws ApiProxyException {

    introductoryText
        .getStylesheets()
        .add(getClass().getResource("/css/styles.css").toExternalForm());
    introductoryText.getStyleClass().add("introductory-text-area");
    continueLabel.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    continueLabel.getStyleClass().add("continue-label");

    GaussianBlur gaussianBlur = new GaussianBlur();
    gaussianBlur.setRadius(63);
    background.setEffect(gaussianBlur);

    continueLabel.setVisible(false);
    continueLabel.setOnMouseClicked(
        event -> {
          try {
            startGame();
          } catch (IOException e) {
            // if game doesn't start
            e.printStackTrace();
          }
        });
    continueLabel.setCursor(Cursor.HAND);

    message = getGptMessage();
  }

  private String getGptMessage() {
    String message = "";
    ChatCompletionRequest chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.4).setTopP(0.5).setMaxTokens(100);
    chatCompletionRequest.addMessage(
        new ChatMessage("user", GptPromptEngineering.getIntroductoryMessage()));
    try {
      // Try catch for accessing ChatGPT
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      if (GameState.isTextToSpeechOn) {
        // say aloud specifies whether the program should access text to speech or not
        App.textToSpeech.speak(result.getChatMessage().getContent());
      }
      message = result.getChatMessage().getContent();
    } catch (ApiProxyException e) {
      // Exception handling
      System.out.println("ERROR: Exception in GptInteraction.runGpt!");
      e.printStackTrace();
    }
    return message;
  }

  public void startGame() throws IOException {
    /* Set challenge timer */
    ChallengeTimer.setCurrentLabelTimer(
        App.topBarController.getTimerLabel(), GameState.timeSetting);
    ChallengeTimer.startTimer();

    /* This is the entry point for the game */
    App.setRoot(SceneManager.AppUI.IN_GAME);

    if (GameState.currentDifficulty == GameState.Difficulty.EASY) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .removeHintCounter();
    } else if (GameState.currentDifficulty == GameState.Difficulty.HARD) {
      ((BottomBarController) SceneManager.getController(SceneManager.AppUI.BOTTOMBAR))
          .setHintCounter(0);
    }

    App.bottomBarController.giveBackstory();
  }

  public void typeText(TextArea textArea, String content) {
    Timeline timeline = new Timeline();
    Duration delay = Duration.millis(2000);
    final StringBuilder text = new StringBuilder();

    for (char character : content.toCharArray()) {
      KeyFrame keyFrame =
          new KeyFrame(
              delay,
              event -> {
                text.append(character);
                textArea.setText(text.toString());
              });

      timeline.getKeyFrames().add(keyFrame);
      delay = delay.add(Duration.millis(75));
    }

    timeline.setOnFinished(
        e -> {
          continueLabel.setVisible(true);
          flashLabel(continueLabel);
        });
    timeline.play();
  }

  private void flashLabel(Label label) {
    final Timeline timeline = new Timeline();

    timeline.setCycleCount(Timeline.INDEFINITE);

    final KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.5), event -> label.setOpacity(0.5));
    final KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1), event -> label.setOpacity(1));

    timeline.getKeyFrames().addAll(keyFrame1, keyFrame2);
    timeline.play();
  }

  public void startPrintingText() {
    typeText(introductoryText, message);
  }
}
