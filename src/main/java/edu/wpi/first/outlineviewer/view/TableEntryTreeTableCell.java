package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.view.dialog.AddBooleanArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddBytesDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddEntryDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddNumberArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddStringArrayDialog;
import edu.wpi.first.outlineviewer.model.Entry;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;

import static edu.wpi.first.outlineviewer.NetworkTableUtils.simpleKey;

/**
 * TreeTableCell implementation that uses different editors based on the type of data in the cell.
 */
public class TableEntryTreeTableCell extends TreeTableCell<Entry, Object> {

  private Control editor;
  private Node graphic;
  private String text;
  private boolean canEdit;
  private AddEntryDialog<?> arrayEditor;

  public TableEntryTreeTableCell() {
    setEditable(true);
  }

  @Override
  protected void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty || getTreeTableRow().getTreeItem() == null) {
      setText("");
      setGraphic(null);
      return;
    }
    Entry entry = getTreeTableRow()
        .getTreeItem()
        .getValue();
    setGraphic(null);
    setText(null);

    canEdit = true; // assume it's editable; if it's not, we'll set it later
    arrayEditor = null; // will get set later if item is an array
    if (item instanceof Boolean) {
      CheckBox checkBox = new CheckBox();
      checkBox.setSelected((Boolean) item);
      editor = checkBox;
      setGraphic(checkBox);
      checkBox.setOnAction(event -> {
        if (!isEditing()) {
          getTreeTableRow().getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());
        }
        commitEdit(checkBox.isSelected());
      });
    } else if (item instanceof String) {
      TextField field = new TextField((String) item);
      field.setOnAction(e -> commitEdit(field.getText()));
      editor = field;
    } else if (item instanceof Number) {
      TextField field = new TextField(entry.getDisplayString());
      field.setOnAction(e -> {
        try {
          commitEdit(Double.parseDouble(field.getText()));
        } catch (NumberFormatException ignore) {
          field.setText(item.toString());
          cancelEdit();
        }
      });
      editor = field;
    } else {
      // check for arrays
      if (item instanceof String[]) {
        AddStringArrayDialog dialog = new AddStringArrayDialog();
        dialog.setInitial((String[]) item);
        arrayEditor = dialog;
        arrayEditor.setKey(entry.getKey());
        arrayEditor.setTitle(String.format("Edit '%s'", simpleKey(entry.getKey())));
      } else if (item instanceof double[]) {
        AddNumberArrayDialog dialog = new AddNumberArrayDialog();
        dialog.setInitial((double[]) item);
        arrayEditor = dialog;
        arrayEditor.setKey(entry.getKey());
        arrayEditor.setTitle(String.format("Edit '%s'", simpleKey(entry.getKey())));
      } else if (item instanceof boolean[]) {
        AddBooleanArrayDialog dialog = new AddBooleanArrayDialog();
        dialog.setInitial((boolean[]) item);
        arrayEditor = dialog;
        arrayEditor.setKey(entry.getKey());
        arrayEditor.setTitle(String.format("Edit '%s'", simpleKey(entry.getKey())));
      } else if (item instanceof byte[]) {
        AddBytesDialog dialog = new AddBytesDialog();
        dialog.setInitial((byte[]) item);
        arrayEditor = dialog;
        arrayEditor.setKey(entry.getKey());
        arrayEditor.setTitle(String.format("Edit '%s'", simpleKey(entry.getKey())));
      } else {
        // not editable
        canEdit = false;
      }
    }
    setText(entry.getDisplayString());
  }

  @Override
  public void startEdit() {
    if (!canEdit) {
      return;
    }
    super.startEdit();
    if (arrayEditor == null) {
      graphic = getGraphic();
      text = getText();
      setGraphic(editor);
      if (!(editor instanceof CheckBox)) {
        setText(null);
      }
    } else {
      arrayEditor.setDisableKey(true);
      arrayEditor.setOnCloseRequest(e -> {
        Entry<?> result = arrayEditor.getResult();
        if (result == null) {
          cancelEdit();
        } else {
          commitEdit(result.getValue());
        }
      });
      arrayEditor.show();
      arrayEditor.getDialogPane().getScene().getWindow().requestFocus();
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    if (arrayEditor == null) {
      setGraphic(graphic);
      setText(text);
    } else {
      arrayEditor.setResult(null);
      arrayEditor.close();
    }
  }

  @Override
  public void commitEdit(Object newValue) {
    super.commitEdit(newValue);
    setGraphic(graphic);
    setText(text);
  }

}
