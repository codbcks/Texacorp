package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.GameState.Difficulty;
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

  private static HashMap<String, String> modifiedNaming;

  @FXML private TextArea chatTextArea;
  @FXML private Button sendButton;
  @FXML private TextArea inputText;
  @FXML private ImageView hintCounter;

  @FXML private Button hintButton;

  private ChatCompletionRequest chatCompletionRequest;
  private ChatCompletionRequest hintCompletionRequest;
  private List<ChatMessage> gptChatLog = new ArrayList<>();
  private List<ChatMessage> hintLog = new ArrayList<>();

  @FXML private Label chatHistoryLabel;
  @FXML private Button forwardButton;
  @FXML private Button backwardButton;

  private int logIndex;
  private int riddleIndex;
  private String previousMessageRole;
  private boolean previousEnivroClick;
  private List<List<ChatMessage>> orderedGptInteractionLog = new ArrayList<>();

  /**
   * This method initializes the BottomBarController by configuring CSS styles, initializing a chat
   * completion request, and setting up event listeners.
   *
   * @throws ApiProxyException if there is an issue with the API proxy
   */
  public void initialize() throws ApiProxyException {

    // initialise css style classes
    inputText.getStyleClass().add("terminal-text-area");

    // initialise GPT
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(100);

    hintCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.25).setTopP(0.8).setMaxTokens(100);

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
   * Method for the hint button. A different hint is provided depending on the stage of the game.
   *
   * @throws ApiProxyException
   */
  public void onRequestHint(ActionEvent event) throws ApiProxyException {
    Platform.runLater(
        () -> {
          hintButton.setDisable(true);
        });
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

    addToLog(chatMessage, false, true, true);
    Task<Void> runGptHintTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // The following code leverages the appendChatMessage function which is implemented in
            // all children of this class

            hintCompletionRequest.addMessage(chatMessage);
            try {
              // Try catch for accessing ChatGPT
              ChatCompletionResult hintCompletionResult = hintCompletionRequest.execute();
              Choice result = hintCompletionResult.getChoices().iterator().next();
              hintCompletionRequest.addMessage(result.getChatMessage());
              if (GameState.hintsRemaining == 0) {
                addToLog(new ChatMessage("assistant", "No hints available!"), false, true, false);
                if (GameState.isTextToSpeechOn) {
                  App.getTextToSpeech().speak("No hints available!");
                }
              } else {
                if (result.getChatMessage().getContent().startsWith("HINT:")) {
                  addToLog(result.getChatMessage(), false, true, false);
                  if (GameState.getCurrentDifficulty() == Difficulty.MEDIUM) {
                    GameState.hintsRemaining--;
                    Platform.runLater(
                        () -> {
                          setHintCounter();
                        });
                    if (GameState.isTextToSpeechOn) {
                      App.getTextToSpeech().speak(result.getChatMessage().getContent());
                    }
                  }
                }
              }
              Platform.runLater(() -> SceneManager.updateChat());
            } catch (ApiProxyException e) {
              e.printStackTrace();
              // Exception handling

              GameState.isGameOffline = true;
            }
            Platform.runLater(
                () -> {
                  hintButton.setDisable(false);
                });
            return null;
          }
        };
    // The GPT thread runnable is a Task so that it can be bound to a GUI element later on
    Thread runGptHintThread = new Thread(runGptHintTask);
    runGptHintThread.start();
  }

  /**
   * typeText prints text (message) to a textarea. The printing effect is similar to a typewriter.
   * The speed of this effect can be altered by manipulating the durations in the appropriate lines.
   * The first "Duration.millis" specifies the delay for the text to appear and the second specifies
   * the delay for each character printed.
   *
   * @param textArea the text area to print the text to.
   * @param message the text to print.
   */
  public void typeText(String message) {
    Timeline typewriterTimeline = new Timeline();
    Duration duration = Duration.millis(15);
    final StringBuilder text = new StringBuilder();

    for (char character : message.toCharArray()) {
      KeyFrame keyFrame =
          new KeyFrame(
              duration,
              event -> {
                text.append(character);
                chatTextArea.setText(text.toString());
              });

      typewriterTimeline.getKeyFrames().add(keyFrame);
      duration = duration.add(Duration.millis(35));
    }
    typewriterTimeline.play();
  }

  /**
   * Method that provides GPT with its backstory for the game. A different story is provided
   * depending on the difficulty.
   */
  public void giveBackstory() {
    addToLog(
        new ChatMessage("assistant", GptPromptEngineering.initializeBackstory()),
        false,
        false,
        true);
    GameState.Difficulty difficulty = GameState.getCurrentDifficulty();
    switch (difficulty) {
      case EASY:
        addToLog(
            new ChatMessage("assistant", GptPromptEngineering.setEasyHintDifficulty()),
            false,
            false,
            true);
        break;
      case MEDIUM:
        addToLog(
            new ChatMessage("assistant", GptPromptEngineering.setMediumHintDifficulty()),
            false,
            false,
            true);
        break;
      case HARD:
        addToLog(
            new ChatMessage("assistant", GptPromptEngineering.setHardHintDifficulty()),
            false,
            false,
            true);
        break;
    }
  }

  /**
   * onSendMessage can be triggered by clicking the send button or pressing enter in the text area.
   * It takes the input from the text area and sends it to GPT. It then appends the response from
   * GPT to the chat text area. If the input string is "HINT", it will trigger the hint function.
   *
   * @param event the action event.
   * @throws ApiProxyException
   * @throws IOException
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {

    String message = inputText.getText().trim();
    inputText.clear();
    if (message == null || message.trim().isEmpty()) {
      // If message is empty, don't do anything.
      return;
    }
    // The following code clears the entry box, writes the most recent user entry, and
    // then runs chat GPT for the user's entry
    ChatMessage msg = new ChatMessage("user", message);
    if (message.equalsIgnoreCase("HINT")) {
      runGptForHint(new ChatMessage("user", GptPromptEngineering.getHint()));
    } else {
      runGpt(msg, false);
    }
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  protected void runGpt(ChatMessage msg, boolean isRiddle) throws ApiProxyException {

    Platform.runLater(() -> turnOffLights());
    addToLog(msg, false, false, true);
    if (isRiddle) {
      riddleIndex = orderedGptInteractionLog.size();
    }

    Task<Void> runGptTask = createRunGptTask(msg);
    Thread runGptThread = new Thread(runGptTask);
    runGptThread.start();
  }

  /**
   * Creates a task that runs the GPT model.
   *
   * @return the task.
   */
  private Task<Void> createRunGptTask(ChatMessage msg) {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        processGptResponse(msg);
        return null;
      }
    };
  }

  /** Processes the response from the GPT model. */
  private void processGptResponse(ChatMessage msg) {
    try {
      ChatMessage gptResponse = getGptResponse();
      handleGptResponse(msg, gptResponse);
    } catch (ApiProxyException e) {
      handleGptError(e);
    } finally {
      Platform.runLater(() -> turnOnLights());
    }
  }

  /**
   * Gets the response from the GPT model.
   *
   * @return the response.
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage getGptResponse() throws ApiProxyException {
    chatCompletionRequest.setMessages(
        orderedGptInteractionLog.get(orderedGptInteractionLog.size() - 1));
    ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
    Choice result = chatCompletionResult.getChoices().iterator().next();
    return result.getChatMessage();
  }

  /**
   * Handles the response from the GPT model.
   *
   * @param gptResponse the response for GPT.
   */
  private void handleGptResponse(ChatMessage userMessage, ChatMessage gptMessage) {
    // check if the GPT response should be a riddle
    if (GameState.isRiddleExpected) {
      addToLog(gptMessage, false, false, false);
      GameState.isRiddleExpected = false;
      updateChat();
    } else {
      // If a hint was asked for and if the response is a hint
      if (("HINT".equals(userMessage.getContent()))
          && (gptMessage.getContent().startsWith("HINT:"))) {
        addToLog(gptMessage, false, false, false);
        if (GameState.isTextToSpeechOn) {
          App.getTextToSpeech().speak(gptMessage.getContent());
        }
        GameState.hintsRemaining--;
        Platform.runLater(
            () -> {
              setHintCounter();
              updateChat();
            });
        // If the response is a hint, but a hint was not asked for
      } else if ((!"HINT".equals(userMessage.getContent()))
          && (gptMessage.getContent().startsWith("HINT:"))) {
        handleHintResponse(gptMessage);
      } else {
        addToLog(gptMessage, false, false, false);
        Platform.runLater(() -> updateChat());
        if (GameState.isTextToSpeechOn) {
          App.getTextToSpeech().speak(gptMessage.getContent());
        }
      }
    }
  }

  /**
   * Handles when a hint is attempted to be provided by GPT through illegal means.
   *
   * @param gptResponse the response from GPT.
   */
  private void handleHintResponse(ChatMessage gptResponse) {
    String response = GptPromptEngineering.getIllegalHintResponse();
    addToLog(gptResponse, false, false, true);
    Platform.runLater(() -> updateChat());
    if (GameState.isTextToSpeechOn) {
      App.getTextToSpeech().speak(response);
    }
  }

  /**
   * Handles errors from the GPT model.
   *
   * @param e the exception.
   */
  private void handleGptError(ApiProxyException e) {
    System.out.println("Error in running GPT API!");
    e.printStackTrace();
  }

  /** Turns off the lights in all rooms. */
  public void addToLog(ChatMessage msg, boolean enviroClick, boolean isHint, boolean isWilsons) {

    if (isWilsons) {
      if (isHint) {
        hintLog.add(msg);
      } else {
        gptChatLog.add(msg);
      }
    }

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

  /** Turns the lights off in all the room. */
  private void turnOffLights() {
    GameState.isGPTRunning = true;
    App.getRoom1().lightsOff();
    App.getRoom2().lightsOff();
    App.getRoom3().lightsOff();
  }

  /** Turns the lights on in all the room. */
  private void turnOnLights() {
    GameState.isGPTRunning = false;
    App.getRoom1().lightsOn();
    App.getRoom2().lightsOn();
    App.getRoom3().lightsOn();
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
    return gptChatLog;
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

    String addedMessage = "";

    if (riddleIndex == logIndex) {
      ChatMessage msge = orderedGptInteractionLog.get(logIndex - 1).get(1);
      typeText("\n" + modifiedNaming.get(msge.getRole()) + " -> " + msge.getContent());
      return;
    }

    for (ChatMessage msg : orderedGptInteractionLog.get(logIndex - 1)) {

      addedMessage =
          addedMessage + "\n" + modifiedNaming.get(msg.getRole()) + " -> " + msg.getContent();
    }

    typeText(addedMessage);
  }

  /**
   * Moves the chat history display forward.
   *
   * @param event the action event triggered by the forward button
   */
  @FXML
  private void onForwardHistory(ActionEvent event) {
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
  private void onBackwardHistory(ActionEvent event) {
    if (logIndex <= 3) {
      return;
    }
    logIndex--;
    updateChat();
  }
}
