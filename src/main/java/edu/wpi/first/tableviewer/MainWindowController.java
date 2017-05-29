package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.AddBooleanDialog;
import edu.wpi.first.tableviewer.dialog.AddNumberDialog;
import edu.wpi.first.tableviewer.dialog.AddStringDialog;
import edu.wpi.first.tableviewer.dialog.Dialogs;
import edu.wpi.first.tableviewer.dialog.PreferencesDialog;
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
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
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

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.wpi.first.tableviewer.NetworkTableUtils.concat;
import static edu.wpi.first.tableviewer.NetworkTableUtils.normalize;
import static edu.wpi.first.tableviewer.NetworkTableUtils.simpleKey;

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

  private static final PseudoClass CLIENT = PseudoClass.getPseudoClass("client");
  private static final PseudoClass SERVER = PseudoClass.getPseudoClass("server");
  private static final PseudoClass FAILED = PseudoClass.getPseudoClass("failed");

  public void updateConnectionLabel() {
    if (NetworkTableUtils.isRunning()) {
      if (NetworkTableUtils.isServer()) {
        if (NetworkTableUtils.failed()) {
          serverFail();
        } else if (NetworkTableUtils.starting()) {
          serverStarting();
        } else { // success
          serverSuccess();
        }
      } else if (NetworkTableUtils.isClient()) {
        if (NetworkTableUtils.failed()) {
          clientFail();
        } else if (NetworkTableUtils.starting()) {
          clientStarting();
        } else { // success
          clientSuccess();
        }
      } else {
        System.out.println("Running, but not in server or client mode");
        generalFailure();
      }
    } else {
      System.out.println("Not running anything");
      generalFailure();
    }
    refreshWindow();
  }

  private void refreshWindow() {
  }

  private void clientStarting() {
    connectionLabel.setText("Connecting to " + Prefs.getResolvedAddress() + "...");
    connectionBackground.pseudoClassStateChanged(CLIENT, true);
    connectionBackground.pseudoClassStateChanged(SERVER, false);
    connectionBackground.pseudoClassStateChanged(FAILED, false);
  }

  private void clientFail() {
    String text = "No connection";
    String addr = Prefs.getResolvedAddress();
    if (addr != null) {
      text += " to " + addr;
    }
    connectionLabel.setText(text);
    connectionBackground.pseudoClassStateChanged(CLIENT, true);
    connectionBackground.pseudoClassStateChanged(SERVER, false);
    connectionBackground.pseudoClassStateChanged(FAILED, true);
  }

  private void clientSuccess() {
    connectionLabel.setText("Connected to server at " + Prefs.getResolvedAddress());
    connectionBackground.pseudoClassStateChanged(CLIENT, true);
    connectionBackground.pseudoClassStateChanged(SERVER, false);
    connectionBackground.pseudoClassStateChanged(FAILED, false);
  }

  private void serverStarting() {
    connectionLabel.setText("Starting server...");
    connectionBackground.pseudoClassStateChanged(CLIENT, false);
    connectionBackground.pseudoClassStateChanged(SERVER, true);
    connectionBackground.pseudoClassStateChanged(FAILED, false);
  }

  private void serverFail() {
    connectionLabel.setText("Could not run server");
    connectionBackground.pseudoClassStateChanged(CLIENT, false);
    connectionBackground.pseudoClassStateChanged(SERVER, true);
    connectionBackground.pseudoClassStateChanged(FAILED, true);
  }

  private void serverSuccess() {
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
    connectionBackground.pseudoClassStateChanged(CLIENT, false);
    connectionBackground.pseudoClassStateChanged(SERVER, true);
    connectionBackground.pseudoClassStateChanged(FAILED, false);
  }

  private void generalFailure() {
    connectionLabel.setText("Something went terribly wrong");
    connectionBackground.pseudoClassStateChanged(CLIENT, false);
    connectionBackground.pseudoClassStateChanged(SERVER, false);
    connectionBackground.pseudoClassStateChanged(FAILED, true);
  }

  @FXML
  private void initialize() {
    NetworkTablesJNI.addEntryListener("",
                                      (uid, key, value, flags) -> Platform.runLater(() -> makeBranches(key, value, flags)),
                                      0xFF);
    NetworkTablesJNI.addConnectionListener((uid, connected, conn) -> {
      Platform.runLater(this::updateConnectionLabel);
    }, true);
    Prefs.serverProperty().addListener(__ -> Platform.runLater(this::updateConnectionLabel));
    Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
    }).scheduleAtFixedRate(() -> Platform.runLater(this::updateConnectionLabel), 0L, 1000, TimeUnit.MILLISECONDS);

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

    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    tableView.setOnKeyPressed(event -> {
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
      String key = entry.getKey(); // entry keys are guaranteed to be normalized
      // Use raw object put from NetworkTable API (JNI doesn't support it)
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
      String key = entry.getKey();
      ContextMenu cm = new ContextMenu();

      if (entry.getValue() == null) {
        // Add the 'add x' items
        MenuItem string = new MenuItem("Add string");
        string.setOnAction(a -> {
          new AddStringDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putString(k, (String) data.getValue());
          });
        });

        MenuItem number = new MenuItem("Add number");
        number.setOnAction(a -> {
          new AddNumberDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putDouble(k, (Double) data.getValue());
          });
        });

        MenuItem bool = new MenuItem("Add boolean");
        bool.setOnAction(a -> {
          new AddBooleanDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putBoolean(k, (Boolean) data.getValue());
          });
        });

        cm.getItems().addAll(string, number, bool, new SeparatorMenuItem());
      }

      if (!key.isEmpty() && entry.getValue() != null) {
        String flagKey = normalize(key).substring(1);
        MenuItem setPersistent;

        if (NetworkTableUtils.isPersistent(flagKey)) {
          // Make the key persistent
          setPersistent = new MenuItem("Set transient");
          setPersistent.setOnAction(__ -> NetworkTableUtils.clearPersistent(flagKey));
        } else {
          // Make the entry persistent
          setPersistent = new MenuItem("Set persistent");
          setPersistent.setOnAction(__ -> NetworkTableUtils.setPersistent(flagKey));
        }

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(__ -> {
          tableView.getSelectionModel()
                   .getSelectedItems()
                   .forEach(i -> remove(i.getValue().getKey()));
        });

        cm.getItems().addAll(setPersistent, delete);
      } else {
        // Remove the separator
        cm.getItems().remove(cm.getItems().size() - 1);
      }

      tableView.setContextMenu(cm);
      cm.show(tableView, e.getScreenX(), e.getScreenY());
    });

    filter.addListener((obs, o, n) -> filterChanged(n));
    if (Prefs.isShowMetaData()) {
      showMetadata();
    } else {
      hideMetadata();
    }
    Prefs.showMetaDataProperty().addListener((__, hide, show) -> {
      if (show) {
        showMetadata();
      } else {
        hideMetadata();
      }
      // dirty hack to refresh the view, otherwise the tree won't render correctly
      refreshWindow();
    });
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

  private void makeBranches(String key, Object value, int flags) {
    key = NetworkTableUtils.normalize(key);
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

  @FXML
  private void showPrefs() throws IOException {
    PreferencesDialog dialog = new PreferencesDialog("Preferences", ButtonType.FINISH);
    Platform.runLater(() -> Dialogs.center(dialog.getDialogPane().getScene().getWindow()));
    dialog.showAndWait()
          .map(ButtonType.FINISH::equals)
          .ifPresent(__ -> dialog.getController().start());
  }

}
