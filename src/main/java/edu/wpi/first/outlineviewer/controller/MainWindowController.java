package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.Preferences;
import edu.wpi.first.outlineviewer.model.TableEntry;
import edu.wpi.first.outlineviewer.view.TableEntryTreeTableCell;
import edu.wpi.first.outlineviewer.view.NetworkTableTree;
import edu.wpi.first.outlineviewer.view.dialog.AddBooleanArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddBooleanDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddBytesDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddEntryDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddNumberArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddNumberDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddStringArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddStringDialog;
import edu.wpi.first.outlineviewer.view.dialog.PreferencesDialog;
import edu.wpi.first.outlineviewer.model.TableValueEntry;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.networktables.NetworkTableValue;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
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

/**
 * Controller for the main window.
 */
public class MainWindowController {

  @FXML
  private Pane root;

  @FXML
  private NetworkTableTree tableView;
  @FXML
  private TreeItem<TableEntry> ntRoot;

  @FXML
  private TreeTableColumn<TableEntry, String> keyColumn;
  @FXML
  private TreeTableColumn<TableEntry, Object> valueColumn;
  @FXML
  private TreeTableColumn<TableEntry, String> typeColumn;

  @FXML
  private ToolBar searchBar;
  @FXML
  private TextField searchField;

  private final Predicate<TableEntry> metadataFilter
      = x -> Preferences.isShowMetaData() || !x.isMetadata();

  private NetworkTable networkTable;

  @FXML
  @SuppressWarnings("PMD.AccessorMethodGeneration")
  private void initialize() {
    networkTable = tableView.getNetworkTable();

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

    ntRoot.setValue(new TableEntry(""));

    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    tableView.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.DELETE) {
        deleteSelectedEntries();
      }
    });

    tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

    keyColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        NetworkTableUtils.simpleKey(param.getValue().getValue().getKey())));
    valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
    typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

    keyColumn.setCellFactory(param
        -> new TextFieldTreeTableCell<TableEntry, String>(new DefaultStringConverter()) {
          @Override
          public void startEdit() {
            TableEntry existing = getTreeTableRow().getItem();
            if (!(existing instanceof TableValueEntry)) {
              // Can't edit a table name
              return;
            }
            super.startEdit();
          }

          @Override
          public void commitEdit(String simpleKey) {
            TableEntry existing = getTreeTableRow().getItem();
            String existingKey = existing.getKey();
            String table = existingKey.substring(0,
                existingKey.lastIndexOf(NetworkTableUtils.simpleKey(existingKey)));
            String newKey = NetworkTableUtils.concat(table, simpleKey);
            if (networkTable.containsKey(newKey)) {
              // That key already exists
              cancelEdit();
              return;
            }
            super.commitEdit(simpleKey);

            String oldKey = existing.getKey();
            NetworkTableValue replacement
                = new NetworkTableValue(networkTable.getValue(oldKey).getType(),
                existing.getValue());

            final int flags = networkTable.getFlags(oldKey);
            networkTable.delete(oldKey);
            networkTable.putValue(newKey, replacement);
            networkTable.setFlags(newKey, flags);
          }
        });
    valueColumn.setCellFactory(param -> new TableEntryTreeTableCell<>());

    valueColumn.setOnEditCommit(e -> {
      TableEntry entry = e.getRowValue().getValue();
      String key = entry.getKey(); // entry keys are guaranteed to be normalized
      networkTable.putValue(key,
          new NetworkTableValue(networkTable.getValue(key).getType(), e.getNewValue()));
    });

    tableView.setRowFactory(param -> {
      final TreeTableRow<TableEntry> row = new TreeTableRow<>();
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
        Predicate<TableEntry> filter = metadataFilter.and(data
            -> data.getKey().toLowerCase(Locale.getDefault()).contains(lower)
              || data.getValue().toString().toLowerCase(Locale.getDefault()).contains(lower)
              || data.getType().toLowerCase(Locale.getDefault()).contains(lower));
        tableView.setFilter(filter);
      }
    });

    tableView.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        TreeItem<TableEntry> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
          return;
        }

        tableView.edit(tableView.getRow(selected), valueColumn);
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
      TreeItem<TableEntry> selected = tableView.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
      TableEntry entry = selected.getValue();
      String key = entry.getKey();
      ContextMenu cm = new ContextMenu();

      if (!(entry instanceof TableValueEntry)) {
        // It's a table, add the 'add x' items
        cm.getItems().addAll(createTableMenuItems(entry));
        cm.getItems().add(new SeparatorMenuItem());
      }

      if (key.isEmpty() && entry.getValue() == null) {
        // Remove the separator
        cm.getItems().remove(cm.getItems().size() - 1);
      } else {
        MenuItem setPersistent = new MenuItem(
            String.format("Set %s", networkTable.isPersistent(key) ? "transient" : "persistent"));
        setPersistent.setOnAction(__ -> {
          if (networkTable.isPersistent(key)) {
            networkTable.clearPersistent(key);
          } else {
            networkTable.setPersistent(key);
          }
        });

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
  private List<MenuItem> createTableMenuItems(TableEntry tableEntry) {
    final String key = tableEntry.getKey();

    MenuItem string = createContextMenuItem("Add string",
        new AddStringDialog(), NetworkTableType.kString, key);

    MenuItem number = createContextMenuItem("Add number",
        new AddNumberDialog(), NetworkTableType.kDouble, key);

    MenuItem bool = createContextMenuItem("Add boolean",
        new AddBooleanDialog(), NetworkTableType.kBoolean, key);

    MenuItem boolArray = createContextMenuItem("Add boolean array",
        new AddBooleanArrayDialog(), NetworkTableType.kBooleanArray, key);

    MenuItem numberArray = createContextMenuItem("Add number array",
        new AddNumberArrayDialog(), NetworkTableType.kDoubleArray, key);

    MenuItem stringArray = createContextMenuItem("Add string array",
        new AddStringArrayDialog(), NetworkTableType.kStringArray, key);

    MenuItem raw = createContextMenuItem("Add raw bytes", new AddBytesDialog(),
        NetworkTableType.kRaw, key);

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
             .map(TableEntry::getKey)
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
  private void showPreferences() throws IOException {
    PreferencesDialog dialog = new PreferencesDialog(ButtonType.CANCEL, ButtonType.OK);
    if (dialog.showAndWait().orElse(false)) {
      dialog.getController().save();
    }
  }

  private MenuItem createContextMenuItem(String text, AddEntryDialog<?> dialog,
                                                NetworkTableType type, String key) {
    MenuItem menuItem = new MenuItem(text);
    menuItem.setOnAction(__ -> dialog.showAndWait().ifPresent(data
        -> networkTable.putValue(NetworkTableUtils.concat(key, data.getKey()),
        new NetworkTableValue(type, data.valueProperty().get()))));
    return menuItem;
  }

}
