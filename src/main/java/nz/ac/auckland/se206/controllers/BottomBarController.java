package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/**
 * This class represents the controller for the bottom bar of the game UI. It handles user input and
 * communication with the GPT-3 API for generating responses to the user's messages. It also
 * maintains a log of the conversation history and provides methods for adding messages to the log
 * and updating the UI to display the conversation history.
 */
public class BottomBarController {
  @FXML private TextArea chatTextArea;
  @FXML private Button sendButton;
  @FXML private TextArea inputText;
  @FXML private ImageView hintCounter;
  @FXML private Label chatHistoryLabel;
  @FXML private Button forwardButton;
  @FXML private Button backwardButton;

  private ChatCompletionRequest chatCompletionRequest;
  private static HashMap<String, String> modifiedNaming;
  private int logIndex;
  private String previousMessageRole;
  private boolean previousEnivroClick;
  private List<List<ChatMessage>> orderedGptInteractionLog = new ArrayList<>();

  public void initialize() throws ApiProxyException {
    // initialise css style classes
    inputText.getStyleClass().add("terminal-text-area");

    // initialise GPT
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);

    modifiedNaming = new HashMap<String, String>();
    modifiedNaming.put("assistant", "AI");
    modifiedNaming.put("user", "YOU");

    logIndex = 0;
    chatHistoryLabel.setText(logIndex + "/0");
    previousMessageRole = "assistant";
    previousEnivroClick = false;

    /* Pressing enter will send through the player's inputs */
    inputText.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            String message = inputText.getText().trim();
            if (!message.isEmpty()) {
              try {
                onSendMessage(null);
              } catch (ApiProxyException | IOException e) {
                e.printStackTrace();
              }
            }
            event.consume();
          }
        });
  }

  /**
   * Method that provides GPT with its backstory for the game. A different story is provided
   * depending on the difficulty.
   */
  public void giveBackstory() {
    addToLog(new ChatMessage("assistant", GptPromptEngineering.initializeBackstory()), false);
    switch (GameState.currentDifficulty) {
      case EASY:
        addToLog(new ChatMessage("assistant", GptPromptEngineering.setEasyHintDifficulty()), false);
        break;
      case MEDIUM:
        addToLog(
            new ChatMessage("assistant", GptPromptEngineering.setMediumHintDifficulty()), false);
        break;
      case HARD:
        addToLog(new ChatMessage("assistant", GptPromptEngineering.setHardHintDifficulty()), false);
        break;
    }
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
    inputText.clear();
    if (message == null || message.trim().isEmpty()) {
      // If message is empty, don't do anything.
      return;
    }
    // The following code clears the entry box, writes the most recent user entry, and
    // then runs chat GPT for the user's entry
    ChatMessage msg = new ChatMessage("user", message);
    runGpt(msg, true);
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  void runGpt(ChatMessage msg, boolean sayAloud) throws ApiProxyException {
    turnOffLights();
    addToLog(msg, false);
    updateChat();

    Thread gptThread =
        new Thread(
            () -> {
              try {
                chatCompletionRequest.setMessages(
                    orderedGptInteractionLog.get(orderedGptInteractionLog.size() - 1));
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();

                addToLog(result.getChatMessage(), false);
                turnOnLights();

                Platform.runLater(
                    () -> {
                      updateChat();
                    });

                if (sayAloud) {
                  App.getTextToSpeech().speak(getRecentLogMessage());
                }

              } catch (ApiProxyException e) {
                Platform.runLater(
                    () -> {
                      System.out.println("ERROR: Exception in GptInteraction.runGpt!");
                      e.printStackTrace();
                    });
              }
            });

    gptThread.start();
  }

  /**
   * Gets the most recent log message.
   *
   * @return the most recent log message
   */
  private String getRecentLogMessage() {
    return orderedGptInteractionLog
        .get(orderedGptInteractionLog.size() - 1)
        .get(orderedGptInteractionLog.get(orderedGptInteractionLog.size() - 1).size() - 1)
        .getContent();
  }

  /**
   * Adds the chat message to the log of GPT interactions.
   *
   * @param msg the chat message to add
   */
  public void addToLog(ChatMessage msg, boolean enviroClick) {

    if (msg.getRole().equals("assistant")
        && previousMessageRole.equals("user")
        && !enviroClick
        && !previousEnivroClick) {
      // save to same list
      orderedGptInteractionLog.get(orderedGptInteractionLog.size() - 1).add(msg);
    } else {
      // save to new list
      orderedGptInteractionLog.add(new ArrayList<ChatMessage>());
      orderedGptInteractionLog.get(orderedGptInteractionLog.size() - 1).add(msg);
      logIndex = orderedGptInteractionLog.size();
    }

    previousMessageRole = msg.getRole();
    previousEnivroClick = enviroClick;
  }

  /**
   * Provides a hint to the player using the GPT model.
   *
   * @return the hint
   */
  public String provideHint() {
    try {
      if (chatCompletionRequest == null) {
        chatCompletionRequest =
            new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);
      }
      chatCompletionRequest.addMessage(new ChatMessage("user", "Can you provide a hint?"));
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      return result.getChatMessage().getContent();
    } catch (ApiProxyException e) {
      System.out.println("Exception in GptInteraction.provideHint()");
      e.printStackTrace();
      return "An error has occurred. Please try again.";
    }
  }

  /** Turns off the lights in all rooms. */
  private void turnOffLights() {
    GameState.isGPTRunning = true;
    App.getRoom1().lightsOff();
    App.getRoom2().lightsOff();
    App.getRoom3().lightsOff();
  }

  /** Turns on the lights in all rooms. */
  private void turnOnLights() {
    GameState.isGPTRunning = false;
    App.getRoom1().lightsOn();
    App.getRoom2().lightsOn();
    App.getRoom3().lightsOn();
  }

  /** Removes the hint counter. */
  public void removeHintCounter() {
    hintCounter.setImage(new Image("/images/countHintsUnlimited.png"));
  }

  /**
   * Sets the hint counter to a given value.
   *
   * @param remainingHints the number of remaining hints
   */
  public void setHintCounter(int remainingHints) {
    hintCounter.setImage(new Image("/images/countHints" + remainingHints + ".png"));
  }

  /** Updates the chat history display. */
  public void updateChat() {
    chatHistoryLabel.setText(
        String.valueOf(logIndex - 2) + "/" + String.valueOf(orderedGptInteractionLog.size() - 2));
    chatTextArea.clear();

    for (ChatMessage msg : orderedGptInteractionLog.get(logIndex - 1)) {
      chatTextArea.appendText("\n" + modifiedNaming.get(msg.getRole()) + " -> " + msg.getContent());
    }
  }

  /**
   * Moves the chat history display forward.
   *
   * @param event the action event triggered by the forward button
   */
  @FXML
  private void clickForwardHistory(ActionEvent event) {
    if (logIndex >= orderedGptInteractionLog.size()) {
      return;
    }
    logIndex++;
    updateChat();
  }

  /**
   * Moves the chat history display backward.
   *
   * @param event the action event triggered by the backward button
   */
  @FXML
  private void clickBackwardHistory(ActionEvent event) {
    if (logIndex <= 3) {
      return;
    }
    logIndex--;
    updateChat();
  }
}
