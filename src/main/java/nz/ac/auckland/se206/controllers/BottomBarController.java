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

public class BottomBarController {
  @FXML private TextArea chatTextArea;
  @FXML private Button sendButton;
  @FXML private TextArea inputText;
  @FXML private ImageView hintCounter;
  @FXML private Label chatHistoryLabel;
  @FXML private Button forwardButton;
  @FXML private Button backwardButton;

  protected ChatCompletionRequest chatCompletionRequest;
  protected static HashMap<String, String> modifiedNaming;
  protected List<ChatMessage> gptInteractionLog = new ArrayList<>();
  private int logIndex;

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
    addToLog(new ChatMessage("assistant", GptPromptEngineering.initializeBackstory()));
    switch (GameState.currentDifficulty) {
      case EASY:
        addToLog(new ChatMessage("assistant", GptPromptEngineering.setEasyHintDifficulty()));
        break;
      case MEDIUM:
        addToLog(new ChatMessage("assistant", GptPromptEngineering.setMediumHintDifficulty()));
        break;
      case HARD:
        addToLog(new ChatMessage("assistant", GptPromptEngineering.setHardHintDifficulty()));
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
    // appendChatMessage(message, "user");
    // The following code clears the entry box, writes the most recent user entry, and
    // then runs chat GPT for the user's entry
    ChatMessage msg = new ChatMessage("user", message);
    runGpt(msg, true);
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  public void appendChatMessage(String chatMessage, String role) {
    // GUI changes are added to the GUI queue
    Platform.runLater(
        () -> {
          chatTextArea.appendText("\n" + modifiedNaming.get(role) + " -> " + chatMessage);
        });
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
    addToLog(msg);
    logIndex++;
    updateChat();

    Thread gptThread =
        new Thread(
            () -> {
              try {
                chatCompletionRequest.setMessages(gptInteractionLog);
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();

                addToLog(result.getChatMessage());
                logIndex++;
                turnOnLights();

                Platform.runLater(
                    () -> {
                      updateChat();
                    });

              } catch (ApiProxyException e) {
                Platform.runLater(
                    () -> {
                      System.out.println("ERROR: Exception in GptInteraction.runGpt!");
                      e.printStackTrace();
                    });
              }
            });

    gptThread.start();
    // if (sayAloud) {
    //   App.textToSpeech.speak(getRecentLogMessage());
    // }
  }

  /**
   * Adds the chat message to the log of GPT interactions.
   *
   * @param msg the chat message to add
   */
  protected void addToLog(ChatMessage msg) {
    gptInteractionLog.add(msg);
  }

  protected String getRecentLogMessage() {
    return gptInteractionLog.get(logIndex + 1).getContent();
  }

  /**
   * Method that gets GPT to provide a hint to the player.
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

  private void turnOffLights() {
    GameState.isGPTRunning = true;
    App.room1.lightsOff();
    App.room2.lightsOff();
    App.room3.lightsOff();
  }

  private void turnOnLights() {
    GameState.isGPTRunning = false;
    App.room1.lightsOn();
    App.room2.lightsOn();
    App.room3.lightsOn();
  }

  public void removeHintCounter() {
    hintCounter.setImage(new Image("/images/countHintsUnlimited.png"));
  }

  public void setHintCounter(int remainingHints) {
    hintCounter.setImage(new Image("/images/countHints" + remainingHints + ".png"));
  }

  private void updateChat() {
    chatHistoryLabel.setText(
        String.valueOf(logIndex) + "/" + String.valueOf(gptInteractionLog.size() - 2));
    chatTextArea.clear();

    ChatMessage msg = gptInteractionLog.get(logIndex + 1);
    chatTextArea.appendText("\n" + modifiedNaming.get(msg.getRole()) + " -> " + msg.getContent());
  }

  @FXML
  private void onForwardHistory(ActionEvent event) {
    if (logIndex == gptInteractionLog.size() - 2) {
      return;
    }
    logIndex++;
    updateChat();
  }

  @FXML
  private void onBackwardHistory(ActionEvent event) {
    if (logIndex == 1) {
      return;
    }
    logIndex--;
    updateChat();
  }
}
