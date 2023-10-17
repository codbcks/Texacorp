package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** This class is the controller for Room 1 in the Escaipe game. */
public class Room1Controller {

  private static String wordToGuess;
  private static String wordList;

  @FXML private Rectangle triggerConsole;
  @FXML private Rectangle lightOverlay;
  @FXML private Rectangle printerPromptTrigger;

  @FXML private Label terminalInstructionsLabel;
  @FXML private Label terminalLabel;
  @FXML private Pane terminalPane;
  @FXML private Pane terminalWrapperPane;
  @FXML private PasswordField riddleAnswerEntry;

  @FXML private ImageView imgMachineMoveX;
  @FXML private ImageView imgConveyorSaw;
  @FXML private Pane paneMachineMoveY;
  @FXML private Pane paneConveyorDropBox;
  @FXML private Rectangle triggerDropSaw;
  @FXML private ImageView imgConveyor;
  @FXML private ImageView imgMachineResin;

  private Timeline lightsOff;
  private Timeline lightsOn;
  private Timeline takeBrokenSaw;
  private Timeline giveFixedSaw;
  private long conveyorFrameRate = 12;
  private long repairBayFrameRate = 20;

  private boolean conveyorIsActive = false;
  private boolean sawDeposited = false;
  private boolean materialDeposited = false;
  private boolean repairComplete = false;

  /**
   * The code initializes the Room1Controller by setting up CSS style classes, mouse interactions,
   * and click events. It also triggers animations for lights off and on, and conveyor motion when
   * the saw is dropped or fixed.
   *
   * @throws ApiProxyException if there is an issue with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {

    // initialise css style classes
    riddleAnswerEntry.getStyleClass().add("riddle-answer-entry");
    terminalInstructionsLabel.getStyleClass().add("terminal-label");
    terminalLabel.getStyleClass().add("terminal-label");

    wordToGuess = getRandomWord();

    mouseInteract(triggerConsole);
    mouseInteract(printerPromptTrigger);

    // Clicking outside the terminal will hide it
    terminalWrapperPane.setOnMouseClicked(
        event -> {
          double x = event.getX();
          double y = event.getY();

          if (x < terminalPane.getLayoutX()
              || x > terminalPane.getLayoutX() + terminalPane.getWidth()
              || y < terminalPane.getLayoutY()
              || y > terminalPane.getLayoutY() + terminalPane.getHeight()) {
            hideTerminal();
          }
        });

    // triggers the lights off animation
    lightsOff =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.3)));

    // triggers the lights on animation
    lightsOn =
        new Timeline(
            new KeyFrame(Duration.seconds(0.0), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.2), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(0.4), e -> lightOverlay.setOpacity(0.0)),
            new KeyFrame(Duration.seconds(0.6), e -> lightOverlay.setOpacity(0.3)),
            new KeyFrame(Duration.seconds(1.2), e -> lightOverlay.setOpacity(0.0)));

    // triggers conveyor motion when saw is dropped
    takeBrokenSaw =
        new Timeline(
            App.getTranslateKeyFrame(0, -52, paneConveyorDropBox, 52 * conveyorFrameRate, 0),
            App.getTranslateKeyFrame(
                -228, 0, paneConveyorDropBox, 228 * conveyorFrameRate, 52 * conveyorFrameRate),
            App.getTranslateKeyFrame(
                0, 116, paneConveyorDropBox, 116 * conveyorFrameRate, 280 * conveyorFrameRate),
            new KeyFrame(
                Duration.millis(396 * conveyorFrameRate),
                e -> {
                  sawDeposited = true;
                  conveyorIsActive = false;
                  checkForMachineStart();
                }));

    // triggers conveyor motion when saw is dropped
    giveFixedSaw =
        new Timeline(
            App.getTranslateKeyFrame(0, -116, paneConveyorDropBox, 116 * conveyorFrameRate, 0),
            App.getTranslateKeyFrame(
                228, 0, paneConveyorDropBox, 228 * conveyorFrameRate, 116 * conveyorFrameRate),
            App.getTranslateKeyFrame(
                0, 52, paneConveyorDropBox, 52 * conveyorFrameRate, 344 * conveyorFrameRate),
            new KeyFrame(
                Duration.millis(396 * conveyorFrameRate),
                e -> {
                  conveyorIsActive = false;
                  repairComplete = true;
                  triggerDropSaw.setCursor(Cursor.HAND);
                }));
  }

  /** Turns the lights off in the room. */
  public void lightsOff() {
    lightsOff.playFromStart();
  }

  /** Turns the lights on in the room. */
  public void lightsOn() {
    lightsOn.playFromStart();
  }

  /**
   * Handles the event when the saw is dropped in Room 1. If the repair is complete, the saw is
   * removed and the fixed saw is added to the top bar. If the repair is not complete and the player
   * has a broken saw, the broken saw is removed and the conveyor belt is activated.
   *
   * @param event The mouse event that triggered the method.
   * @throws IOException If an I/O error occurs.
   */
  @FXML
  private void dropSaw(MouseEvent event) throws IOException {
    if (repairComplete) {
      imgConveyorSaw.setVisible(false);
      App.getTopBarController().giveItem(TopBarController.Item.SAW_FIXED);
      triggerDropSaw.setCursor(Cursor.DEFAULT);
    } else if (App.getTopBarController().hasItem(TopBarController.Item.SAW_BROKEN)) {
      App.getTopBarController().removeItem(TopBarController.Item.SAW_BROKEN);
      imgConveyorSaw.setVisible(true);
      takeBrokenSaw.play();
      activateConveyor(false);
      triggerDropSaw.setCursor(Cursor.DEFAULT);
    }
  }

  /**
   * Drops resin into the machine when the user clicks on the resin image. If the user has resin in
   * their inventory, the resin is removed from the inventory, the resin image is made visible, and
   * the materialDeposited flag is set to true. Finally, the checkForMachineStart() method is called
   * to check if the machine can be started.
   *
   * @param event The mouse event that triggered the method call.
   * @throws IOException If an I/O error occurs.
   */
  @FXML
  private void dropResin(MouseEvent event) throws IOException {
    if (App.getTopBarController().hasItem(TopBarController.Item.RESIN)) {
      App.getTopBarController().removeItem(TopBarController.Item.RESIN);
      imgMachineResin.setVisible(true);
      materialDeposited = true;
      checkForMachineStart();
    }
  }

  /**
   * Activates the conveyor belt animation in Room 1.
   *
   * @param backward if true, the conveyor belt moves backward; if false, it moves forward
   */
  private void activateConveyor(boolean backward) {
    conveyorIsActive = true;

    Task<Void> conveyorMovementTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {

            int conveyorFrame = 1;
            conveyorIsActive = true;

            while (conveyorIsActive) {
              if (conveyorFrame == 5) {
                conveyorFrame = 1;
              }

              if (conveyorFrame == 0) {
                conveyorFrame = 4;
              }

              imgConveyor.setImage(
                  new Image("/images/leftRoomBelt-Frame" + conveyorFrame + ".png"));
              try {
                Thread.sleep(conveyorFrameRate);
              } catch (Exception e) {
                System.err.println("ERROR: Exception in Room1Controller.dropSaw!");
              }

              if (backward) {
                conveyorFrame--;
              } else {
                conveyorFrame++;
              }
            }
            return null;
          }
        };

    Thread conveyorMovementThread = new Thread(conveyorMovementTask);
    conveyorMovementThread.start();
  }

  /**
   * Checks if the saw and material have been deposited and the password has been obtained. If all
   * conditions are met, a Timeline animation is created for the repair bay. The animation moves the
   * machine and saw, changes the saw image to a fixed saw, activates the conveyor, and plays a
   * sound effect.
   */
  private void checkForMachineStart() {
    if (sawDeposited && materialDeposited && GameState.isPasswordObtained) {
      Timeline activateRepairBay =
          new Timeline(
              // Timeline for the repair bay
              App.getTranslateKeyFrame(0, 64, paneMachineMoveY, 110 * repairBayFrameRate, 0),
              App.getTranslateKeyFrame(50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 0),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 10 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 20 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 30 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 40 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 50 * repairBayFrameRate),
              new KeyFrame(
                  // Timeline for the saw
                  Duration.millis(50 * repairBayFrameRate),
                  e -> {
                    // change the saw image to the fixed saw
                    imgConveyorSaw.setImage(new Image("/images/SAW_FIXED.png"));
                  }),
              App.getTranslateKeyFrame(
                  50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 60 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 70 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 80 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 90 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  50, 0, imgMachineMoveX, 10 * repairBayFrameRate, 100 * repairBayFrameRate),
              new KeyFrame(
                  Duration.millis(110 * repairBayFrameRate),
                  e -> {
                    activateConveyor(true);
                    giveFixedSaw.play();
                  }),
              App.getTranslateKeyFrame(
                  -50, 0, imgMachineMoveX, 40 * repairBayFrameRate, 130 * repairBayFrameRate),
              App.getTranslateKeyFrame(
                  0, -64, paneMachineMoveY, 40 * repairBayFrameRate, 130 * repairBayFrameRate));

      activateRepairBay.play(); // start the animation
    }
  }

  /**
   * Helper method for changing cursor appearance for interactable objects.
   *
   * @param node The interactable object.
   */
  @FXML
  private void mouseInteract(Node node) {
    node.setOnMouseEntered(
        e -> {
          node.setCursor(Cursor.HAND);
        });

    node.setOnMouseExited(
        e -> {
          node.setCursor(Cursor.DEFAULT);
        });
  }

  /**
   * Generates a random word from a list of words to be used in the riddle.
   *
   * @return a random word from the list.
   */
  public String getRandomWord() {
    wordList = "star,laser,satellite,cat,potato,computer,mouse,pyramid,phone,camera";
    // Splitting the list into an array of individual entries
    return wordList.split(",")[(int) (Math.random() * 10)];
  }

  /**
   * Getter method for the word to guess for the riddle for this round.
   *
   * @return the word to guess.
   */
  public String getWordToGuess() {
    return wordToGuess;
  }

  public void setWordToGuess(String wordToGuess) {
    this.wordToGuess = wordToGuess;
  }

  /**
   * Generates a riddle using the GptPromptEngineering class with the given word to guess. Adds the
   * generated riddle to the log and updates the chat.
   *
   * @param event The mouse event that triggered the method.
   * @throws ApiProxyException If an error occurs while generating the riddle.
   */
  @FXML
  public void generateRiddle(MouseEvent event) throws ApiProxyException {
    String riddle = GptPromptEngineering.getRiddleWithGivenWord(wordToGuess);

    SceneManager.addToLogEnviroMessage(new ChatMessage("assistant", riddle));
    SceneManager.updateChat();
  }

  /**
   * Clicking the console will trigger the GPT to generate a riddle.
   *
   * @param event the mouse event
   * @throws IOException if there is an I/O error
   */
  @FXML
  public void clickTriggerConsole(MouseEvent event) throws IOException {
    if (GameState.isFirstTime) {
      GameState.isFirstTime = false;
      GameState.isRiddleExpected = true;
      Platform.runLater(
          () -> {
            showTerminal();
          });
      try {
        GameState.isRiddleActive = true;
        App.bottomBarController.runGpt(
            // runGpt is a method in the parent class, it returns the GPT response for the input.
            new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord(wordToGuess)));
      } catch (ApiProxyException e) {
        e.printStackTrace();
      }
    } else if (!GameState.isFirstTime && !GameState.isRoom1Solved) {
      Platform.runLater(
          () -> {
            showTerminal();
          });
    } else {
      return;
    }
  }

  /** Hides the terminal for password entry. */
  @FXML
  private void hideTerminal() {
    terminalWrapperPane.setVisible(false);
    terminalPane.setVisible(false);
    TranslateTransition translateTransition =
        new TranslateTransition(Duration.millis(1000), terminalPane);
    translateTransition.setByY(65);
    translateTransition.play();
    terminalPane.setTranslateY(0);
  }

  /** Shows the terminal for password entry. */
  @FXML
  private void showTerminal() {
    terminalWrapperPane.setVisible(true);
    terminalPane.setVisible(true);

    if (GameState.isTextToSpeechOn) {
      App.textToSpeech.speak("The... password... is...");
    }

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.millis(1000), terminalPane);
    translateTransition.setByY(-65);
    translateTransition.play();
    riddleAnswerEntry.requestFocus();
  }

  /**
   * Submits the password guess and tells player if guess was successful or not.
   *
   * @param event the action event
   */
  @FXML
  private void onSubmitGuess(ActionEvent event) {
    String guess = riddleAnswerEntry.getText();
    if (guess.equalsIgnoreCase(wordToGuess)) {

      Platform.runLater(
          () -> {
            SceneManager.appendChatMessage("Success!", "assistant");
            hideTerminal();
          });
      GameState.isRiddleActive = false;
      GameState.isRoom1Solved = true;
      App.topBarController.giveItem(TopBarController.Item.SAW_BLADE);
    } else {
      Platform.runLater(
          () -> {
            SceneManager.appendChatMessage("Declined!", "assistant");
            riddleAnswerEntry.clear();
          });
    }
  }

  /* ------- NOTE: This is how we will be animating items into the inventory --------

    TranslateTransition translate = new TranslateTransition();
    translate.setByX((INVX + (INVGAPX * invIndex)) - imageToMove.getLayoutX());
    translate.setByY(INVY - imageToMove.getLayoutY());
    translate.setDuration(Duration.millis(600));
    translate.setNode(imageToMove);
    translate.play();

  */

  /**
   * Clicking this will prompt the player to pick up the 3D printer.
   *
   * @param event the mouse event
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void printerPrompt(MouseEvent event) throws ApiProxyException {
    SceneManager.appendChatMessage(
        "Two 3D printers loaded with high-tensile steel. Perfect for producing a durable saw"
            + " blade.",
        "user");
  }
}
