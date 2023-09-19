package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.concurrent.Task;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public abstract class GptInteraction {

  /* The GPT Interaction class contains code that was previously repeated in chat and room.
  This allows any class which extends it to interface with ChatGPT*/

  protected ChatCompletionRequest chatCompletionRequest;
  protected static HashMap<String, String> modifiedNaming;
  protected List<ChatMessage> gptInteractionLog = new ArrayList<>();

  // This HashMap replaces names with contextual equivalents

  public GptInteraction() {
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.5).setTopP(0.7).setMaxTokens(150);
    modifiedNaming = new HashMap<String, String>();
    // Entering the new names into the HashMap
    modifiedNaming.put("assistant", "AI");
    modifiedNaming.put("user", "YOU");
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
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  protected void runGpt(ChatMessage msg, boolean sayAloud) throws ApiProxyException {

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

  protected abstract void appendChatMessage(String chatMessage, String role);

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
}
