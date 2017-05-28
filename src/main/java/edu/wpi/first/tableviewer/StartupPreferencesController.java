package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.Arrays;

/**
 *
 */
public class StartupPreferencesController {

  @FXML
  private TextField addressField;
  @FXML
  private CheckBox showMetaData;

  private final BooleanProperty started = new SimpleBooleanProperty(this, "started", false);

  @FXML
  private void initialize() {
    addressField.setText(Main.preferences.get("address", "localhost"));

    showMetaData.selectedProperty().setValue(Main.preferences.getBoolean("show_metadata", false));

    showMetaData.selectedProperty()
                .addListener((observable, oldValue, newValue) -> Main.preferences.putBoolean("show_metadata", newValue));
  }

  @FXML
  private void startClient() {
    System.out.println("Starting client");
    String url = addressField.getText();
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
    int port = 1735;
    if (addrPort.length == 2) {
      port = Integer.parseInt(addrPort[1]);
    }
    System.out.println("Connecting to " + address + ":" + port);
    NetworkTable.setClientMode();
    NetworkTable.setIPAddress(address);
    NetworkTable.setPort(port);
    NetworkTable.initialize();
    Main.preferences.put("resolved-address", address);
    Main.preferences.putBoolean("client", true);
    Main.preferences.put("address", addressField.getText());
    Main.preferences.putBoolean("server", false);
    started.setValue(true);
  }

  @FXML
  private void startServer() {
    System.out.println("Starting server");
    NetworkTable.setServerMode();
    if (addressField.getText().matches("[0-9]+")) {
      int port = Integer.parseInt(addressField.getText());
      NetworkTable.setPort(port);
    }
    NetworkTable.initialize();
    Main.preferences.put("address", addressField.getText());
    Main.preferences.putBoolean("server", true);
    started.setValue(true);
  }

  @FXML
  private void cancel() {
    System.out.println("Exiting");
    System.exit(0);
  }

  public Boolean getStarted() {
    return started.get();
  }

  public ObservableBooleanValue startedProperty() {
    return started;
  }

}
