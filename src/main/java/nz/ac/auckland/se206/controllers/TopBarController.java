package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class TopBarController {
  @FXML private Label topBarTimer;
  @FXML private HBox inventoryContainer;
  @FXML private Button forfeitButton;

  public enum Item {
    SAW_BODY,
    SAW_BLADE,
    SAW_BATTERY
  }

  private static int invLength;
  private static Item[] inventory;
  private static int nextIndex = 0;

  public void initialize() throws ApiProxyException {
    /* Initialize inventory */
    invLength = Item.values().length;
    inventory = new Item[invLength];
  }

  public Label getTimerLabel() {
    return topBarTimer;
  }

  public int getItem(Item item) {
    for (int i = 0; i < invLength; i++) {
      if (inventory[i] == item) {
        return i;
      }
    }

    return -1;
  }

  public boolean hasItem(Item item) {
    if (getItem(item) == -1) {
      return false;
    } else {
      return true;
    }
  }

  public void giveItem(Item item) {
    if (!hasItem(item)) {
      inventory[nextIndex] = item;
      inventoryContainer.getChildren().add(new ImageView("/images/" + item.name() + ".png"));
      nextIndex++;
    }
  }

  public void removeItem(Item item) {
    int tempIndex = getItem(item);

    if (tempIndex != -1) {
      for (int i = tempIndex; i < invLength - 1; i++) {
        inventory[i] = inventory[i + 1];
      }
      inventory[invLength - 1] = null;
      inventoryContainer.getChildren().remove(tempIndex);
      nextIndex--;
    }
  }

  public void testPrintOut() {
    System.out.println("+-- INVENTORY --+");
    for (int i = 0; i < invLength; i++) {
      System.out.println("| " + i + ": " + inventory[i]);
    }
  }

  @FXML
  private void clickToMenu(MouseEvent event) throws IOException {
    App.setRoot(SceneManager.AppUI.INTRO);
    ChallengeTimer.cancelTimer();
    App.resetGame();
  }
}
