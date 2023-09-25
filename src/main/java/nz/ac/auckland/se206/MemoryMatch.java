package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MemoryMatch extends GridPane {
  private Button[] cards = new Button[16];
  private List<Image> symbols = loadCardImages();
  @FXML private Button firstCard = null;
  @FXML private Button secondCard = null;

  public MemoryMatch() {
    initialize();
  }

  private List<Image> loadCardImages() {

    List<Image> cardImages = new ArrayList<>();
    try {
      for (int i = 1; i <= 4; i++) {
        Image image = new Image("/images/card" + i + ".png");
        cardImages.add(image);
        cardImages.add(image);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return cardImages;
  }

  private void initialize() {
    Collections.shuffle(symbols);

    for (int i = 0; i < 16; i++) {
      Image symbol = symbols.get(i);
      cards[i] = new Button("");
      cards[i].setPrefSize(60, 60);
      cards[i].setUserData(symbol);
      cards[i].setOnAction(e -> revealCard((Button) e.getSource()));
      this.add(cards[i], i % 4, i / 4);
    }
  }

  private void revealCard(Button selectedCard) {
    if (firstCard == null || secondCard == null) {
      selectedCard.setGraphic(new ImageView((Image) selectedCard.getUserData()));
      selectedCard.setDisable(true);
      if (firstCard == null) {
        firstCard = selectedCard;
      } else if (secondCard == null) {
        secondCard = selectedCard;

        if (!firstCard.getUserData().equals(secondCard.getUserData())) {
          firstCard.setDisable(false);
          secondCard.setDisable(false);
          firstCard.setGraphic(null);
          secondCard.setGraphic(null);
        }
        firstCard = null;
        secondCard = null;
      }
    }
  }
}
