package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LockPickController {
  @FXML private Rectangle lockPin0;

  @FXML
  public void pinHover(MouseEvent event) throws IOException {
    lockPin0.setFill(Color.BLACK);
  }

  @FXML
  public void pinNotHover(MouseEvent event) throws IOException {
    lockPin0.setFill(Color.BLACK);
  }
}
