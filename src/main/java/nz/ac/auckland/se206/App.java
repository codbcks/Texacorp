package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.BottomBarController;
import nz.ac.auckland.se206.controllers.Room1Controller;
import nz.ac.auckland.se206.controllers.Room2Controller;
import nz.ac.auckland.se206.controllers.Room3Controller;
import nz.ac.auckland.se206.controllers.TopBarController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  public static TextToSpeech textToSpeech;
  public static Room1Controller room1;
  public static Room2Controller room2;
  public static Room3Controller room3;
  public static TopBarController topBarController;
  public static BottomBarController bottomBarController;

  public static void main(final String[] args) {
    textToSpeech = new TextToSpeech();
    launch();
  }

  public static void setRoot(SceneManager.AppUI newUI) throws IOException {
    scene.setRoot(SceneManager.getUI(newUI));
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {

    SceneManager.createAppUi(SceneManager.AppUI.BOTTOMBAR, "bottomBar");
    SceneManager.createAppUi(SceneManager.AppUI.TOPBAR, "topBar");
    SceneManager.createAppUi(SceneManager.AppUI.ROOM1, "room1");
    SceneManager.createAppUi(SceneManager.AppUI.ROOM2, "room2");
    SceneManager.createAppUi(SceneManager.AppUI.ROOM3, "room3");
    SceneManager.createAppUi(SceneManager.AppUI.INTRO, "intro");
    SceneManager.createAppUi(SceneManager.AppUI.WIN, "win");
    SceneManager.createAppUi(SceneManager.AppUI.LOSE, "lose");

    room1 =
        (Room1Controller)
            SceneManager.getController(SceneManager.AppUI.ROOM1); // <==== Obsolete, remove.
    room2 =
        (Room2Controller)
            SceneManager.getController(SceneManager.AppUI.ROOM2); // <==== Obsolete, remove.
    room3 =
        (Room3Controller)
            SceneManager.getController(SceneManager.AppUI.ROOM3); // <==== Obsolete, remove.
    topBarController =
        (TopBarController)
            SceneManager.getController(SceneManager.AppUI.TOPBAR); // <==== Obsolete, remove.
    bottomBarController =
        (BottomBarController)
            SceneManager.getController(SceneManager.AppUI.BOTTOMBAR); // <==== Obsolete, remove.

    Parent root = SceneManager.getUI(SceneManager.AppUI.INTRO);

    scene = new Scene(root, 960, 640);

    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}
