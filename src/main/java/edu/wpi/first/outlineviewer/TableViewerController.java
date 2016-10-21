package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.text.Text;

import java.util.LinkedList;

public class TableViewerController {

  @FXML
  private TreeTableView<TableEntry> table;

  @FXML
  private Text connectionIndicator;

  private TableEntryParent root;

  @FXML
  void initialize() {
    TreeTableColumn<TableEntry, String> keyCol = new TreeTableColumn<>("Key");
    TreeTableColumn<TableEntry, String> valueCol = new TreeTableColumn<>("Value");
    TreeTableColumn<TableEntry, String> typeCol = new TreeTableColumn<>("Type");
    table.getColumns().setAll(keyCol, valueCol, typeCol);

    keyCol.setCellValueFactory(p -> p.getValue().getValue().getKey());
    valueCol.setCellValueFactory(p -> p.getValue().getValue().getValue());
    typeCol.setCellValueFactory(p -> p.getValue().getValue().getType());

    keyCol.setSortType(TreeTableColumn.SortType.DESCENDING);
    table.setSortMode(TreeSortMode.ALL_DESCENDANTS);

    root = new TableEntryParent("Root");
    table.setRoot(root.getTreeItem());

    NetworkTablesJNI.ConnectionListenerFunction listenerFunction = (uid, connected, conn) -> {
      if (NetworkTable.getTable("").isServer()) {
        connectionIndicator.setText("Number of Clients:\t"
            + NetworkTablesJNI.getConnections().length);
      } else {
        connectionIndicator.setText("Connection Status: " + connected);
      }
    };
    listenerFunction.apply(0, false, null);
    NetworkTablesJNI.addConnectionListener(listenerFunction, true);

    NetworkTablesJNI.addEntryListener("", (uid, key, value, flags) -> {
      LinkedList<String> subTables = splitDiscardingEmpty(key, "/");
      LinkedList<TableEntryParent> parents = new LinkedList<>();
      parents.add(root);
      loop:
      for (int i = 0; i < subTables.size(); i++) {
        if (i == (subTables.size() - 1)) {
          final TableEntryData newItem = new TableEntryData(subTables.get(i), value);
          parents.getLast().getTreeItem().getChildren().add(newItem.getTreeItem());
          newItem.setupListener();
        } else {
          for (TreeItem<TableEntry> t : parents.get(i).getTreeItem().getChildren()) {
            if (t.getValue().getKey().getValue().equals(subTables.get(i))
                && t.getValue() instanceof TableEntryParent) {
              parents.add((TableEntryParent) t.getValue());
              continue loop;
            }
          }
          final TableEntryParent newParent = new TableEntryParent(subTables.get(i));
          parents.get(i).getTreeItem().getChildren().add(newParent.getTreeItem());
          parents.add(newParent);
        }
      }
    }, ITable.NOTIFY_IMMEDIATE | ITable.NOTIFY_NEW);
  }

  private LinkedList<String> splitDiscardingEmpty(String str, String separator) {
    LinkedList<String> results = new LinkedList<>();
    for (String string : str.split(separator)) {
      if (string.length() > 0) {
        results.add(string);
      }
    }
    return results;
  }
}
