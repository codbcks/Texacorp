package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GptInteraction;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class Room1Controller extends GptInteraction {
  @FXML private ImageView backdrop; // Room graphic
  @FXML private ImageView evidence; // Evidence graphic
  @FXML private ImageView itemLockPick; // Lock-Pick icon
  @FXML private ImageView itemBoltCutters; // Bolt-Cutters icon
  @FXML private ImageView obstacleLaser; // Laser grid overlay
  @FXML private Rectangle triggerPodium; // Desk in room centre trigger
  @FXML private Rectangle triggerCupboard; // Storage cupboard trigger
  @FXML private Rectangle triggerTimer; // Timer trigger
  @FXML private Rectangle triggerConsole; // Console trigger
  @FXML private Rectangle triggerDoor; // Door trigger
  @FXML private Rectangle triggerDraws; // Copper coil trigger
  @FXML private Rectangle moveRoom2;
  @FXML private Label labelCharacterName; // Character name label
  @FXML private Label labelTimer; // Timer label for room
  @FXML private Label labelDialogueBox; // Dialogue label

  private final int INVX = 8; // Inventory x pos
  private final int INVY = 8; // Inventory y pos
  private final int INVGAPX = 72; // Gap between cells
  private int invIndex = 0; // Inventory current next slot

  /** Initializes labelTimer as the scene's dedicated timer GUI representation. */
  @FXML
  public void initialize() throws ApiProxyException {
    // Set challenge timer
    ChallengeTimer.setCurrentLabelTimer(labelTimer);
    // Set label timer
    GameState.roomTimerLabel = labelTimer;
    appendChatMessage("Once I take that folder, I'll only have 2 minutes to escape...", "user");
  }

  // Trigger podium activates when the user clicks on the file in the middle of the room
  @FXML
  public void clickTriggerPodium(MouseEvent event) {

    if (invIndex > 0) {
      // This code runs when the player tries to interact with the table a second time
      appendChatMessage(
          "I've started the countdown, it's too late to put this evidence back now.", "user");
      triggerPodium.setVisible(false);
    } else {
      // This code runs when the playre tries to interact with the code at first
      GameState.isLaserActive = true;

      // Multithreading is used to execute the check-password logic
      Thread laserCheckThread =
          new Thread(
              () -> {
                while (GameState.isLaserActive) {
                  try {
                    Thread.sleep(32);
                  } catch (InterruptedException e) {
                    System.out.println("ERROR: Exception in Room1Controller.clickTriggerPodium!");
                    e.printStackTrace();
                  }
                }

                // This line runs once the laser has been deactivated. It makes the laser invisible,
                // and prints to the console.
                Platform.runLater(
                    () -> {
                      obstacleLaser.setVisible(false);
                      appendChatMessage(
                          "Password recieved! Laser array powering down...", "assistant");
                    });
              });

      // Starting the previously mentioned laser checking thread
      laserCheckThread.start();

      obstacleLaser.setVisible(true);
      moveToInventory(evidence);

      // Prompting ChatGPT to tell the player they have 2 minutes to live
      gptPromptHelper(
          "Say a warning message indicating that the building will self destruct in 2 minutes with"
              + " under 30 words");
    }
  }

  // This function moves the player over to the chat GUI
  @FXML
  public void clickTriggerConsole(MouseEvent event) throws IOException {
    if (GameState.isLaserActive) {
      // Only switch when the laser is already active
      ChallengeTimer.setCurrentLabelTimer(GameState.chatTimerLabel);
      App.setRoot(SceneManager.AppUI.CHAT);
    }
  }

  @FXML
  public void clickMoveRoom2(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.ROOM2);
  }

  // This function triggers when the player tries to access the door
  @FXML
  public void clickTriggerDoor(MouseEvent event) throws IOException {
    // If the laser is active, they cannot leave, and the AI should tell them so.
    if (GameState.isLaserActive) {
      gptPromptHelper(
          "Inform the user that the door lasers will deactivate once the password is recieved by"
              + " the room console");
      // If they do not have the bolt cutters, but the laser is not active, prompt the user to find
      // some.
    } else if ((!GameState.isLaserActive) && (invIndex < 3) && (invIndex > 0)) {
      appendChatMessage(
          "The lasers' heat has welded the door shut! That cannot be OSHA approved.", "user");
      // If they have bolt cutters and the laser is not active, cancel the timer and send them to
      // win screen.
    } else if ((!GameState.isLaserActive) && (invIndex == 3)) {
      ChallengeTimer.cancelTimer();
      App.setRoot(SceneManager.AppUI.WIN);
    }
  }

  // The following method allows the player to access the bolt cutter item
  @FXML
  public void clickTriggerCupboard(MouseEvent event) {
    // When they don't have the lock pick, they cannot open the cupboard.
    if (invIndex == 1) {
      appendChatMessage(
          "This cupboard is locked, but maybe I can find something to pick the lock with...",
          "user");
      // When they have the lock pick, but the lasers are active..
    } else if ((invIndex == 2) & (GameState.isLaserActive)) {
      appendChatMessage(
          "I can use these bolt-cutters to cut open the door, as soon as I've dealt with the"
              + " lasers...",
          "user");
      moveToInventory(itemBoltCutters);
      // When they have the lock pick, and the laser isn't active...
    } else if ((invIndex == 2) & (!GameState.isLaserActive)) {
      appendChatMessage(
          "I'm running out of time, I have to get these bolt-cutters back to the door!", "user");
      moveToInventory(itemBoltCutters);
      // If they already have the bolt cutters
    } else if (invIndex > 2) {
      appendChatMessage("One mop... One bucket... I don't need any of this stuff.", "user");
      triggerCupboard.setVisible(false);
    }
  }

  @FXML
  public void clickTriggerTimer(MouseEvent event) {
    // This function triggers when the player tries to interact with the GUI timer
    if (invIndex > 0) {
      appendChatMessage(
          "FACT: Hitting the countdown timer will not stop the self-destruct sequence.",
          "assistant");
    }
  }

  @FXML
  public void clickTriggerDraws(MouseEvent event) {
    // This function triggers when the player tries to take the copper
    if (invIndex == 1) {
      // If they don't have the lock pick
      moveToInventory(itemLockPick);
      appendChatMessage("Maybe if I bend some of this wire... YES! I've made a lockpick!", "user");
    } else if (invIndex > 1) {
      // If they have the lock pick
      appendChatMessage(
          "Maybe I could use the sharper pieces to tunnel my way out... Hehe, no.", "user");
      triggerDraws.setVisible(false);
    }
  }

  protected void appendChatMessage(String chatMessage, String role) {
    // This function appends the chat messagest to the output label
    Platform.runLater(
        () -> {
          labelDialogueBox.setText(chatMessage);
          labelCharacterName.setText(modifiedNaming.get(role) + ": ");
        });
  }

  // This function takes an image in the GUI and moves it into the next
  // inventory slot.
  private void moveToInventory(ImageView imageToMove) {
    imageToMove.setVisible(true);

    // Specifying the GUI animation
    TranslateTransition translate = new TranslateTransition();
    translate.setByX((INVX + (INVGAPX * invIndex)) - imageToMove.getLayoutX());
    translate.setByY(INVY - imageToMove.getLayoutY());
    translate.setDuration(Duration.millis(600));
    translate.setNode(imageToMove);
    translate.play();

    // incrementing inventory index
    invIndex++;
  }

  // A helper function to avoid repeating code when prompting gpt
  private void gptPromptHelper(String prompt) {
    try {
      runGpt(new ChatMessage("user", prompt), true);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }
}
