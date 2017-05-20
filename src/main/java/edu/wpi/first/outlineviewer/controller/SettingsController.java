package edu.wpi.first.outlineviewer.controller;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SettingsController {

  @FXML
  private Parent root;

  @FXML
  private TextField hostTextField;

  @FXML
  private CheckBox displayMetadataCheckBox;

  @FXML
  private Button startClientButton;

  @FXML
  private Button startServerButton;

  @FXML
  void cancelButtonAction(ActionEvent event) {
    root.getScene().getWindow().hide();
  }

  @FXML
  void startClientButtonAction(ActionEvent event) {
    NetworkTable.shutdown();

    NetworkTable.setClientMode();
    if (isTeamNumber(hostTextField.getText())) {
      NetworkTable.setTeam(Integer.parseInt(hostTextField.getText()));
    } else {
      NetworkTable.setIPAddress(hostTextField.getText());
    }
    NetworkTable.initialize();

    root.getScene().getWindow().hide();
  }

  @FXML
  void startServerButtonAction(ActionEvent event) {
    NetworkTable.shutdown();

    NetworkTable.setServerMode();
    NetworkTable.setIPAddress("");
    NetworkTable.initialize();

    root.getScene().getWindow().hide();
  }

  @FXML
  void initialize() {
    hostTextField.textProperty().addListener((observable, oldValue, newValue) ->
        startClientButton.setDisable(!(InetAddresses.isInetAddress(newValue)
          || InternetDomainName.isValid(newValue)
          || isTeamNumber(newValue))));
  }

  private boolean isTeamNumber(final String text) {
    return text.matches("[1-9](\\d{1,3})?");
  }

}

