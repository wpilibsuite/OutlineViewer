package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.outlineviewer.NetworkTableUtils;
import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.model.TreeTableEntry;
import edu.wpi.first.outlineviewer.view.TreeEntryTreeTableCell;
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
import edu.wpi.first.outlineviewer.model.TreeEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Controller for the main window.
 */
public class MainWindowController {

  @FXML
  private Pane root;

  @FXML
  private NetworkTableTree tableView;
  @FXML
  private TreeItem<TreeTableEntry> ntRoot;

  @FXML
  private TreeTableColumn<TreeRow, String> keyColumn;
  @FXML
  private TreeTableColumn<TreeRow, Object> valueColumn;
  @FXML
  private TreeTableColumn<TreeRow, String> typeColumn;

  @FXML
  @SuppressWarnings("PMD.AccessorMethodGeneration")
  private void initialize() {
    ntRoot.setValue(new TreeTableEntry(NetworkTableUtils.getNetworkTableInstance().getTable("")));

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
    valueColumn.setCellFactory(param -> new TreeEntryTreeTableCell<>());

    valueColumn.setOnEditCommit(event -> {
      TreeRow entry = event.getRowValue().getValue();

      // Users can only modify the value of TreeEntries
      if (entry instanceof TreeEntry) {
        TreeEntry treeEntry = (TreeEntry) entry;
        NetworkTableValue newValue;
        switch (treeEntry.getNetworkTableEntry().getType()) {
          case kDouble:
            newValue = NetworkTableValue.makeDouble((Double) event.getNewValue());
            break;
          case kString:
            newValue = NetworkTableValue.makeString((String) event.getNewValue());
            break;
          case kBoolean:
            newValue = NetworkTableValue.makeBoolean((Boolean) event.getNewValue());
            break;
          default:
            newValue = NetworkTableValue.makeString(event.getNewValue().toString());
            break;
        }
        treeEntry.getNetworkTableEntry().setValue(newValue);
      }
    });

    tableView.setRowFactory(param -> {
      final TreeTableRow<TreeRow> row = new TreeTableRow<>();
      // Clicking on an empty row should clear the selection.
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        if (row.getTreeItem() == null) {
          tableView.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    });

    tableView.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        TreeItem<TreeRow> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
          return;
        }

        tableView.edit(tableView.getRow(selected), valueColumn);
      }
    });

    tableView.setOnContextMenuRequested(contextMenuEvent -> {
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

      TreeItem<TreeRow> selected = tableView.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }

      TreeRow entry = selected.getValue();
      ContextMenu cm = new ContextMenu();

      // If the row is a table, add the 'add x' items
      if (entry instanceof TreeTableEntry) {
        cm.getItems().addAll(createTableMenuItems((TreeTableEntry) entry));
        cm.getItems().add(new SeparatorMenuItem());
      } else if (entry instanceof TreeEntry) {
        NetworkTableEntry networkTableEntry = ((TreeEntry) entry).getNetworkTableEntry();

        MenuItem setPersistent = new MenuItem(
            String.format("Set %s", networkTableEntry.isPersistent() ? "transient" : "persistent"));
        setPersistent.setOnAction(event -> {
          if (networkTableEntry.isPersistent()) {
            networkTableEntry.clearPersistent();
          } else {
            networkTableEntry.setPersistent();
          }
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> deleteSelectedEntries());

        cm.getItems().addAll(setPersistent, delete);
      }

      tableView.setContextMenu(cm);
      cm.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    });
  }

  /**
   * Creates all the menu items for a context menu for the given table entry.
   *
   * @param treeTableEntry the entry for the subtable to create the menu items for
   */
  private List<MenuItem> createTableMenuItems(TreeTableEntry treeTableEntry) {
    MenuItem string = createContextMenuItem("Add string",
        treeTableEntry.getKey(),
        new AddStringDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setString(value));

    MenuItem number = createContextMenuItem("Add number",
        treeTableEntry.getKey(),
        new AddNumberDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setNumber(value));

    MenuItem bool = createContextMenuItem("Add boolean",
        treeTableEntry.getKey(),
        new AddBooleanDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setBoolean(value));

    MenuItem stringArray = createContextMenuItem("Add string array",
        treeTableEntry.getKey(),
        new AddStringArrayDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setStringArray(value));

    MenuItem numberArray = createContextMenuItem("Add number array",
        treeTableEntry.getKey(),
        new AddNumberArrayDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setNumberArray(value));

    MenuItem boolArray = createContextMenuItem("Add boolean array",
        treeTableEntry.getKey(),
        new AddBooleanArrayDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setBooleanArray(value));

    MenuItem raw = createContextMenuItem("Add raw bytes",
        treeTableEntry.getKey(),
        new AddBytesDialog(),
        (key, value) -> treeTableEntry.getNetworkTable().getEntry(key).setRaw(value));

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
             .map(TreeRow::getKey)
             .forEach(NetworkTableUtils::delete);
  }

  @FXML
  private void close() {
    System.exit(0);
  }

  @FXML
  private void showPreferences() throws IOException {
    PreferencesDialog dialog = new PreferencesDialog(ButtonType.CANCEL, ButtonType.OK);
    if (dialog.showAndWait().orElse(false)) {
      dialog.getController().save();
    }
  }

  private static <T> MenuItem createContextMenuItem(String text, String key,
                                                    AddEntryDialog<T> dialog,
                                                    BiConsumer<String, T> resultConsumer) {
    MenuItem menuItem = new MenuItem(text);
    menuItem.setOnAction(event -> dialog.showAndWait().ifPresent(result
        -> resultConsumer.accept(NetworkTableUtils
        .concat(key, result.getKey()), result.getValue())));
    return menuItem;
  }

}
