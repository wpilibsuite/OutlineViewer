package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.IndexedStringConverter;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * A dialog for adding or editing arrays of numbers in a network table entry.
 */
public class AddNumberArrayDialog extends AddEntryArrayDialog<Pair<Integer, Number>, Number[]> {

  public AddNumberArrayDialog() {
    super("Number Array");
  }

  @Override
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void setInitial(Number[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (Number value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  @Override
  protected Pair<Integer, Number> getDefaultItem() {
    return new Pair<>(list.getItems().size() + 1, 0.0);
  }

  @Override
  protected Callback<ListView<Pair<Integer, Number>>, ListCell<Pair<Integer, Number>>>
      getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(DoubleToStringConverter.INSTANCE);
  }

  @Override
  protected Number[] getData() {
    return list.getItems()
        .stream()
        .map(Pair::getValue)
        .collect(Collectors.toList())
        .toArray(new Number[0]);
  }

  private static final class DoubleToStringConverter extends IndexedStringConverter<Number> {

    static final IndexedStringConverter<Number> INSTANCE = new DoubleToStringConverter();

    @Override
    public String toString(Pair<Integer, Number> object) {
      if (object == null) {
        return null;
      }

      Number val = object.getValue();
      if (val.doubleValue() == val.intValue()) {
        return String.valueOf(val.intValue());
      } else {
        return String.valueOf(val.doubleValue());
      }
    }

    @Override
    public Pair<Integer, Number> fromString(String string) {
      return fromString(0, string);
    }

    @Override
    public Pair<Integer, Number> fromString(Integer index, String string) {
      return new Pair<>(index, Double.parseDouble(string));
    }

  }

}
