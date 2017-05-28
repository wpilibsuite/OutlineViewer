package edu.wpi.first.tableviewer;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;

/**
 * TreeTableCell implementation that uses different editors based on the type of data in the cell.
 */
class TableEntryDataTreeTableCell extends TreeTableCell<TableEntryData, Object> {

  private Class<?> type = null;
  private Control editor = null;
  private Node graphic = null;
  private String text = null;

  public TableEntryDataTreeTableCell() {
    setEditable(true);
  }

  @Override
  protected void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setText("");
      setGraphic(null);
      return;
    }
    type = item.getClass();
    setGraphic(null);
    setText(null);
    if (item instanceof Boolean) {
      CheckBox checkBox = new CheckBox();
      checkBox.setSelected((Boolean) item);
      editor = checkBox;
      setGraphic(checkBox);
      checkBox.setOnAction(event -> {
        if (!isEditing())
          getTreeTableRow().getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());
        commitEdit(checkBox.isSelected());
      });
    } else if (item instanceof String) {
      TextField field = new TextField((String) item);
      field.setOnAction(e -> commitEdit(field.getText()));
      editor = field;
    } else if (item instanceof Number) {
      TextField field = new TextField(item.toString());
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
      throw new AssertionError("Can't handle " + item.getClass());
    }
    setText(item.toString());
  }

  @Override
  public void startEdit() {
    super.startEdit();
    graphic = getGraphic();
    text = getText();
    setGraphic(editor);
    if (!(editor instanceof CheckBox)) {
      setText(null);
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setGraphic(graphic);
    setText(text);
  }

  @Override
  public void commitEdit(Object newValue) {
    super.commitEdit(newValue);
    setGraphic(graphic);
    setText(text);
  }

}
