package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.controllers.BottomBarController;
import nz.ac.auckland.se206.gpt.ChatMessage;

/**
 * The SceneManager class manages the different UI scenes in the application. It contains an enum
 * AppUI that lists all the different UI scenes, a HashMap sceneMap that contains all the UI
 * 'roots', and a HashMap loaderMap that contains all the FXMLLoader objects for each UI scene.
 */
public class SceneManager {

  /** An enum that lists all the different UI scenes. */
  public enum AppInterface {
    INTRO,
    IN_GAME,
    ROOM1,
    ROOM2,
    ROOM3,
    CHAT,
    LOSE,
    WIN,
    TOPBAR,
    BOTTOMBAR
  }

  // The HashMap that contains all UI 'roots'
  private static HashMap<AppInterface, Parent> sceneMap = new HashMap<>();

  // The HashMap that contains all UI 'roots'
  private static HashMap<AppInterface, FXMLLoader> loaderMap = new HashMap<>();

  /**
   * Creates a new UI instance and adds it to the sceneMap and loaderMap.
   *
   * @param ui The UI scene to create.
   * @param fxml The name of the FXML file to load.
   * @throws IOException If the FXML file cannot be loaded.
   */
  public static void createAppInterface(AppInterface ui, String fxml) throws IOException {
    loaderMap.put(ui, new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")));
    sceneMap.put(ui, loaderMap.get(ui).load());
  }

  /**
   * Returns the controller for the specified UI scene.
   *
   * @param ui The UI scene to get the controller for.
   * @return The controller for the specified UI scene.
   */
  public static Object getController(AppInterface ui) {
    return loaderMap.get(ui).getController();
  }

  // OBSELETE, REMOVE
  public static void addAppInterface(AppInterface ui, Parent root) {
    sceneMap.put(ui, root);
  }

  /**
   * Returns the UI instance corresponding with the requested UI.
   *
   * @param ui The UI scene to get the instance for.
   * @return The UI instance corresponding with the requested UI.
   */
  public static Parent getInterface(AppInterface ui) {
    return sceneMap.get(ui);
  }

  /**
   * Adds a chat message to the log in the bottom bar.
   *
   * @param chatMessage The chat message to add.
   */
  public static void addToLogEnviroMessage(ChatMessage chatMessage) {
    ((BottomBarController) getController(AppInterface.BOTTOMBAR))
        .addToLog(chatMessage, true, false, false);
  }

  /** Updates the chat in the bottom bar. */
  public static void updateChat() {
    ((BottomBarController) getController(AppInterface.BOTTOMBAR)).updateChat();
  }
}
