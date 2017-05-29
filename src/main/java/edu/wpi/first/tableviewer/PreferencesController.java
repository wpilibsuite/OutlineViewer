package edu.wpi.first.tableviewer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;

import java.util.Arrays;

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
    System.out.println("Starting client");
    NetworkTableUtils.shutdown();
    String url = idField.getText();
    int port = 1735;
    if (portField.getText().matches("[0-9]+")) {
      port = Integer.parseInt(portField.getText());
    }
    if (url.isEmpty()) {
      url = "localhost";
    }
    url = url.replaceAll("^.*://", ""); // remove leading protocol
    if (url.matches("[0-9]+")) {
      // treat as a team number
      url = "roborio-" + url + "-frc.local";
    }
    System.out.println("Connecting to " + url + ":" + port);
    Prefs.setResolvedAddress(url);
    Prefs.setServer(false);
    Prefs.setPort(port);
    Prefs.setIp(idField.getText());
  }

  /**
   * Starts running in server mode, using the port set in the preferences pane.
   */
  private void startServer() {
    System.out.println("Starting server");
    NetworkTableUtils.shutdown();
    if (portField.getText().matches("[0-9]+")) {
      int port = Integer.parseInt(portField.getText());
      Prefs.setPort(port);
    }
    Prefs.setIp(idField.getText());
    Prefs.setServer(true);
  }

  /**
   * Starts running ntcore in the selected mode.
   */
  public void start() {
    System.out.println("Starting");
    if (Prefs.isServer()) {
      startServer();
    } else {
      startClient();
    }
  }

}
