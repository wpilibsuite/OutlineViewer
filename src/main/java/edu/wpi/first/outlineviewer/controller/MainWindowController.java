package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.Preferences;
import edu.wpi.first.outlineviewer.view.TableEntryTreeTableCell;
import edu.wpi.first.outlineviewer.view.NetworkTableTree;
import edu.wpi.first.outlineviewer.controller.dialog.AddBooleanArrayDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddBooleanDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddBytesDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddNumberArrayDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddNumberDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddStringArrayDialog;
import edu.wpi.first.outlineviewer.controller.dialog.AddStringDialog;
import edu.wpi.first.outlineviewer.controller.dialog.Dialogs;
import edu.wpi.first.outlineviewer.controller.dialog.PreferencesDialog;
import edu.wpi.first.outlineviewer.model.Entry;
import edu.wpi.first.outlineviewer.model.TableEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static edu.wpi.first.outlineviewer.NetworkTableUtils.concat;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.isPersistent;
import static edu.wpi.first.outlineviewer.NetworkTableUtils.simpleKey;

/**
 * Controller for the main window.
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

  private final Predicate<Entry> metadataFilter
      = x -> Preferences.isShowMetaData() || !x.isMetadata();

  @FXML
  private void initialize() {
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
        deleteSelectedEntries();
      }
    });

    tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    NetworkTablesJNI.addEntryListener(
        "",
        (uid, key, value, flags) -> tableView.updateFromNetworkTables(key, value, flags),
        0xFF);


    keyColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        simpleKey(param.getValue().getValue().getKey())));
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
    typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

    keyColumn.setCellFactory(param
        -> new TextFieldTreeTableCell<Entry, String>(new DefaultStringConverter()) {
          @Override
          public void startEdit() {
            Entry existing = getTreeTableRow().getItem();
            if (existing instanceof TableEntry) {
              // Can't edit a table name
              return;
            }
            super.startEdit();
          }

          @Override
          public void commitEdit(String simpleKey) {
            Entry existing = getTreeTableRow().getItem();
            String existingKey = existing.getKey();
            String table
                = existingKey.substring(0, existingKey.lastIndexOf(simpleKey(existingKey)));
            String newKey = concat(table, simpleKey);
            if (NetworkTablesJNI.containsKey(newKey)) {
              // That key already exists
              cancelEdit();
              return;
            }
            super.commitEdit(simpleKey);
            String oldKey = existing.getKey();
            Entry replacement = Entry.entryFor(newKey, existing.getValue());
            final int flags = NetworkTablesJNI.getEntryFlags(oldKey);
            NetworkTablesJNI.deleteEntry(oldKey);
            NetworkTableUtils.put(newKey, replacement.getValue());
            NetworkTablesJNI.setEntryFlags(newKey, flags);
          }
        });
    valueColumn.setCellFactory(param -> new TableEntryTreeTableCell());

    valueColumn.setOnEditCommit(e -> {
      Entry entry = e.getRowValue().getValue();
      String key = entry.getKey(); // entry keys are guaranteed to be normalized
      NetworkTableUtils.put(key, e.getNewValue());
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
        String lower = newText.toLowerCase(Locale.getDefault());
        Predicate<Entry> filter = metadataFilter.and(data
            -> data.getKey().toLowerCase(Locale.getDefault()).contains(lower)
              || data.getDisplayString().toLowerCase(Locale.getDefault()).contains(lower)
              || data.getType().toLowerCase(Locale.getDefault()).contains(lower));
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
        // Close previous context menu
        tableView.getContextMenu().hide();
      }
      // The actions in the menu only affect one entry,
      // so we only select the entry that was clicked on.
      if (tableView.getSelectionModel().getSelectedItems().size() > 1) {
        tableView.getSelectionModel()
                 .clearAndSelect(tableView.getSelectionModel().getSelectedIndex());
      }
      TreeItem<Entry> selected = tableView.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
      Entry entry = selected.getValue();
      String key = entry.getKey();
      ContextMenu cm = new ContextMenu();

      if (entry instanceof TableEntry) {
        // It's a table, add the 'add x' items
        cm.getItems().addAll(createTableMenuItems(entry));
        cm.getItems().add(new SeparatorMenuItem());
      }

      if (key.isEmpty() && entry.getValue() == null) {
        // Remove the separator
        cm.getItems().remove(cm.getItems().size() - 1);
      } else {
        MenuItem setPersistent = new MenuItem(
            String.format("Set %s", isPersistent(key) ? "transient" : "persistent"));
        setPersistent.setOnAction(__ -> NetworkTableUtils.togglePersistent(key));

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(__ -> deleteSelectedEntries());

        cm.getItems().addAll(setPersistent, delete);
      }

      tableView.setContextMenu(cm);
      cm.show(tableView, e.getScreenX(), e.getScreenY());
    });
    Preferences.showMetaDataProperty().addListener(__ -> tableView.updateItemsFromFilter());
  }

  /**
   * Creates all the menu items for a context menu for the given table entry.
   *
   * @param tableEntry the entry for the subtable to create the menu items for
   */
  private List<MenuItem> createTableMenuItems(Entry<?> tableEntry) {
    final String key = tableEntry.getKey();

    MenuItem string = new MenuItem("Add string");
    string.setOnAction(a
        -> new AddStringDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putString(fullKey, data.getValue());
        }));

    MenuItem number = new MenuItem("Add number");
    number.setOnAction(a
        -> new AddNumberDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putDouble(fullKey, data.getValue().doubleValue());
        }));

    MenuItem bool = new MenuItem("Add boolean");
    bool.setOnAction(a
        -> new AddBooleanDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putBoolean(fullKey, data.getValue());
        }));

    MenuItem boolArray = new MenuItem("Add boolean array");
    boolArray.setOnAction(__
        -> new AddBooleanArrayDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putBooleanArray(fullKey, data.getValue());
        }));

    MenuItem numberArray = new MenuItem("Add number array");
    numberArray.setOnAction(__
        -> new AddNumberArrayDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putDoubleArray(fullKey, data.getValue());
        }));

    MenuItem stringArray = new MenuItem("Add string array");
    stringArray.setOnAction(__
        -> new AddStringArrayDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putStringArray(fullKey, data.getValue());
        }));

    MenuItem raw = new MenuItem("Add raw bytes");
    raw.setOnAction(__
        -> new AddBytesDialog().showAndWait().ifPresent(data -> {
          String fullKey = concat(key, data.getKey());
          NetworkTablesJNI.putRaw(fullKey, data.getValue());
        }));

    return Arrays.asList(string, number, bool,
                         new SeparatorMenuItem(),
                         stringArray, numberArray, boolArray,
                         new SeparatorMenuItem(),
                         raw);
  }

  /**
   * Deletes all selected entries.
   */
  private void deleteSelectedEntries() {
    tableView.getSelectionModel()
             .getSelectedItems()
             .stream()
             .map(TreeItem::getValue)
             .map(Entry::getKey)
             .forEach(NetworkTableUtils::delete);
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
          .filter(ButtonType.FINISH::equals)
          .ifPresent(__ -> dialog.getController().start());
  }

}
