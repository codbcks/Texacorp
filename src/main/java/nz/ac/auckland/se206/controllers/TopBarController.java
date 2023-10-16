package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/**
 * This class represents the controller for the top bar in the game. It contains methods for
 * managing the inventory, giving and removing items, and printing the inventory. It also has a
 * method for returning the timer label and a method for returning the index of a specific item in
 * the inventory.
 */
public class TopBarController {

  /** An enumeration of all the items that can be in the inventory. */
  public enum Item {
    SAW_BODY,
    SAW_BLADE,
    SAW_BATTERY,
    SAW_BROKEN,
    SAW_FIXED,
    RESIN
  }

  private static int invLength;
  private static Item[] inventory;
  private static int nextIndex = 0;

  @FXML private Label topBarTimer;
  @FXML private HBox inventoryContainer;
  @FXML private Button forfeitButton;

  /**
   * The code initializes the top bar controllers by setting the timer label and initializing the
   * inventory.
   *
   * @throws ApiProxyException if there is an issue with the API proxy
   */
  public void initialize() throws ApiProxyException {
    /* Initialize inventory */
    invLength = Item.values().length;
    inventory = new Item[invLength];
  }

  /**
   * Returns the timer label for other classes to access.
   *
   * @return the timer label.
   */
  public Label getTimerLabel() {
    return topBarTimer;
  }

  /**
   * Returns the index of a specific item in the inventory.
   *
   * @param item the item to search for.
   * @return the index of the item in the inventory, or -1 if the item is not in the inventory.
   */
  public int getItem(Item item) {
    for (int i = 0; i < invLength; i++) {
      if (inventory[i] == item) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Checks if the inventory contains a specific item.
   *
   * @param item the item to check for.
   * @return true if the inventory contains the item, false otherwise.
   */
  public boolean hasItem(Item item) {
    if (getItem(item) == -1) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Adds an item to the inventory.
   *
   * @param item the item to add.
   */
  public void giveItem(Item item) {
    if (!hasItem(item)) {
      inventory[nextIndex] = item;
      inventoryContainer.getChildren().add(new ImageView("/images/" + item.name() + ".png"));
      nextIndex++;
    }
  }

  /**
   * Removes an item from the inventory.
   *
   * @param item the item to remove.
   */
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

  /** Prints out the inventory to the console. */
  public void testPrintOut() {
    System.out.println("+-- INVENTORY --+");
    for (int i = 0; i < invLength; i++) {
      System.out.println("| " + i + ": " + inventory[i]);
    }
  }

  /**
   * Returns to the main menu when the forfeit button is clicked.
   *
   * @throws IOException if there is an error with the input/output.
   */
  @FXML
  private void clickToMenu() throws IOException {
    App.setInterface(SceneManager.AppInterface.INTRO);
    ChallengeTimer.cancelTimer();
    App.resetGame();
  }
}
