package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Doubles;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A dialog for adding or editing arrays of numbers in a network table entry.
 */
public class AddNumberArrayDialog extends AddEntryArrayDialog<Double, double[]> {

  public AddNumberArrayDialog() {
    super("Number Array");
  }

  @Override
  public void setInitial(double[] initialValues) {
    list.getItems().clear();
    for (double value : initialValues) {
      list.getItems().add(value);
    }
  }

  @Override
  protected Double getDefaultItem() {
    return 0.0;
  }

  @Override
  protected Callback<ListView<Double>, ListCell<Double>> getCellFactory() {
    return __ -> new TextFieldListCell<>(DoubleToStringConverter.INSTANCE);
  }

  @Override
  protected double[] getData() {
    return Doubles.toArray(list.getItems());
  }

  private static final class DoubleToStringConverter extends StringConverter<Double> {

    static final StringConverter<Double> INSTANCE = new DoubleToStringConverter();

    @Override
    public String toString(Double object) {
      if (object == object.intValue()) {
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
