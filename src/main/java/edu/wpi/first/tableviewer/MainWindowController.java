package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.AddBooleanDialog;
import edu.wpi.first.tableviewer.dialog.AddNumberDialog;
import edu.wpi.first.tableviewer.dialog.AddStringDialog;
import edu.wpi.first.wpilibj.networktables.ConnectionInfo;
import edu.wpi.first.wpilibj.networktables.EntryInfo;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class MainWindowController {

  @FXML
  private Pane root;

  @FXML
  private TreeTableView<TableEntryData> tableView;
  @FXML
  private TreeItem<TableEntryData> ntRoot;

  @FXML
  private TreeTableColumn<TableEntryData, String> keyColumn;
  @FXML
  private TreeTableColumn<TableEntryData, Object> valueColumn;
  @FXML
  private TreeTableColumn<TableEntryData, String> typeColumn;

  @FXML
  private ToolBar searchBar;
  @FXML
  private Button closeSearchButton;
  @FXML
  private TextField searchField;

  @FXML
  private Pane connectionBackground;
  @FXML
  private Label connectionLabel;

  private boolean showMetadata = false;

  private final Predicate<TableEntryData> metadataFilter = x -> showMetadata || !x.isMetadata();
  private final Property<Predicate<TableEntryData>> filter = new SimpleObjectProperty<>(this, "filter", Predicates.always());

  public void updateConnectionLabel(boolean isConnected, ConnectionInfo connectionInfo) {
    if (Main.preferences.getBoolean("server", false)) {
      // running server
      String text = "Running server";
      int numClients = NetworkTablesJNI.getConnections().length;
      switch (numClients) {
        case 0:
          text += " (No clients)";
          break;
        case 1:
          text += " (1 client)";
          break;
        default:
          text += " (" + numClients + " clients)";
          break;
      }
      connectionLabel.setText(text);
      connectionLabel.setStyle("-fx-text-fill: white");
      connectionBackground.setStyle("-fx-background-color: linear-gradient(to bottom, #555, #222)");
    } else if (isConnected) {
      // client with connection
      connectionLabel.setText("Connected to server at " + Main.preferences.get("resolved-address", connectionInfo.remote_ip));
      connectionLabel.setStyle("-fx-text-fill: white");
      connectionBackground.setStyle("-fx-background-color: linear-gradient(to bottom, blue, darkblue)");
    } else {
      // client, no connection
      String text = "No connection";
      String addr = Main.preferences.get("resolved-address", null);
      if (addr != null) {
        text += " to " + addr;
      }
      connectionLabel.setText(text);
      connectionLabel.setStyle("-fx-text-fill: white");
      connectionBackground.setStyle("-fx-background-color: linear-gradient(to bottom, orangered, darkred)");
    }
  }

  @FXML
  private void initialize() {
    NetworkTablesJNI.addEntryListener("",
                                      (uid, key, value, flags) -> Platform.runLater(() -> makeBranches(key, value, flags)),
                                      0xFF);
    NetworkTablesJNI.addConnectionListener((uid, connected, conn) -> {
      Platform.runLater(() -> updateConnectionLabel(connected, conn));
    }, true);

    root.setOnKeyPressed(event -> {
      if (event.isControlDown() && event.getCode() == KeyCode.F) {
        searchBar.setManaged(true);
        Platform.runLater(searchField::requestFocus);
        searchField.selectAll();
      }
      if (event.getCode() == KeyCode.ESCAPE) {
        searchField.setText("");
        searchBar.setManaged(false);
      }
    });

    tableView.setSortPolicy(view -> {
      sort(tableView.getRoot());
      return true;
    });

    tableView.setOnKeyTyped(event -> {
      if (event.getCode() == KeyCode.DELETE) {
        ObservableList<TreeItem<TableEntryData>> selectedItems = tableView.getSelectionModel().getSelectedItems();
        selectedItems.stream()
                     .filter(Objects::nonNull)
                     .map(TreeItem::getValue)
                     .map(TableEntryData::getKey)
                     .forEach(this::remove);
      }
    });

    tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);


    keyColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(simpleKey(param.getValue().getValue().getKey())));
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
    typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

    valueColumn.setCellFactory(param -> new TableEntryDataTreeTableCell());

    valueColumn.setOnEditCommit(e -> {
      TableEntryData entry = e.getRowValue().getValue();
      String key = entry.getKey();
      NetworkTable.getTable(key.substring(0, key.lastIndexOf('/'))).putValue(simpleKey(key), e.getNewValue());
    });

    tableView.setRowFactory(param -> {
      final TreeTableRow<TableEntryData> row = new TreeTableRow<>();
      // Clicking on an empty row should clear the selection.
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        if (row.getTreeItem() == null) {
          tableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });

    searchField.textProperty().addListener((__, oldText, newText) -> {
      if (newText.isEmpty()) {
        setFilter(null);
      } else {
        String lower = newText.toLowerCase();
        setFilter(data -> data.getKey().toLowerCase().contains(lower)
            || data.getValue().toString().toLowerCase().contains(lower)
            || data.getType().toLowerCase().contains(lower));
      }
    });

    tableView.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        TreeItem<TableEntryData> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
          return;
        }
        int row = tableView.getRow(selected);
        tableView.edit(row, valueColumn);
      }
    });

    tableView.setOnContextMenuRequested(e -> {
      if (tableView.getContextMenu() != null) {
        tableView.getContextMenu().hide();
      }
      TreeItem<TableEntryData> selected = tableView.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
      TableEntryData entry = selected.getValue();
      String key = normalizeKey(entry.getKey()).substring(1); // remove leading slash
      ContextMenu cm = new ContextMenu();

      if (entry.getValue() == null) {
        // Add the 'add x' items
        MenuItem string = new MenuItem("Add string");
        string.setOnAction(a -> {
          new AddStringDialog().showAndWait().ifPresent(data -> {
            NetworkTable.getTable(key).putString(data.getKey(), (String) data.getValue());
          });
        });

        MenuItem number = new MenuItem("Add number");
        number.setOnAction(a -> {
          new AddNumberDialog().showAndWait().ifPresent(data -> {
            NetworkTable.getTable(key).putNumber(data.getKey(), (Double) data.getValue());
          });
        });

        MenuItem bool = new MenuItem("Add boolean");
        bool.setOnAction(a -> {
          new AddBooleanDialog().showAndWait().ifPresent(data -> {
            NetworkTable.getTable(key).putBoolean(data.getKey(), (Boolean) data.getValue());
          });
        });

        cm.getItems().addAll(string, number, bool, new SeparatorMenuItem());
      }

      if (!key.isEmpty() && entry.getValue() != null) {
        String full = normalizeKey(key);
        MenuItem setPersistent;

        if (NetworkTableUtils.rootTable.isPersistent(full)) {
          // Make the key persistent
          setPersistent = new MenuItem("Set transient");
          setPersistent.setOnAction(__ -> NetworkTableUtils.rootTable.clearPersistent(full));
        } else {
          // Make the entry persistent
          setPersistent = new MenuItem("Set persistent");
          setPersistent.setOnAction(__ -> NetworkTableUtils.rootTable.setPersistent(full));
        }

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(a -> remove(entry.getKey()));

        cm.getItems().addAll(setPersistent, delete);
      } else {
        // Remove the separator
        cm.getItems().remove(cm.getItems().size() - 1);
      }

      tableView.setContextMenu(cm);
      cm.show(tableView, e.getScreenX(), e.getScreenY());
    });

    filter.addListener((obs, o, n) -> filterChanged(n));
  }

  private void remove(String key) {
    if (NetworkTablesJNI.containsKey(key)) {
      NetworkTablesJNI.deleteEntry(key);
    } else {
      // subtable
      EntryInfo[] entries = NetworkTablesJNI.getEntries(key, 0xFF);
      Stream.of(entries)
            .map(entryInfo -> entryInfo.name)
            .forEach(this::remove);
      makeBranches(key, null, ITable.NOTIFY_DELETE);
    }
  }

  public void showMetadata() {
    showMetadata = true;
    setFilter(null);
  }

  public void hideMetadata() {
    showMetadata = false;
    setFilter(metadataFilter);
  }

  /**
   * Sorts tree nodes recursively in order of branches before leaves, then alphabetically.
   *
   * @param node the root node to sort
   */
  private void sort(TreeItem<TableEntryData> node) {
    if (!node.isLeaf()) {
      boolean wasExpanded = node.isExpanded();
      FXCollections.sort(node.getChildren(),
                         ((Comparator<TreeItem<TableEntryData>>) (a, b) -> a.isLeaf() ? b.isLeaf() ? 0 : 1 : -1)
                             .thenComparing(Comparator.comparing(item -> item.getValue().getKey())));
      node.getChildren().forEach(this::sort);
      node.setExpanded(wasExpanded);
    }
  }

  private void filterChanged(Predicate<TableEntryData> filter) {
    if (filter == null || filter.equals(Predicates.always())) {
      tableView.setRoot(ntRoot);
    } else {
      TreeItem<TableEntryData> filteredRoot = new TreeItem<>();
      filteredRoot.setValue(ntRoot.getValue());
      filteredRoot.setExpanded(ntRoot.isExpanded());
      filter(ntRoot, filter, filteredRoot);
      tableView.setRoot(filteredRoot);
      tableView.sort();
    }
  }


  private <T> void filter(TreeItem<T> allRoot,
                          Predicate<? super T> filter,
                          TreeItem<T> filteredRoot) {
    for (TreeItem<T> child : allRoot.getChildren()) {
      // Copy the child to avoid mucking with the real tree
      TreeItem<T> filteredChild = new TreeItem<>();
      filteredChild.setValue(child.getValue());
      filteredChild.setExpanded(child.isExpanded());
      // Recurse; filter children
      filter(child, filter, filteredChild);
      if (matches(child, filter)) {
        filteredRoot.getChildren().add(filteredChild);
      }
    }
  }

  private <T> boolean matches(TreeItem<T> treeItem, Predicate<? super T> predicate) {
    if (treeItem.isLeaf()) {
      return predicate.test(treeItem.getValue());
    } else {
      return treeItem.getChildren().stream().anyMatch(c -> matches(c, predicate));
    }
  }

  /**
   * Normalizes a network table key to start with exactly one leading slash ("/").
   */
  private static String normalizeKey(String key) {
    while (key.startsWith("//")) {
      // lazy
      key = key.substring(1);
    }
    if (!key.startsWith("/")) {
      key = "/" + key;
    }
    return key;
  }

  private static String simpleKey(String key) {
    if (key.isEmpty() || key.equals("/")) {
      return "Root";
    }
    if (!key.contains("/")) {
      return key;
    }
    return key.substring(key.lastIndexOf('/') + 1);
  }

  private void makeBranches(String key, Object value, int flags) {
    key = normalizeKey(key);
    boolean deleted = (flags & ITable.NOTIFY_DELETE) != 0;
    List<String> pathElements = Stream.of(key.split("/"))
                                      .filter(s -> !s.isEmpty())
                                      .collect(Collectors.toList());
    TreeItem<TableEntryData> current = ntRoot;
    TreeItem<TableEntryData> parent;
    StringBuilder k = new StringBuilder();
    for (int i = 0; i < pathElements.size(); i++) {
      String pathElement = pathElements.get(i);
      k.append("/").append(pathElement);
      parent = current;
      current = current.getChildren().stream().filter(item -> item.getValue().getKey().equals(k.toString())).findFirst().orElse(null);
      if (deleted) {
        if (current == null) {
          break;
        } else if (i == pathElements.size() - 1) {
          // last
          parent.getChildren().remove(current);
        }
      } else if (i == pathElements.size() - 1) {
        if (current == null) {
          current = new TreeItem<>(new TableEntryData(key, value));
          parent.getChildren().add(current);
        } else {
          current.getValue().setValue(value);
        }
      } else if (current == null) {
        current = new TreeItem<>(new TableEntryData(k.toString(), null));
        current.setExpanded(true);
        parent.getChildren().add(current);
      }
    }
    tableView.sort();
    filterChanged(getFilter()); // prompts a refresh of filtered values
  }

  /**
   * Sets a predicate to use to filter entries matching certain criteria.
   *
   * @param filter a filter for table tables.
   */
  public void setFilter(Predicate<TableEntryData> filter) {
    if (filter == null || filter.equals(Predicates.always())) {
      this.filter.setValue(metadataFilter);
    } else {
      this.filter.setValue(metadataFilter.and(filter));
    }
  }

  /**
   * Gets the predicate used to filter the table entries to display.
   */
  public Predicate<TableEntryData> getFilter() {
    return filter.getValue();
  }

  public Property<Predicate<TableEntryData>> filterProperty() {
    return filter;
  }

  @FXML
  private void close() {
    System.exit(0);
  }

  @FXML
  private void clearSearch() {
    searchField.setText("");
    searchField.requestFocus();
  }

}
