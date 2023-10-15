package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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

public class Room1Controller {
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

  private static String wordToGuess;
  private static String wordList;

  private boolean conveyorIsActive = false;
  private boolean sawDeposited = false;
  private boolean materialDeposited = false;
  private boolean repairComplete = false;

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

  @FXML
  private void dropSaw(MouseEvent event) throws IOException {
    if (repairComplete) {
      imgConveyorSaw.setVisible(false);
      App.topBarController.giveItem(TopBarController.Item.SAW_FIXED);
      triggerDropSaw.setCursor(Cursor.DEFAULT);
    } else if (App.topBarController.hasItem(TopBarController.Item.SAW_BROKEN)) {
      App.topBarController.removeItem(TopBarController.Item.SAW_BROKEN);
      imgConveyorSaw.setVisible(true);
      takeBrokenSaw.play();
      activateConveyor(false);
      triggerDropSaw.setCursor(Cursor.DEFAULT);
    }
  }

  @FXML
  private void dropResin(MouseEvent event) throws IOException {
    if (App.topBarController.hasItem(TopBarController.Item.RESIN)) {
      App.topBarController.removeItem(TopBarController.Item.RESIN);
      imgMachineResin.setVisible(true);
      materialDeposited = true;
      checkForMachineStart();
    }
  }

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

  private void checkForMachineStart() {
    if (sawDeposited && materialDeposited && GameState.isPasswordObtained) {
      Timeline activateRepairBay =
          new Timeline(
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
                  Duration.millis(50 * repairBayFrameRate),
                  e -> {
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

      activateRepairBay.play();
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
   * @return a random word from the list
   */
  private String getRandomWord() {
    wordList = "star,laser,satellite,cat,potato,computer,mouse,pyramid,phone,camera";
    // Splitting the list into an array of individual entries
    return wordList.split(",")[(int) (Math.random() * 5)];
  }

  // Generate a riddle using GPT and set the word to guess
  @FXML
  public void generateRiddle(MouseEvent event) throws ApiProxyException {
    String riddle = GptPromptEngineering.getRiddleWithGivenWord(wordToGuess);

    SceneManager.addToLogEnviroClick(new ChatMessage("assistant", riddle));
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
      try {
        App.bottomBarController.runGpt(
            // runGpt is a method in the parent class, it returns the GPT response for the input.
            new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord(wordToGuess)),
            false);
      } catch (ApiProxyException e) {
        e.printStackTrace();
      }
      GameState.isFirstTime = false;
    } else if (!GameState.isFirstTime && !GameState.isPasswordObtained) {
      showTerminal();
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
    translateTransition.setByY(120);
    terminalPane.setTranslateY(0);
  }

  /** Shows the terminal for password entry. */
  @FXML
  private void showTerminal() {
    terminalWrapperPane.setVisible(true);
    terminalPane.setVisible(true);
    App.textToSpeech.speak("The... password... is...");
    TranslateTransition translateTransition =
        new TranslateTransition(Duration.millis(1000), terminalPane);
    translateTransition.setByY(-120);
    translateTransition.play();
    riddleAnswerEntry.requestFocus();
  }

  /**
   * Submits the password guess and tells player if guess was successful or not.
   *
   * @param event the action event
   */
  @FXML
  private void submitGuess(ActionEvent event) {
    String guess = riddleAnswerEntry.getText();
    if (guess.equalsIgnoreCase(wordToGuess)) {

      SceneManager.addToLogEnviroClick(new ChatMessage("user", "Success!"));
      SceneManager.updateChat();

      hideTerminal();
      GameState.isPasswordObtained = true;
      checkForMachineStart();
    } else {

      SceneManager.addToLogEnviroClick(new ChatMessage("assistant", "Declined!"));
      SceneManager.updateChat();

      riddleAnswerEntry.clear();
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
    SceneManager.addToLogEnviroClick(
        new ChatMessage(
            "user",
            "Two 3D printers loaded with high-tensile steel. Pefect for producing a durable saw"
                + " blade."));
    SceneManager.updateChat();
  }
}
