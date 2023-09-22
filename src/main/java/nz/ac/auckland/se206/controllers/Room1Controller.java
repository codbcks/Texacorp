package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room1Controller {
  @FXML private Rectangle triggerConsole;
  @FXML private Rectangle moveRoom2;
  @FXML private SubScene topBar;
  @FXML private SubScene bottomBar;

  private static String wordToGuess;
  private static String wordList;

  @FXML
  public void initialize() throws ApiProxyException {
    topBar.setRoot(SceneManager.getUI(SceneManager.AppUI.TOPBAR));
    bottomBar.setRoot(SceneManager.getUI(SceneManager.AppUI.BOTTOMBAR));

    wordToGuess = getRandomWord();
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
    App.setRoot(SceneManager.AppUI.ROOM2);
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
