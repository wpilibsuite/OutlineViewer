package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.Preferences;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;

/**
 * Controller for the app preferences pane.
 */
public class PreferencesController {

  @FXML
  private TextField idField;
  @FXML
  private TextField portField;
  @FXML
  private ToggleSwitch serverModeSwitch;
  @FXML
  private ToggleSwitch metadataSwitch;

  @FXML
  private void initialize() {
    idField.disableProperty().bind(serverModeSwitch.selectedProperty());

    idField.setText(Preferences.getIp());
    portField.setText(String.valueOf(Preferences.getPort()));
    serverModeSwitch.setSelected(Preferences.isServer());
    metadataSwitch.setSelected(Preferences.isShowMetaData());

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
    if (portField.getText().matches("[0-9]+")) {
      Preferences.setPort(Integer.parseInt(portField.getText()));
    } else {
      Preferences.setPort(NetworkTable.DEFAULT_PORT);
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
