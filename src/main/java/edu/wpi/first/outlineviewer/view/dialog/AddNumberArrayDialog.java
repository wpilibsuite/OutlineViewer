package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A dialog for adding or editing arrays of numbers in a network table entry.
 */
public class AddNumberArrayDialog extends AddEntryArrayDialog<Number, Number[]> {

  public AddNumberArrayDialog() {
    super("Number Array");
  }

  @Override
  public void setInitial(Number[] initialValues) {
    list.getItems().clear();
    for (Number value : initialValues) {
      list.getItems().add(value);
    }
  }

  @Override
  protected Double getDefaultItem() {
    return 0.0;
  }

  @Override
  protected Callback<ListView<Number>, ListCell<Number>> getCellFactory() {
    return __ -> new TextFieldListCell<>(DoubleToStringConverter.INSTANCE);
  }

  @Override
  protected Number[] getData() {
    return list.getItems().toArray(new Number[0]);
  }

  private static final class DoubleToStringConverter extends StringConverter<Number> {

    static final StringConverter<Number> INSTANCE = new DoubleToStringConverter();

    @Override
    public String toString(Number object) {
      if (object.doubleValue() == object.intValue()) {
        return String.valueOf(object.intValue());
      } else {
        return String.valueOf(object.doubleValue());
      }
    }

    @Override
    public Double fromString(String string) {
      return Double.parseDouble(string);
    }

  }

}
