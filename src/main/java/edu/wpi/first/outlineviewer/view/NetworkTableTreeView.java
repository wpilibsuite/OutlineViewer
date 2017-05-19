package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import edu.wpi.first.outlineviewer.model.NetworkTableParent;
import edu.wpi.first.outlineviewer.model.NetworkTableString;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.util.Arrays;
import java.util.List;

public class NetworkTableTreeView extends TreeTableView<NetworkTableData> {

  private List<NetworkTableData> data = Arrays.asList(new NetworkTableString("S1", "V1"),
    new NetworkTableString("S2", "V2"),
    new NetworkTableString("S3", "V3"));

  public NetworkTableTreeView() {
    final TreeItem<NetworkTableData> root = new TreeItem<>(new NetworkTableParent(""));
    setRoot(root);
    setShowRoot(false);

    data.forEach(d -> root.getChildren().add(new TreeItem<>(d))); // DEBUG

    TreeTableColumn<NetworkTableData, String> pathColumn = new TreeTableColumn<>("Path");
    pathColumn.setPrefWidth(200);
    pathColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("path"));

    TreeTableColumn<NetworkTableData, String> valueColumn = new TreeTableColumn<>("Value");
    valueColumn.setPrefWidth(200);
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));

    getColumns().add(pathColumn);
    getColumns().add(valueColumn);
    setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
  }
}
