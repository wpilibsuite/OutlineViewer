package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OutlineViewerController {

  @FXML
  private Parent root;
  @FXML
  private Pane settingsPane;
  @FXML
  private MenuBar menuBar;
  @FXML
  private Text connectionIndicator;

  private Stage settingsStage;

  @FXML
  void onSettingsMenuItemAction(ActionEvent event) {
    if (settingsStage == null) {
      settingsStage = new Stage();
      settingsStage.setScene(new Scene(settingsPane));
      settingsStage.initStyle(StageStyle.UTILITY);
      settingsStage.initModality(Modality.APPLICATION_MODAL);
    }
    settingsStage.showAndWait();
  }

  @FXML
  void initialize() {
    NetworkTablesJNI.ConnectionListenerFunction clf = (uid, connected, conn) -> {
      if (NetworkTable.getTable("").isServer()) {
        connectionIndicator.setText("Number of Clients: "
            + NetworkTablesJNI.getConnections().length);
      } else if (connected && conn != null) {
        connectionIndicator.setText("Connected: " + conn.remote_ip);
      } else {
        connectionIndicator.setText("Disconnected");
      }
    };
    clf.apply(0, false, null);
    NetworkTablesJNI.addConnectionListener(clf, true);
  }

}
