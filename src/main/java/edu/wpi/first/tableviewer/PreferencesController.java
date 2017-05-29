package edu.wpi.first.tableviewer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;

import java.util.Arrays;

/**
 *
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

  private void startClient() {
    System.out.println("Starting client");
    NetworkTableUtils.shutdown();
    String url = idField.getText();
    if (url.isEmpty()) {
      url = "localhost:1735";
    }
    url = url.replaceAll("^.*://", ""); // remove leading protocol
    String[] addrPort = url.split(":");
    System.out.println(Arrays.toString(addrPort));
    String address = addrPort[0];
    if (address.matches("[0-9]+")) {
      // treat as a team number
      address = "roborio-" + address + "-frc.local";
    }
    int port = Prefs.getPort();
    if (addrPort.length == 2) {
      port = Integer.parseInt(addrPort[1]);
    }
    System.out.println("Connecting to " + address + ":" + port);
    Prefs.setResolvedAddress(address);
    Prefs.setServer(false);
    Prefs.setPort(port);
    Prefs.setIp(idField.getText());
  }

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

  public void start() {
    System.out.println("Starting");
    if (Prefs.isServer()) {
      startServer();
    } else {
      startClient();
    }
  }

}
