package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Bytes;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import edu.wpi.first.outlineviewer.view.IndexedStringConverter;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * A dialog for editing arrays of raw bytes. These are represented as integers because Java doesn't
 * support unsigned bytes.
 */
public class AddBytesDialog extends AddEntryArrayDialog<Pair<Integer, Byte>, byte[]> {

  public AddBytesDialog() {
    super("Raw Bytes");
  }

  /**
   * Sets the initial values in the raw bytes array.
   */
  public void setInitial(byte[] initialValues) {
    list.getItems().clear();
    int index = 0;
    for (byte value : initialValues) {
      list.getItems().add(new Pair<>(index++, value));
    }
  }

  @Override
  protected Pair<Integer, Byte> getDefaultItem() {
    return new Pair<>(list.getItems().size() + 1, new Byte("0"));
  }

  @Override
  protected Callback<ListView<Pair<Integer, Byte>>, ListCell<Pair<Integer, Byte>>>
      getCellFactory() {
    return __ -> new EditableTextFieldListCell<>(ByteToStringConverter.INSTANCE);
  }

  @Override
  protected byte[] getData() {
    return Bytes.toArray(list.getItems()
        .stream()
        .map(Pair::getValue)
        .collect(Collectors.toList()));
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
