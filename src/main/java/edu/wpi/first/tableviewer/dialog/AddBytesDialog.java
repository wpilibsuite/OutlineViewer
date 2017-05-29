package edu.wpi.first.tableviewer.dialog;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

/**
 * A dialog for editing arrays of raw bytes. These are represented as integers because Java doesn't
 * support unsigned bytes.
 */
public class AddBytesDialog extends AddEntryDialog<byte[]> {

  private ListView<Byte> list;

  public AddBytesDialog() {
    super("Raw Bytes");
    getDialogPane().setMaxHeight(300);
  }

  @Override
  protected Node createCustomControl() {
    list = new ListView<>();
    list.setEditable(true);
    list.setCellFactory(__ -> new TextFieldListCell<>(ByteToStringConverter.INSTANCE));
    list.setOnKeyPressed(e -> {
      KeyCode code = e.getCode();
      if (code == KeyCode.DELETE) {
        removeSelected();
      }
    });

    Button add = new Button("+");
    add.setPrefWidth(40);
    add.setOnAction(__ -> list.getItems().add((byte) 0));

    return new VBox(8, list, add);
  }

  public void setInitial(byte[] initialValues) {
    list.getItems().clear();
    for (byte value : initialValues) {
      list.getItems().add(value);
    }
  }

  private void removeSelected() {
    list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
  }

  @Override
  protected byte[] getData() {
    return toPrimitiveArray(list.getItems());
  }

  private static byte[] toPrimitiveArray(List<Byte> list) {
    byte[] arr = new byte[list.size()];
    for (byte i = 0; i < list.size(); i++) {
      arr[i] = list.get(i);
    }
    return arr;
  }

  public static byte[] fromUnsigned(byte[] unsigned) {
    byte[] array = new byte[unsigned.length];
    for (byte i = 0; i < unsigned.length; i++) {
      array[i] = (byte) (unsigned[i] & 0xFF);
    }
    return array;
  }

  private static final class ByteToStringConverter extends StringConverter<Byte> {

    public static final StringConverter<Byte> INSTANCE = new ByteToStringConverter();

    @Override
    public String toString(Byte b) {
      return String.format("0x%02X", b);
    }

    @Override
    public Byte fromString(String string) {
      if (string.matches("0x[0-9A-F]{1,2}")) {
        // hex, remove leading 0x
        return (byte) Integer.parseUnsignedInt(string.substring(2), 16);
      } else if (string.matches("[0-9A-F]{1,2}")) {
        return (byte) Integer.parseUnsignedInt(string, 16);
      } else {
        throw new NumberFormatException("Not a valid 1-byte hex string: " + string);
      }
    }

  }

}
