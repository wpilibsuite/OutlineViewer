package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class PreferencesController {

  @FXML
  private Parent root;

  @FXML
  private TextField hostTextField;

  @FXML
  private CheckBox displayMetadataCheckBox;

  @FXML
  void cancelButtonAction(ActionEvent event) {
    root.getScene().getWindow().hide();
  }

  @FXML
  void startClientButtonAction(ActionEvent event) {
    NetworkTable.setClientMode();
    NetworkTable.setIPAddress(hostTextField.getText());
    NetworkTable.initialize();

    root.getScene().getWindow().hide();
  }

  @FXML
  void startServerButtonAction(ActionEvent event) {
    NetworkTable.setServerMode();
    NetworkTable.setIPAddress("");
    NetworkTable.initialize();

    root.getScene().getWindow().hide();
  }

}

