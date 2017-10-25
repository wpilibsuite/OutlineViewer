package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.model.IndexedStringConverter;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Dialog for adding string arrays to network tables.
 */
public class AddStringArrayDialog extends AddEntryArrayDialog<Pair<Integer, String>, String[]> {

  public AddStringArrayDialog() {
    super("String Array");
  }

  @Override
  protected Pair<Integer, String> getDefaultItem() {
    return new Pair<>(0, "String Array");
  }

  @Override
  protected Callback<ListView<Pair<Integer, String>>, ListCell<Pair<Integer, String>>> getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(StringToStringConverter.INSTANCE);
  }

  @Override
  protected String[] getData() {
    return list.getItems()
        .stream()
        .map(Pair::getValue)
        .collect(Collectors.toList())
        .toArray(new String[list.getItems().size()]);
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  public void setInitial(String[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (String value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  private static final class StringToStringConverter extends IndexedStringConverter<String> {

    static final IndexedStringConverter<String> INSTANCE = new StringToStringConverter();

    @Override
    public String toString(Pair<Integer, String> object) {
      if (object == null) {
        return null;
      }

      return object.getValue();
    }

    @Override
    public Pair<Integer, String> fromString(String string) {
      return fromString(0, string);
    }

    @Override
    public Pair<Integer, String> fromString(Integer index, String string) {
      return new Pair<>(index, string);
    }
  }

}
