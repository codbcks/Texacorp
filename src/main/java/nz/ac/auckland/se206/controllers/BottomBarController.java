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

  protected ChatCompletionRequest chatCompletionRequest;
  protected static HashMap<String, String> modifiedNaming;
  protected List<ChatMessage> gptInteractionLog = new ArrayList<>();

  public void initialize() throws ApiProxyException {
    inputText.getStyleClass().add("terminal-text-area");

    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);

    modifiedNaming = new HashMap<String, String>();
    modifiedNaming.put("assistant", "AI");
    modifiedNaming.put("user", "YOU");

    /* Setting the enter button key press */
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

  public void giveBackstory() {
    provideBackStory(GptPromptEngineering.initializeBackstory());
    switch (GameState.currentDifficulty) {
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
    } else if (message.toLowerCase().contains("hint")
        || message.toLowerCase().contains("another")
        || message.toLowerCase().contains("help")) {
      if (GameState.hintsRemaining > 0) {
        GameState.hintsRemaining--;
        appendChatMessage(provideHint(), "assistant");
      } else {
        appendChatMessage("No more hints!", "assistant");
        return;
      }
    }

    appendChatMessage(inputText.getText(), "user");
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

  // A helper function to avoid repeating code when prompting gpt
  private void gptPromptHelper(String prompt) {
    try {
      runGpt(new ChatMessage("user", prompt), true);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  protected void runGpt(ChatMessage msg, boolean sayAloud) throws ApiProxyException {

    turnOffLights();

    addToLog(msg);
    Task<Void> runGptTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            // The following code leverages the appendChatMessage function which is implemeneted in
            // all children of this class
            appendChatMessage("Processing...", "assistant");
            chatCompletionRequest.setMessages(gptInteractionLog);
            try {
              // Try catch for accessing ChatGPT
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();
              chatCompletionRequest.addMessage(result.getChatMessage());
              appendChatMessage(
                  result.getChatMessage().getContent(), result.getChatMessage().getRole());

              turnOnLights();

              GameState.isGPTRunning = false;
              App.room2.lightsOn();

              if (sayAloud) {
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
}
