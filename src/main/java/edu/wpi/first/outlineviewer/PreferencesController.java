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

  private int result = 0;

  @FXML
  void cancelButtonAction(ActionEvent event) {
    result = 0;
    root.getScene().getWindow().hide();
  }

  @FXML
  void startClientButtonAction(ActionEvent event) {
    NetworkTable.setClientMode();
    NetworkTable.setIPAddress(hostTextField.getText());
    NetworkTable.initialize();

    result = 1;
    root.getScene().getWindow().hide();
  }

  @FXML
  void startServerButtonAction(ActionEvent event) {
    NetworkTable.setServerMode();
    NetworkTable.setIPAddress("");
    NetworkTable.initialize();

    result = 2;
    root.getScene().getWindow().hide();
  }

  public int getResult() {
    return result;
  }

}

