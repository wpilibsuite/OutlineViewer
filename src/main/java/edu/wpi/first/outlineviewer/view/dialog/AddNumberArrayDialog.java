package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.IndexedStringConverter;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * A dialog for adding or editing arrays of numbers in a network table entry.
 */
public class AddNumberArrayDialog extends AddEntryArrayDialog<Pair<Integer, Double>, double[]> {

  public AddNumberArrayDialog() {
    super("Number Array");
  }

  @Override
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void setInitial(double[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (double value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  @Override
  protected Pair<Integer, Double> getDefaultItem() {
    return new Pair<>(list.getItems().size() + 1, 0.0);
  }

  @Override
  protected Callback<ListView<Pair<Integer, Double>>, ListCell<Pair<Integer, Double>>>
      getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(DoubleToStringConverter.INSTANCE);
  }

  @Override
  protected double[] getData() {
    return list.getItems()
        .stream()
        .map(Pair::getValue)
        .mapToDouble(Double::doubleValue)
        .toArray();
  }

  private static final class DoubleToStringConverter extends IndexedStringConverter<Double> {

    static final IndexedStringConverter<Double> INSTANCE = new DoubleToStringConverter();

    @Override
    public String toString(Pair<Integer, Double> object) {
      if (object == null) {
        return null;
      }

      Double val = object.getValue();
      if (val == val.intValue()) {
        return String.valueOf(val.intValue());
      } else {
        return String.valueOf(val.doubleValue());
      }
    }

    @Override
    public Pair<Integer, Double> fromString(String string) {
      return fromString(0, string);
    }

    @Override
    public Pair<Integer, Double> fromString(Integer index, String string) {
      return new Pair<>(index, Double.parseDouble(string));
    }

  }

}
