package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.NetworkTableData;
import javafx.collections.MapChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class NetworkTableTreeView extends TreeTableView<NetworkTableData> {

  /**
   * Create a new NetworkTableTreeView.  Will not show any data until
   * {@link #setRootData(NetworkTableData)} is called.
   */
  public NetworkTableTreeView() {
    setShowRoot(false);

    TreeTableColumn<NetworkTableData, String> pathColumn = new TreeTableColumn<>("Key");
    pathColumn.setPrefWidth(200);
    pathColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("key"));

    TreeTableColumn<NetworkTableData, ?> valueColumn = new TreeTableColumn<>("Value");
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
    item.setExpanded(true);

    data.getChildren().values().stream()
        .map(NetworkTableTreeView::getTreeItem)
        .forEach(item.getChildren()::add);

    data.getChildren().addListener(
        (MapChangeListener.Change<? extends String, ? extends NetworkTableData> change) -> {
          if (change.wasAdded()) {
            item.getChildren().add(getTreeItem(change.getValueAdded()));
          } else if (change.wasRemoved()) {
            item.getChildren().removeIf(t -> t.getValue().keyProperty().get()
                .equals(change.getValueRemoved().keyProperty().get()));
          }
        });

    return item;
  }

}
