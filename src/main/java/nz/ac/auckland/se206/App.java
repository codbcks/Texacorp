package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  public static TextToSpeech textToSpeech;
  public static RoomController mainRoom;

  public static void main(final String[] args) {
    textToSpeech = new TextToSpeech();
    launch();
  }

  public static void setRoot(SceneManager.AppUI newUI) throws IOException {
    scene.setRoot(SceneManager.getUI(newUI));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneManager.addAppUI(SceneManager.AppUI.INTRO, loadFxml("intro"));
    SceneManager.addAppUI(SceneManager.AppUI.ROOM, loadFxml("room"));
    SceneManager.addAppUI(SceneManager.AppUI.LOCKPICK, loadFxml("lockPick"));
    SceneManager.addAppUI(SceneManager.AppUI.LOSE, loadFxml("lose"));
    SceneManager.addAppUI(SceneManager.AppUI.WIN, loadFxml("win"));
    Parent root = SceneManager.getUI(SceneManager.AppUI.INTRO);

    scene = new Scene(root, 1200, 640);

    String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
    scene.getStylesheets().add(cssPath);

    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}
