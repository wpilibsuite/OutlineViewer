package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.component.NetworkTableTree;
import edu.wpi.first.tableviewer.dialog.AddBooleanDialog;
import edu.wpi.first.tableviewer.dialog.AddNumberDialog;
import edu.wpi.first.tableviewer.dialog.AddStringDialog;
import edu.wpi.first.tableviewer.dialog.Dialogs;
import edu.wpi.first.tableviewer.dialog.PreferencesDialog;
import edu.wpi.first.tableviewer.entry.Entry;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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
  private NetworkTableTree tableView;
  @FXML
  private TreeItem<Entry> ntRoot;

  @FXML
  private TreeTableColumn<Entry, String> keyColumn;
  @FXML
  private TreeTableColumn<Entry, Object> valueColumn;
  @FXML
  private TreeTableColumn<Entry, String> typeColumn;

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

  private final Predicate<Entry> metadataFilter = x -> showMetadata || !x.isMetadata();

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

    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    tableView.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.DELETE) {
        ObservableList<TreeItem<Entry>> selectedItems = tableView.getSelectionModel().getSelectedItems();
        selectedItems.stream()
                     .filter(Objects::nonNull)
                     .map(TreeItem::getValue)
                     .map(Entry::getKey)
                     .forEach(NetworkTableUtils::delete);
      }
    });

    tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);


    keyColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(simpleKey(param.getValue().getValue().getKey())));
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
    typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

    valueColumn.setCellFactory(param -> new TableEntryTreeTableCell());

    valueColumn.setOnEditCommit(e -> {
      Entry entry = e.getRowValue().getValue();
      String key = entry.getKey(); // entry keys are guaranteed to be normalized
      // Use raw object put from NetworkTable API (JNI doesn't support it)
      NetworkTable.getTable(key.substring(0, key.lastIndexOf('/'))).putValue(simpleKey(key), e.getNewValue());
    });

    tableView.setRowFactory(param -> {
      final TreeTableRow<Entry> row = new TreeTableRow<>();
      // Clicking on an empty row should clear the selection.
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        if (row.getTreeItem() == null) {
          tableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });

    tableView.setFilter(metadataFilter);

    searchField.textProperty().addListener((__, oldText, newText) -> {
      if (newText.isEmpty()) {
        tableView.setFilter(metadataFilter);
      } else {
        String lower = newText.toLowerCase();
        Predicate<Entry> filter = metadataFilter.and(data -> data.getKey().toLowerCase().contains(lower)
            || data.getDisplayString().toLowerCase().contains(lower)
            || data.getType().toLowerCase().contains(lower));
        tableView.setFilter(filter);
      }
    });

    tableView.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        TreeItem<Entry> selected = tableView.getSelectionModel().getSelectedItem();
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
      TreeItem<Entry> selected = tableView.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
      Entry entry = selected.getValue();
      String key = entry.getKey();
      ContextMenu cm = new ContextMenu();

      if (entry.getValue() == null) {
        // Add the 'add x' items
        MenuItem string = new MenuItem("Add string");
        string.setOnAction(a -> {
          new AddStringDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putString(k, data.getValue());
          });
        });

        MenuItem number = new MenuItem("Add number");
        number.setOnAction(a -> {
          new AddNumberDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putDouble(k, data.getValue().doubleValue());
          });
        });

        MenuItem bool = new MenuItem("Add boolean");
        bool.setOnAction(a -> {
          new AddBooleanDialog().showAndWait().ifPresent(data -> {
            String k = concat(key, data.getKey());
            NetworkTablesJNI.putBoolean(k, data.getValue());
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
                   .forEach(i -> NetworkTableUtils.delete(i.getValue().getKey()));
        });

        cm.getItems().addAll(setPersistent, delete);
      } else {
        // Remove the separator
        cm.getItems().remove(cm.getItems().size() - 1);
      }

      tableView.setContextMenu(cm);
      cm.show(tableView, e.getScreenX(), e.getScreenY());
    });

    showMetadata(Prefs.isShowMetaData());
    Prefs.showMetaDataProperty().addListener((__, hide, show) -> {
      showMetadata(show);
      // dirty hack to refresh the view, otherwise the tree won't render correctly
      refreshWindow();
    });
  }

  public void showMetadata(boolean doShow) {
    showMetadata = doShow;
    tableView.updateItemsFromFilter();
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
