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
import nz.ac.auckland.se206.GameState.Difficulty;
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
  protected ChatCompletionRequest hintCompletionRequest;
  protected static HashMap<String, String> modifiedNaming;
  protected List<ChatMessage> gptInteractionLog = new ArrayList<>();

  public void initialize() throws ApiProxyException {

    // initialise css style classes
    inputText.getStyleClass().add("terminal-text-area");

    // initialise GPT
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);

    hintCompletionRequest =
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

  /**
   * Method that runs GPT for a hint. Somewhat similar to running GPT for chat, but with checks for
   * the response from GPT and the hint counter.
   *
   * @param chatMessage the chat message to process.
   * @see #runGpt(ChatMessage)
   */
  private void runGptForHint(ChatMessage chatMessage) {
    addToLog(chatMessage);
    Task<Void> runGptHintTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // The following code leverages the appendChatMessage function which is implemented in
            // all children of this class
            Platform.runLater(
                () -> {
                  appendChatMessage("Processing...", "assistant");
                });
            hintCompletionRequest.addMessage(chatMessage);
            try {
              // Try catch for accessing ChatGPT
              ChatCompletionResult hintCompletionResult = hintCompletionRequest.execute();
              Choice result = hintCompletionResult.getChoices().iterator().next();
              hintCompletionRequest.addMessage(result.getChatMessage());
              if (GameState.hintsRemaining == 0) {
                Platform.runLater(
                    () -> {
                      appendChatMessage("No hints available!", "assistant");
                    });
                if (GameState.isTextToSpeechOn) {
                  App.textToSpeech.speak("No hints available!");
                }
              } else {
                if (result.getChatMessage().getContent().startsWith("HINT:")) {
                  Platform.runLater(
                      () -> {
                        appendChatMessage(
                            result.getChatMessage().getContent(),
                            result.getChatMessage().getRole());
                      });
                  if (GameState.getCurrentDifficulty() == Difficulty.MEDIUM) {
                    GameState.hintsRemaining--;
                    Platform.runLater(
                        () -> {
                          setHintCounter();
                        });
                    if (GameState.isTextToSpeechOn) {
                      // say aloud specifies whether the program should access text to speech or
                      // not
                      App.textToSpeech.speak(result.getChatMessage().getContent());
                    }
                  }
                }
              }
            } catch (ApiProxyException e) {
              // Exception handling
              System.out.println("ERROR: Exception in GptInteraction.runGptForHint!");
              e.printStackTrace();
            }
            return null;
          }
        };
    // The GPT thread runnable is a Task so that it can be bound to a GUI element later on
    Thread runGptHintThread = new Thread(runGptHintTask);
    runGptHintThread.start();
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

  /**
   * Creates a task that runs the GPT model.
   *
   * @return the task.
   */
  private Task<Void> createRunGptTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        processGptResponse();
        return null;
      }
    };
  }

  /** Processes the response from the GPT model. */
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

  /**
   * Gets the response from the GPT model.
   *
   * @return the response.
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private String getGptResponse() throws ApiProxyException {
    chatCompletionRequest.setMessages(gptInteractionLog);
    ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
    Choice result = chatCompletionResult.getChoices().iterator().next();
    return result.getChatMessage().getContent();
  }

  /**
   * Handles the response from the GPT model.
   *
   * @param gptResponse the response for GPT.
   */
  private void handleGptResponse(String gptResponse) {
    if (!GameState.isRiddleGiven) {
      Platform.runLater(() -> appendChatMessage(gptResponse, "assistant"));
    } else {
      if ((gptResponse.startsWith("HINT:") || gptResponse.contains("hint"))) {
        handleHintResponse(gptResponse);
      } else {
        Platform.runLater(() -> appendChatMessage(gptResponse, "assistant"));
      }
    }
  }

  /**
   * Handles when a hint is attempted to be provided by GPT through illegal means.
   *
   * @param gptResponse the response from GPT.
   */
  private void handleHintResponse(String gptResponse) {
    Platform.runLater(
        () -> appendChatMessage("Hints are not given through the chat!", "assistant"));
    if (GameState.isTextToSpeechOn) {
      App.textToSpeech.speak("Hints are not given through the chat!");
    }
  }

  /**
   * Handles errors from the GPT model.
   *
   * @param e the exception.
   */
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

  /** Turns off the lights in all rooms. */
  private void turnOffLights() {
    GameState.isGPTRunning = true;
    App.room1.lightsOff();
    App.room2.lightsOff();
    App.room3.lightsOff();
  }

  /** Turns on the lights in all rooms. */
  private void turnOnLights() {
    GameState.isGPTRunning = false;
    App.room1.lightsOn();
    App.room2.lightsOn();
    App.room3.lightsOn();
  }

  /** Method to display infinity in the hint counter area for EASY difficulty. */
  public void removeHintCounter() {
    hintCounter.setImage(new Image("/images/countHintsUnlimited.png"));
  }

  /**
   * Method to display the number of hints remaining in the hint counter area for MEDIUM difficulty
   * (and HARD, though only called once).
   */
  public void setHintCounter() {
    hintCounter.setImage(new Image("/images/countHints" + GameState.hintsRemaining + ".png"));
  }

  /**
   * Getter method for the GPT interaction log.
   *
   * @return the GPT interaction log.
   */
  public List<ChatMessage> getGptInteractionlog() {
    return gptInteractionLog;
  }
}
