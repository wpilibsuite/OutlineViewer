package edu.wpi.first.outlineviewer;

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
    idField.setText(Prefs.getIp());
    portField.setText(String.valueOf(Prefs.getPort()));
    serverModeSwitch.selectedProperty().bindBidirectional(Prefs.serverProperty());
    metadataSwitch.selectedProperty().bindBidirectional(Prefs.showMetaDataProperty());
    idField.disableProperty().bind(serverModeSwitch.selectedProperty());
  }

  /**
   * Starts running in client mode, using the address and port set in the preferences pane.
   */
  private void startClient() {
    NetworkTableUtils.shutdown();

    Prefs.setServer(false);
    Prefs.setIp(idField.getText());

    if (portField.getText().matches("[0-9]+")) {
      Prefs.setPort(Integer.parseInt(portField.getText()));
    }
  }

  /**
   * Starts running in server mode, using the port set in the preferences pane.
   */
  private void startServer() {
    NetworkTableUtils.shutdown();
    if (portField.getText().matches("[0-9]+")) {
      Prefs.setPort(Integer.parseInt(portField.getText()));
    }

    String url = idField.getText();
    if (url.isEmpty()) {
      url = "localhost";
    }
    url = url.replaceAll("^.*://", ""); // remove leading protocol
    Prefs.setIp(url);

    Prefs.setServer(true);
  }

  /**
   * Starts running ntcore in the selected mode.
   */
  public void start() {
    if (Prefs.isServer()) {
      startServer();
    } else {
      startClient();
    }
  }

}
