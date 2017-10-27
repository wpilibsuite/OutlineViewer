package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.IndexedStringConverter;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import edu.wpi.first.outlineviewer.view.IndexedValue;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Dialog for adding string arrays to network tables.
 */
public class AddStringArrayDialog extends AddEntryArrayDialog<IndexedValue<String>, String[]> {

  public AddStringArrayDialog() {
    super("String Array");
  }

  @Override
  protected IndexedValue<String> getDefaultItem() {
    return new IndexedValue<>(list.getItems().size() + 1, "String Array");
  }

  @Override
  protected Callback<ListView<IndexedValue<String>>, ListCell<IndexedValue<String>>>
      getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(StringToStringConverter.INSTANCE);
  }

  @Override
  protected String[] getData() {
    return list.getItems()
        .stream()
        .map(IndexedValue::getValue)
        .collect(Collectors.toList())
        .toArray(new String[list.getItems().size()]);
  }

  @Override
  @SuppressWarnings({"PMD.UseVarargs", "PMD.AvoidInstantiatingObjectsInLoops"})
  public void setInitial(String[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (String value : initialValues) {
      list.getItems().add(new IndexedValue<>(index++, value));
    }
  }

  private static final class StringToStringConverter extends IndexedStringConverter<String> {

    static final IndexedStringConverter<String> INSTANCE = new StringToStringConverter();

    @Override
    public String toString(IndexedValue<String> object) {
      if (object == null) {
        return null;
      }

      return object.getValue();
    }

    @Override
    public IndexedValue<String> fromString(String string) {
      return fromString(0, string);
    }

    @Override
    public IndexedValue<String> fromString(Integer index, String string) {
      return new IndexedValue<>(index, string);
    }
  }

}
