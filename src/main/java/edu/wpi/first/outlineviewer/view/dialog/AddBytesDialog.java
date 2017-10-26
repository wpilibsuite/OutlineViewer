package edu.wpi.first.outlineviewer.view.dialog;

import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import edu.wpi.first.outlineviewer.view.IndexedStringConverter;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * A dialog for editing arrays of raw bytes. These are represented as integers because Java doesn't
 * support unsigned bytes.
 */
public class AddBytesDialog extends AddEntryArrayDialog<Pair<Integer, Byte>, Byte[]> {

  public AddBytesDialog() {
    super("Raw Bytes");
  }

  /**
   * Sets the initial values in the raw bytes array.
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void setInitial(Byte[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (Byte value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  /**
   * Sets the initial values in the raw bytes array.
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void setInitial(byte[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (byte value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  @Override
  protected Pair<Integer, Byte> getDefaultItem() {
    return new Pair<>(list.getItems().size() + 1, Byte.valueOf("0"));
  }

  @Override
  protected Callback<ListView<Pair<Integer, Byte>>, ListCell<Pair<Integer, Byte>>>
      getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(ByteToStringConverter.INSTANCE);
  }

  @Override
  protected Byte[] getData() {
    return list.getItems()
        .stream()
        .map(Pair::getValue)
        .toArray(Byte[]::new);
  }

  private static final class ByteToStringConverter extends IndexedStringConverter<Byte> {

    static final IndexedStringConverter<Byte> INSTANCE = new ByteToStringConverter();

    @Override
    public String toString(Pair<Integer, Byte> object) {
      return String.format("0x%02X", object.getValue());
    }

    @Override
    public Pair<Integer, Byte> fromString(String string) {
      return fromString(0, string);
    }

    @Override
    public Pair<Integer, Byte> fromString(Integer index, String string) {
      if (string.matches("0x[0-9a-fA-F]{1,2}")) {
        // hex, remove leading 0x
        return new Pair<>(index, (byte) Integer.parseUnsignedInt(string.substring(2), 16));
      } else if (string.matches("[0-9a-fA-F]{1,2}")) {
        return new Pair<>(index, (byte) Integer.parseUnsignedInt(string, 16));
      } else {
        throw new NumberFormatException("Not a valid 1-byte hex string: " + string);
      }
    }
  }

}
