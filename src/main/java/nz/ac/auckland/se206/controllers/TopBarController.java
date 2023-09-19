package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.ChallengeTimer;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class TopBarController {
  @FXML private Label topBarTimer;

  public void initialize() throws ApiProxyException {
    // Set challenge timer
    ChallengeTimer.setCurrentLabelTimer(topBarTimer);
    ChallengeTimer.startTimer();
  }
}
