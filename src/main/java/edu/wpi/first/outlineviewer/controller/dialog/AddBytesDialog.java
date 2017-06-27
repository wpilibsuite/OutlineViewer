package edu.wpi.first.outlineviewer.controller.dialog;

import com.google.common.primitives.Bytes;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A dialog for editing arrays of raw bytes. These are represented as integers because Java doesn't
 * support unsigned bytes.
 */
public class AddBytesDialog extends AddEntryArrayDialog<Byte, byte[]> {

  public AddBytesDialog() {
    super("Raw Bytes");
  }

  /**
   * Sets the initial values in the raw bytes array.
   */
  public void setInitial(byte[] initialValues) {
    list.getItems().clear();
    for (byte value : initialValues) {
      list.getItems().add(value);
    }
  }

  @Override
  protected Byte getDefaultItem() {
    return 0;
  }

  @Override
  protected Callback<ListView<Byte>, ListCell<Byte>> getCellFactory() {
    return __ -> new TextFieldListCell<>(ByteToStringConverter.INSTANCE);
  }

  @Override
  protected byte[] getData() {
    return Bytes.toArray(list.getItems());
  }

  private static final class ByteToStringConverter extends StringConverter<Byte> {

    static final StringConverter<Byte> INSTANCE = new ByteToStringConverter();

    @Override
    public String toString(Byte object) {
      return String.format("0x%02X", object);
    }

    @Override
    public Byte fromString(String string) {
      if (string.matches("0x[0-9a-fA-F]{1,2}")) {
        // hex, remove leading 0x
        return (byte) Integer.parseUnsignedInt(string.substring(2), 16);
      } else if (string.matches("[0-9a-fA-F]{1,2}")) {
        return (byte) Integer.parseUnsignedInt(string, 16);
      } else {
        throw new NumberFormatException("Not a valid 1-byte hex string: " + string);
      }
    }

  }

}
