package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.Arrays;

/**
 *
 */
public class PreferencesController {

  @FXML
  private TextField addressField;
  @FXML
  private CheckBox showMetaData;

  private final BooleanProperty cancelled = new SimpleBooleanProperty(this, "cancelled", false);
  private final BooleanProperty started = new SimpleBooleanProperty(this, "started", false);

  @FXML
  private void initialize() {
    addressField.setText(Prefs.getIp());

    showMetaData.selectedProperty().setValue(Prefs.isShowMetaData());

    showMetaData.selectedProperty()
                .addListener((__, hide, show) -> Prefs.setShowMetaData(show));
  }

  @FXML
  public void startClient() {
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
    Prefs.setResolvedAddress(address);
    Prefs.setServer(false);
    Prefs.setIp(addressField.getText());
    started.setValue(true);
  }

  @FXML
  public void startServer() {
    System.out.println("Starting server");
    NetworkTable.setServerMode();
    if (addressField.getText().matches("[0-9]+")) {
      int port = Integer.parseInt(addressField.getText());
      NetworkTable.setPort(port);
    }
    NetworkTable.initialize();
    Prefs.setIp(addressField.getText());
    Prefs.setServer(true);
    started.setValue(true);
  }

  @FXML
  public void cancel() {
    cancelled.setValue(true);
  }

  public boolean isCancelled() {
    return cancelled.get();
  }

  public ReadOnlyBooleanProperty cancelledProperty() {
    return cancelled;
  }

  public Boolean getStarted() {
    return started.get();
  }

  public ReadOnlyBooleanProperty startedProperty() {
    return started;
  }

}