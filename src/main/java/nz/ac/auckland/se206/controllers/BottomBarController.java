package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
  @FXML private Button hintButton;

  protected ChatCompletionRequest chatCompletionRequest;
  protected static HashMap<String, String> modifiedNaming;
  protected List<ChatMessage> gptInteractionLog = new ArrayList<>();

  public void initialize() throws ApiProxyException {
    // initialise css style classes
    inputText.getStyleClass().add("terminal-text-area");

    // initialise GPT
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);

    modifiedNaming = new HashMap<String, String>();
    modifiedNaming.put("assistant", "AI");
    modifiedNaming.put("user", "YOU");

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
   * Method for the hint button. A different hint is provided depending on the stage of the game.
   *
   * @throws ApiProxyException
   */
  public void onRequestHint(ActionEvent event) throws ApiProxyException {
    runGptForHint(new ChatMessage("user", GptPromptEngineering.getHint()));
  }

  private void runGptForHint(ChatMessage chatMessage) {
    addToLog(chatMessage);
    Task<Void> runGptTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // The following code leverages the appendChatMessage function which is implemeneted in
            // all children of this class
            Platform.runLater(
                () -> {
                  appendChatMessage("Processing...", "assistant");
                });
            chatCompletionRequest.addMessage(chatMessage);
            try {
              // Try catch for accessing ChatGPT
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();
              chatCompletionRequest.addMessage(result.getChatMessage());
              Platform.runLater(
                  () -> {
                    appendChatMessage(
                        result.getChatMessage().getContent(), result.getChatMessage().getRole());
                  });
              if (GameState.isTextToSpeechOn) {
                // say aloud specifies whether the program should access text to speech or not
                App.textToSpeech.speak(result.getChatMessage().getContent());
              }
            } catch (ApiProxyException e) {
              // Exception handling
              System.out.println("ERROR: Exception in GptInteraction.runGpt!");
              e.printStackTrace();
            }
            return null;
          }
        };

    // The GPT thread runnable is a Task so that it can be bound to a GUI element later on
    Thread runGptThread = new Thread(runGptTask);
    runGptThread.start();
  }

  /**
   * Method that provides GPT with its backstory for the game. A different story is provided
   * depending on the difficulty.
   */
  public void giveBackstory() {
    provideBackStory(GptPromptEngineering.initializeBackstory());
    GameState.Difficulty difficulty = GameState.getCurrentDifficulty();
    switch (difficulty) {
      case EASY:
        provideBackStory(GptPromptEngineering.setEasyHintDifficulty());
        break;
      case MEDIUM:
        provideBackStory(GptPromptEngineering.setMediumHintDifficulty());
        break;
      case HARD:
        provideBackStory(GptPromptEngineering.setHardHintDifficulty());
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
    appendChatMessage(message, "user");
    // The following code clears the entry box, writes the most recent user entry, and
    // then runs chat GPT for the user's entry
    ChatMessage msg = new ChatMessage("user", message);
    runGpt(msg);
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
  protected void runGpt(ChatMessage msg) throws ApiProxyException {
    turnOffLights();
    addToLog(msg);
    Task<Void> runGptTask = createRunGptTask();
    Thread runGptThread = new Thread(runGptTask);
    runGptThread.start();
  }

  private Task<Void> createRunGptTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        processGptResponse();
        return null;
      }
    };
  }

  private void processGptResponse() {
    appendChatMessage("Processing...", "assistant");
    try {
      String gptResponse = getGptResponse();
      handleGptResponse(gptResponse);
    } catch (ApiProxyException e) {
      handleGptError(e);
    } finally {
      turnOnLights();
    }
  }

  private String getGptResponse() throws ApiProxyException {
    chatCompletionRequest.setMessages(gptInteractionLog);
    ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
    Choice result = chatCompletionResult.getChoices().iterator().next();
    return result.getChatMessage().getContent();
  }

  private void handleGptResponse(String gptResponse) {
    if (gptResponse.startsWith("HINT #") || gptResponse.contains("hint")) {
      handleHintResponse(gptResponse);
    } else {
      Platform.runLater(() -> appendChatMessage(gptResponse, "assistant"));
    }
  }

  private void handleHintResponse(String gptResponse) {
    Platform.runLater(
        () -> appendChatMessage("Hints are not given through the chat!", "assistant"));
    if (GameState.isTextToSpeechOn) {
      App.textToSpeech.speak("Hints are not given through the chat!");
    }
  }

  private void handleGptError(ApiProxyException e) {
    System.out.println("Error. Exception in GptInteraction.runGpt");
    e.printStackTrace();
  }

  /**
   * Adds the chat message to the log of GPT interactions.
   *
   * @param msg the chat message to add
   */
  protected void addToLog(ChatMessage msg) {
    gptInteractionLog.add(msg);
  }

  /**
   * Clears the GPT interaction log. Initial thinking was that this would help moderate token use.
   */
  public void clearLog() {
    gptInteractionLog.clear();
  }

  /**
   * Method that provides GPT with its backstory for the game.
   *
   * @param story
   */
  public void provideBackStory(String story) {
    addToLog(new ChatMessage("assistant", story));
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

  public List<ChatMessage> getGptInteractionlog() {
    return gptInteractionLog;
  }
}
