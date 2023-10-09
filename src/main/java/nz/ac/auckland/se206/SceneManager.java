package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.controllers.BottomBarController;

/** SceneManager holds one instance of each scene using a HashMap */
public class SceneManager {
  public enum AppUI {
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
  private static HashMap<AppUI, Parent> sceneMap = new HashMap<>();

  // The HashMap that contains all UI 'roots'
  private static HashMap<AppUI, FXMLLoader> loaderMap = new HashMap<>();

  public static void createAppUi(AppUI ui, String fxml) throws IOException {
    loaderMap.put(ui, new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")));
    sceneMap.put(ui, loaderMap.get(ui).load());
  }

  public static Object getController(AppUI ui) {
    return loaderMap.get(ui).getController();
  }

  // OBSELETE, REMOVE
  public static void addAppUI(AppUI ui, Parent root) {
    sceneMap.put(ui, root);
  }

  // This method returns the UI instance corrisponding with the requested UI.
  public static Parent getUI(AppUI ui) {
    return sceneMap.get(ui);
  }

  public static void appendChatMessage(String chatMessage, String role) {
    ((BottomBarController) getController(AppUI.BOTTOMBAR)).appendChatMessage(chatMessage, role);
  }
}
