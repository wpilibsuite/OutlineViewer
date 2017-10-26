package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.wpi.first.outlineviewer.FxHelper;
import java.util.concurrent.TimeUnit;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.ListViewMatchers;

class AddBytesDialogTest extends AddEntryArrayDialogTest<AddBytesDialog> {

  AddBytesDialogTest() {
    super(AddBytesDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final Byte[] test = new Byte[]{0, 1, 2, 127, (byte) 255};
    dialog.setInitial(test);

    Assertions.assertArrayEquals(
        test,
        ((ListView<Pair<Integer, String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .toArray(Byte[]::new));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final Byte[] test = new Byte[]{0, 1, 2, 127, (byte) 255};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

  @Test
  void testToStringConverter() {
    ListView<Pair<Integer, Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("BE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0).getValue().byteValue());
  }

  @Test
  void testToStringConverterWithLeading() {
    ListView<Pair<Integer, Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("0xBE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0).getValue().byteValue());
  }

  @Test
  void testToStringConverterInvalid() throws InterruptedException {
    ListView<Pair<Integer, Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");
    ListCell<Pair<Integer, Byte>> cell = from(listView).lookup(".list-cell").query();

    assertTrue(FxHelper.catchInJavaFXThread(() -> doubleClickOn(cell)
            .press(KeyCode.DELETE)
            .write(String.valueOf("Test"))
            .type(KeyCode.ENTER),
        NumberFormatException.class,
        3, TimeUnit.SECONDS));
  }

}
