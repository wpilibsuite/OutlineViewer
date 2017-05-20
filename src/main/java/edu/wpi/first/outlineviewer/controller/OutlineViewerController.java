package edu.wpi.first.outlineviewer.controller;

import com.google.common.collect.Lists;
import edu.wpi.first.outlineviewer.model.NetworkTableData;
import edu.wpi.first.outlineviewer.view.NetworkTableTreeView;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
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

import java.util.Arrays;
import java.util.Queue;

public class OutlineViewerController {

  @FXML
  private Parent root;
  @FXML
  private Pane settingsPane;
  @FXML
  private MenuBar menuBar;
  @FXML
  private NetworkTableTreeView networkTableTreeView;
  @FXML
  private Text connectionIndicator;

  private Stage settingsStage;
  private final NetworkTableData rootData = new NetworkTableData("Root");

  @FXML
  void onSettingsMenuItemAction(ActionEvent event) {
    if (settingsStage == null) {
      settingsStage = new Stage();
      settingsStage.setTitle("Settings");
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

    NetworkTablesJNI.addEntryListener("", ((uid, key, value, flags) -> {
      Queue<String> keys = Lists.newLinkedList(Arrays.asList(key.split("\\/")));

      NetworkTableData next = rootData;
      while (keys.size() > 1) {
        if (keys.peek().isEmpty()) {
          keys.remove();
        } else if (next.getChildren().containsKey(keys.peek())) {
          next = next.getChildren().get(keys.poll());
        } else {
          NetworkTableData newData = NetworkTableData.createNetworkTableData(keys.poll(), value);
          next.addChild(newData);
          next = newData;
        }
      }

      if (next.getChildren().containsKey(keys.peek())) {
        // TODO: Make this type safe
        next.getChildren().get(keys.poll()).valueProperty().setValue(value);
      } else {
        next.addChild(NetworkTableData.createNetworkTableData(keys.poll(), value));
      }
    }), ITable.NOTIFY_IMMEDIATE
        | ITable.NOTIFY_LOCAL
        | ITable.NOTIFY_NEW
        | ITable.NOTIFY_UPDATE);

    NetworkTablesJNI.addEntryListener("", ((uid, key, value, flags) -> {
      Queue<String> keys = Lists.newLinkedList(Arrays.asList(key.split("\\/")));
      if (keys.peek().isEmpty()) {
        keys.remove();
      }
      rootData.getChild(keys).ifPresent(NetworkTableData::remove);
    }), ITable.NOTIFY_LOCAL
        | ITable.NOTIFY_DELETE);


    networkTableTreeView.setRootData(rootData);
  }

  public NetworkTableData getRootData() {
    return rootData;
  }
}
