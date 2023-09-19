package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

/** SceneManager holds one instance of each scene using a HashMap */
public class SceneManager {
  public enum AppUI {
    INTRO,
    ROOM1,
    ROOM2,
    ROOM3,
    CHAT,
    LOCKPICK,
    LOSE,
    WIN,
    TOPBAR
  }

  // The HashMap that contains all UI 'roots'
  private static HashMap<AppUI, Parent> sceneMap = new HashMap<>();

  // This method adds a new instance of one of the UIs to the HashMap
  public static void addAppUI(AppUI ui, Parent root) {
    sceneMap.put(ui, root);
  }

  // This method returns the UI instance corrisponding with the requested UI.
  public static Parent getUI(AppUI ui) {
    return sceneMap.get(ui);
  }
}
