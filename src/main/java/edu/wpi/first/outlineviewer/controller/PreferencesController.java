package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.outlineviewer.Preferences;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;

import java.util.Optional;

/**
 * Controller for the app preferences pane.
 */
public class PreferencesController {

  @FXML
  private ToggleSwitch serverModeSwitch;
  @FXML
  private TextField idField;
  @FXML
  private ToggleSwitch defaultPortSwitch;
  @FXML
  private TextField portField;

  @FXML
  private void initialize() {
    idField.disableProperty().bind(serverModeSwitch.selectedProperty());
    portField.disableProperty().bind(defaultPortSwitch.selectedProperty());

    idField.setText(Preferences.getIp());
    portField.setText(String.valueOf(Preferences.getPort()));
    serverModeSwitch.setSelected(Preferences.isServer());
    defaultPortSwitch.setSelected(Preferences.getPort() == NetworkTableInstance.kDefaultPort);

    //When the user selects default port from non-default port we need to update the port number
    //and port text field to the default port number
    defaultPortSwitch.setOnMouseClicked(event -> {
      if (defaultPortSwitch.selectedProperty().get()) {
        Preferences.setPort(NetworkTableInstance.kDefaultPort);
        portField.setText(String.valueOf(Preferences.getPort()));
      }
    });

    Platform.runLater(() -> {
      // If the id field is not disabled, request focus.  Otherwise, the port field should request
      // focus.
      portField.requestFocus();
      idField.requestFocus();
    });
  }

  /**
   * Starts running ntcore in the selected mode.
   */
  public void save() {
    Optional<Integer> portNum = Optional.empty();

    try {
      //Try to convert the port number into an Integer to validate
      Integer val = Integer.valueOf(portField.getText());
      if (val > 0 && val <= 65535)
        portNum = Optional.of(val);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (portNum.isPresent()) {
      Preferences.setPort(portNum.get());
    } else {
      Preferences.setPort(NetworkTableInstance.kDefaultPort);
    }

    String url = idField.getText();
    if (url.isEmpty()) {
      url = "localhost";
    }
    url = url.replaceAll("^.*://", ""); // remove leading protocol
    Preferences.setIp(url);

    Preferences.setServer(serverModeSwitch.isSelected());
  }

}
