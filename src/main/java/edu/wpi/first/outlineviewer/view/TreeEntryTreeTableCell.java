package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.model.TreeRow;
import edu.wpi.first.outlineviewer.view.dialog.AddBooleanArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddBytesDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddEntryDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddNumberArrayDialog;
import edu.wpi.first.outlineviewer.view.dialog.AddStringArrayDialog;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;

import static edu.wpi.first.outlineviewer.NetworkTableUtilities.simpleKey;

/**
 * TreeTableCell implementation that uses different editors based on the type of data in the cell.
 */
public class TreeEntryTreeTableCell<T extends TreeRow> extends TreeTableCell<T, Object> {

  private Control editor;
  private Node graphic;
  private String text;
  private AddEntryDialog<?> arrayEditor;

  public TreeEntryTreeTableCell() {
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
    TreeRow entry = getTreeTableRow()
        .getTreeItem()
        .getValue();
    setGraphic(null);
    setText(null);

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
      TextField field = new TextField(entry.getValue().toString());
      field.setOnAction(e -> {
        try {
          commitEdit(Double.parseDouble(field.getText()));
        } catch (NumberFormatException ignore) {
          field.setText(item.toString());
          cancelEdit();
        }
      });
      editor = field;
    } else if (item instanceof String[]) {
      AddStringArrayDialog dialog = new AddStringArrayDialog();
      dialog.setInitial((String[]) item);
      arrayEditor = dialog;
      arrayEditor.setKey(entry.getKey());
      arrayEditor.setTitle(String.format("Edit '%s'", simpleKey(entry.getKey())));
    } else if (item instanceof Number[]) {
      AddNumberArrayDialog dialog = new AddNumberArrayDialog();
      dialog.setInitial((Number[]) item);
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
    }

    setText(entry.getValue().toString());
  }

  @Override
  public void startEdit() {
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
      arrayEditor.onShownProperty().setValue(event
          -> arrayEditor.getDialogPane().getScene().getWindow().requestFocus());
      if (arrayEditor.showAndWait().isPresent()) {
        commitEdit(arrayEditor.getResult().getValue());
      } else {
        cancelEdit();
      }
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
