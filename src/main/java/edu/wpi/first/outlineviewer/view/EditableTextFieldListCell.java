package edu.wpi.first.outlineviewer.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Cell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class EditableTextFieldListCell<T> extends DraggableCell<IndexedValue<T>> {
  private TextField textField;
  private final ObjectProperty<IndexedStringConverter<T>> converter
      = new SimpleObjectProperty<>(this, "converter");

  @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
  public EditableTextFieldListCell(IndexedStringConverter<T> converter) {
    this.getStyleClass().add("text-field-list-cell");
    setConverter(converter);

    //Java does this in startEdit for some reason. We need to add a change listener to the focused
    //property here so we need to make the text field early
    textField = createTextField(this, getConverter());
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        commitEdit(converter.fromString(getIndex(), textField.getText()));
      }
    });
  }

  public final ObjectProperty<IndexedStringConverter<T>> converterProperty() {
    return converter;
  }

  public final void setConverter(IndexedStringConverter<T> value) {
    converterProperty().set(value);
  }

  public final IndexedStringConverter<T> getConverter() {
    return converterProperty().get();
  }

  @Override
  public void startEdit() {
    if (!isEditable() || !getListView().isEditable()) {
      return;
    }
    super.startEdit();

    if (isEditing()) {
      if (textField == null) {
        textField = createTextField(this, getConverter());
      }

      if (textField != null) {
        textField.setText(getItemText(this, getConverter()));
      }
      setText(null);

      setGraphic(textField);

      if (textField != null) {
        textField.selectAll();
        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
      }
    }
  }

  public TextField getTextField() {
    return textField;
  }

  @Override
  public void cancelEdit() {
    setText(textField.getText());
    super.cancelEdit();
    setText(getItemText(this, getConverter()));
    setGraphic(null);
  }

  @Override
  public void commitEdit(IndexedValue<T> newValue) {
    if (!isEditing() && !newValue.equals(getItem())) {
      ListView<IndexedValue<T>> list = getListView();

      if (list != null) {
        //Pulled from the super method
        list.fireEvent(new ListView.EditEvent<>(
            list,
            ListView.editCommitEvent(),
            newValue,
            list.getEditingIndex()));
      }
    }

    super.commitEdit(newValue);
  }

  @Override
  public void updateItem(IndexedValue<T> item, boolean empty) {
    super.updateItem(item, empty);
    final IndexedStringConverter<T> converter1 = getConverter();
    if (isEmpty()) {
      setText(null);
      setGraphic(null);
    } else {
      if (isEditing()) {
        if (textField != null) {
          textField.setText(getItemText(this, converter1));
        }
        setText(null);

        setGraphic(textField);
      } else {
        setText(getItemText(this, converter1));
        setGraphic(null);
      }
    }
  }

  private String getItemText(Cell<IndexedValue<T>> cell, IndexedStringConverter<T> converter) {
    if (converter == null) {
      if (cell.getItem() == null) {
        return "";
      } else {
        return cell.getItem().getValue().toString();
      }
    } else {
      if (cell.getItem() == null) {
        return "";
      } else {
        return converter.toString(new IndexedValue<>(getIndex(), cell.getItem().getValue()));
      }
    }
  }

  private TextField createTextField(final Cell<IndexedValue<T>> cell,
                                    final IndexedStringConverter<T> converter) {
    final TextField textField = new TextField(getItemText(cell, converter));

    // Use onAction here rather than onKeyReleased (with check for Enter),
    // as otherwise we encounter RT-34685
    textField.setOnAction(event -> {
      if (converter == null) {
        throw new IllegalStateException(
            "Attempting to convert text input into Object, but provided "
                + "StringConverter is null. Be sure to set a StringConverter "
                + "in your cell factory.");
      }
      cell.commitEdit(converter.fromString(cell.getItem().getIndex(), textField.getText()));
      event.consume();
    });
    textField.setOnKeyReleased(t -> {
      if (t.getCode() == KeyCode.ESCAPE) {
        cell.cancelEdit();
        t.consume();
      }
    });
    return textField;
  }

}