package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class NetworkTableTreeView extends TreeTableView<NetworkTableData> {

  public NetworkTableTreeView() {
    setShowRoot(false);

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

  public void setRootData(final NetworkTableData root) {
    setRoot(getTreeItem(root));
  }

  private static TreeItem<NetworkTableData> getTreeItem(final NetworkTableData data) {
    TreeItem<NetworkTableData> item = new TreeItem<>(data);

    data.getChildren().stream()
        .map(NetworkTableTreeView::getTreeItem)
        .forEach(item.getChildren()::add);

    data.getChildren().addListener((ListChangeListener.Change<? extends NetworkTableData> c) -> {
      while (c.next()) {
        if (c.wasAdded()) {
          c.getAddedSubList().stream()
              .map(NetworkTableTreeView::getTreeItem)
              .forEach(item.getChildren()::add);
        }
      }
    });

    return item;
  }

}
