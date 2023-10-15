package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.controllers.BottomBarController;
import nz.ac.auckland.se206.controllers.Room1Controller;
import nz.ac.auckland.se206.controllers.Room2Controller;
import nz.ac.auckland.se206.controllers.Room3Controller;
import nz.ac.auckland.se206.controllers.TopBarController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The App class extends the Application class and serves as the main entry point for the
 * application. It contains methods for initializing the application, resetting the game, and
 * getting various controllers and objects. The class also defines constants and provides utility
 * methods for translating nodes and getting the current scene.
 */
public class App extends Application {

  public static final int ROOM_WIDTH = 960;

  private static Scene scene;
  private static TextToSpeech textToSpeech;
  private static Room1Controller room1;
  private static Room2Controller room2;
  private static Room3Controller room3;
  private static TopBarController topBarController;
  private static BottomBarController bottomBarController;

  /**
   * The main method initializes the TextToSpeech object and launches the application.
   *
   * @param args The command line arguments.
   */
  public static void main(final String[] args) {
    textToSpeech = new TextToSpeech();
    launch();
  }

  /**
   * This method sets the root scene to a new UI element.
   *
   * @param newUI The new UI element to set the root scene to.
   * @throws IOException If the FXML file for the new UI element is not found.
   */
  public static void setRoot(SceneManager.AppInterface newUI) throws IOException {
    scene.setRoot(SceneManager.getInterface(newUI));
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {

    SceneManager.createAppInterface(SceneManager.AppInterface.INTRO, "intro");
    SceneManager.createAppInterface(SceneManager.AppInterface.WIN, "win");
    SceneManager.createAppInterface(SceneManager.AppInterface.LOSE, "lose");
    resetGame();

    Parent root = SceneManager.getInterface(SceneManager.AppInterface.INTRO);

    scene = new Scene(root, 960, 640);
    stage.setResizable(false);

    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }

  /**
   * This method resets the game by creating new UI elements and getting their controllers.
   *
   * @throws IOException If any of the FXML files for the UI elements are not found.
   */
  public static void resetGame() throws IOException {

    SceneManager.createAppInterface(SceneManager.AppInterface.ROOM1, "room1");
    SceneManager.createAppInterface(SceneManager.AppInterface.ROOM2, "room2");
    SceneManager.createAppInterface(SceneManager.AppInterface.ROOM3, "room3");
    SceneManager.createAppInterface(SceneManager.AppInterface.BOTTOMBAR, "bottomBar");
    SceneManager.createAppInterface(SceneManager.AppInterface.TOPBAR, "topBar");

    room1 = ((Room1Controller) SceneManager.getController(SceneManager.AppInterface.ROOM1));
    room2 = ((Room2Controller) SceneManager.getController(SceneManager.AppInterface.ROOM2));
    room3 = ((Room3Controller) SceneManager.getController(SceneManager.AppInterface.ROOM3));
    topBarController =
        ((TopBarController) SceneManager.getController(SceneManager.AppInterface.TOPBAR));
    bottomBarController =
        ((BottomBarController) SceneManager.getController(SceneManager.AppInterface.BOTTOMBAR));

    SceneManager.createAppInterface(SceneManager.AppInterface.IN_GAME, "inGame");
  }

  /**
   * This method returns the current scene.
   *
   * @return The current scene.
   */
  public static Scene getCurrentScene() {
    return scene;
  }

  /**
   * This method returns a KeyFrame object for translating a node.
   *
   * @param xDist The distance to translate the node in the x direction.
   * @param yDist The distance to translate the node in the y direction.
   * @param nodeToMove The node to translate.
   * @param durationMillis The duration of the translation animation in milliseconds.
   * @param startDelayMillis The delay before the translation animation starts in milliseconds.
   * @return A KeyFrame object for translating a node.
   */
  public static KeyFrame getTranslateKeyFrame(
      double xDist, double yDist, Node nodeToMove, double durationMillis, double startDelayMillis) {
    return new KeyFrame( // KeyFrame for translating a node
        Duration.millis(startDelayMillis),
        e -> {
          TranslateTransition tempTransition =
              new TranslateTransition(Duration.millis(durationMillis), nodeToMove);
          tempTransition.setByX(xDist); // set the distance to move in the x direction
          tempTransition.setByY(yDist); // set the distance to move in the y direction
          tempTransition.play();
        });
  }

  /**
   * This method returns the TextToSpeech object.
   *
   * @return The TextToSpeech object.
   */
  public static TextToSpeech getTextToSpeech() {
    return textToSpeech;
  }

  /**
   * This method returns the Room1Controller object.
   *
   * @return The Room1Controller object.
   */
  public static Room1Controller getRoom1() {
    return room1;
  }

  /**
   * This method returns the Room2Controller object.
   *
   * @return The Room2Controller object.
   */
  public static Room2Controller getRoom2() {
    return room2;
  }

  /**
   * This method returns the Room3Controller object.
   *
   * @return The Room3Controller object.
   */
  public static Room3Controller getRoom3() {
    return room3;
  }

  /**
   * This method returns the TopBarController object.
   *
   * @return The TopBarController object.
   */
  public static TopBarController getTopBarController() {
    return topBarController;
  }

  /**
   * This method returns the BottomBarController object.
   *
   * @return The BottomBarController object.
   */
  public static BottomBarController getBottomBarController() {
    return bottomBarController;
  }
}
