package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GptInteraction;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** Controller class for the chat view. */
public class ChatController extends GptInteraction {
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private Label labelTimer;
  private static String wordList;
  private static String wordToGuess;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
    chatTextArea.appendText("\n");

    GameState.chatTimerLabel = labelTimer;

    // Specifying a list of different words for the player to guess
    wordList = "star,laser,satellite,cat,potato,computer,mouse,pyramid,phone,camera";
    // Splitting the list into an array of individual entries
    wordToGuess = wordList.split(",")[(int) (Math.random() * 5)];

    try {
      runGpt(
          // runGpt is a method in the parent class, it returns the GPT response for the given
          // input.
          new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord(wordToGuess)), false);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  protected void appendChatMessage(String chatMessage, String role) {
    // GUI changes are added to the GUI queue
    Platform.runLater(
        () -> {
          chatTextArea.appendText("\n" + modifiedNaming.get(role) + " -> " + chatMessage);
        });
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      // If message is empty, don't do anything.
      return;
    } else if (message.contains(wordToGuess)) {
      // If message has contents, pass it to ChatGPT
      GameState.isLaserActive = false;
      leaveChat();
      return;
    }
    appendChatMessage(inputText.getText(), "user");
    // The following code clears the entry box, writes the most recent user entry, and
    // then runs chat GPT for the user's entry
    inputText.clear();
    ChatMessage msg = new ChatMessage("user", message);
    runGpt(msg, true);
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    leaveChat();
  }

  private void leaveChat() throws IOException {
    // Challenge timer's GUI timer pointer is changed.
    ChallengeTimer.setCurrentLabelTimer(GameState.roomTimerLabel);
    App.setRoot(SceneManager.AppUI.ROOM);
  }
}
