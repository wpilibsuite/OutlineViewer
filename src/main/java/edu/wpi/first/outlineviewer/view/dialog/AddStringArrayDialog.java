package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Dialog for adding string arrays to network tables.
 */
public class AddStringArrayDialog extends AddEntryArrayDialog<String, String[]> {

  public AddStringArrayDialog() {
    super("String Array");
  }

  @Override
  protected String getDefaultItem() {
    return "change me!";
  }

  @Override
  protected Callback<ListView<String>, ListCell<String>> getCellFactory() {
    return __ -> new TextFieldListCell<>(StringToStringConverter.INSTANCE);
  }

  @Override
  protected String[] getData() {
    return list.getItems().toArray(new String[list.getItems().size()]);
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  public void setInitial(String[] initialValues) {
    list.getItems().clear();
    for (String value : initialValues) {
      list.getItems().add(value);
    }
  }

  private static final class StringToStringConverter extends StringConverter<String> {

    static final StringConverter<String> INSTANCE = new StringToStringConverter();

    @Override
    public String toString(String string) {
      return string;
    }

    @Override
    public String fromString(String string) {
      return string;
    }

  }

}
