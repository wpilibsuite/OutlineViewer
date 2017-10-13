package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class EditableTextFieldListCell<T> extends ListCell<T> {
  private TextField textField;
  private final ObjectProperty<StringConverter<T>> converter
      = new SimpleObjectProperty<>(this, "converter");

  public static Callback<ListView<String>, ListCell<String>> forListView() {
    return forListView(new DefaultStringConverter());
  }

  public static <T> Callback<ListView<T>, ListCell<T>> forListView(final StringConverter<T>
                                                                       converter) {
    return list -> new TextFieldListCell<>(converter);
  }

  public EditableTextFieldListCell() {
    this(null);
  }

  @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
  public EditableTextFieldListCell(StringConverter<T> converter) {
    this.getStyleClass().add("text-field-list-cell");
    setConverter(converter);

    //Java does this in startEdit for some reason. We need to add a change listener to the focused
    //property here so we need to make the text field early
    textField = createTextField(this, getConverter());
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        commitEdit(converter.fromString(textField.getText()));
      }
    });
  }

  public final ObjectProperty<StringConverter<T>> converterProperty() {
    return converter;
  }

  public final void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  public final StringConverter<T> getConverter() {
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
  public void commitEdit(T newValue) {
    if (!isEditing() && !newValue.equals(getItem())) {
      ListView<T> list = getListView();

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
  public void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    final StringConverter<T> converter1 = getConverter();
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

  private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
    return converter == null
        ? cell.getItem() == null ? "" : cell.getItem().toString() :
        converter.toString(cell.getItem());
  }

  private static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T>
      converter) {
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
      cell.commitEdit(converter.fromString(textField.getText()));
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