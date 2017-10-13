package edu.wpi.first.outlineviewer.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class EditableTextFieldListCell<T> extends ListCell<T> {

  /**
   * Provides a {@link TextField} that allows editing of the cell content when the cell is
   * double-clicked, or when {@link ListView#edit(int)} is called. This method will only work on
   * {@link ListView} instances which are of type String.
   *
   * @return A {@link Callback} that can be inserted into the {@link ListView#cellFactoryProperty()
   * cell factory property} of a ListView, that enables textual editing of the content.
   */
  public static Callback<ListView<String>, ListCell<String>> forListView() {
    return forListView(new DefaultStringConverter());
  }

  /**
   * Provides a {@link TextField} that allows editing of the cell content when the cell is
   * double-clicked, or when {@link ListView#edit(int)} is called. This method will work on any
   * ListView instance, regardless of its generic type. However, to enable this, a {@link
   * StringConverter} must be provided that will convert the given String (from what the user typed
   * in) into an instance of type T. This item will then be passed along to the {@link
   * ListView#onEditCommitProperty()} callback.
   *
   * @param converter A {@link StringConverter} that can convert the given String (from what the
   * user typed in) into an instance of type T.
   * @return A {@link Callback} that can be inserted into the {@link ListView#cellFactoryProperty()
   * cell factory property} of a ListView, that enables textual editing of the content.
   */
  public static <T> Callback<ListView<T>, ListCell<T>> forListView(final StringConverter<T>
                                                                       converter) {
    return list -> new TextFieldListCell<>(converter);
  }

  private TextField textField;

  /**
   * Creates a default TextFieldListCell with a null converter. Without a {@link StringConverter}
   * specified, this cell will not be able to accept input from the TextField (as it will not know
   * how to convert this back to the domain object). It is therefore strongly encouraged to not use
   * this constructor unless you intend to set the converter separately.
   */
  public EditableTextFieldListCell() {
    this(null);
  }

  /**
   * Creates a TextFieldListCell that provides a {@link TextField} when put into editing mode that
   * allows editing of the cell content. This method will work on any ListView instance, regardless
   * of its generic type. However, to enable this, a {@link StringConverter} must be provided that
   * will convert the given String (from what the user typed in) into an instance of type T. This
   * item will then be passed along to the {@link ListView#onEditCommitProperty()} callback.
   *
   * @param converter A {@link StringConverter converter} that can convert the given String (from
   * what the user typed in) into an instance of type T.
   */
  public EditableTextFieldListCell(StringConverter<T> converter) {
    this.getStyleClass().add("text-field-list-cell");
    setConverter(converter);

    //Java does this in startEdit for some reason. We need to add a change listener to the focused
    //property here so we need to make the text field early
    textField = createTextField(this, getConverter());
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      System.out.println("Focus changed from " + oldValue + " to " + newValue);
      if (!newValue) {
        commitEdit(converter.fromString(textField.getText()));
      }
    });
  }

  private ObjectProperty<StringConverter<T>> converter
      = new SimpleObjectProperty<>(this, "converter");

  /**
   * The {@link StringConverter} property.
   */
  public final ObjectProperty<StringConverter<T>> converterProperty() {
    return converter;
  }

  /**
   * Sets the {@link StringConverter} to be used in this cell.
   */
  public final void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  /**
   * Returns the {@link StringConverter} used in this cell.
   */
  public final StringConverter<T> getConverter() {
    return converterProperty().get();
  }

  /**
   * {@inheritDoc}
   */
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

      startEdit(this, getConverter(), null, null, textField);
    }
  }

  public TextField getTextField() {
    return textField;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void cancelEdit() {
    setText(textField.getText());
    super.cancelEdit();
    cancelEdit(this, getConverter(), null);
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    updateItem(this, getConverter(), null, null, textField);
  }

  private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
    return converter == null ?
        cell.getItem() == null ? "" : cell.getItem().toString() :
        converter.toString(cell.getItem());
  }

  private static <T> void startEdit(final Cell<T> cell,
                                    final StringConverter<T> converter,
                                    final HBox hbox,
                                    final Node graphic,
                                    final TextField textField) {
    if (textField != null) {
      textField.setText(getItemText(cell, converter));
    }
    cell.setText(null);

    if (graphic != null) {
      hbox.getChildren().setAll(graphic, textField);
      cell.setGraphic(hbox);
    } else {
      cell.setGraphic(textField);
    }

    if (textField != null) {
      textField.selectAll();
      // requesting focus so that key input can immediately go into the
      // TextField (see RT-28132)
      textField.requestFocus();
    }
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

  private static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node
      graphic) {
    cell.setText(getItemText(cell, converter));
    cell.setGraphic(graphic);
  }

  private static <T> void updateItem(final Cell<T> cell,
                                     final StringConverter<T> converter,
                                     final HBox hbox,
                                     final Node graphic,
                                     final TextField textField) {
    if (cell.isEmpty()) {
      cell.setText(null);
      cell.setGraphic(null);
    } else {
      if (cell.isEditing()) {
        if (textField != null) {
          textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null) {
          hbox.getChildren().setAll(graphic, textField);
          cell.setGraphic(hbox);
        } else {
          cell.setGraphic(textField);
        }
      } else {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
      }
    }
  }
}