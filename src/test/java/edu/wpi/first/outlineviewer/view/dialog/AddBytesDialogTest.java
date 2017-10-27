package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.view.EditableTextFieldListCell;
import edu.wpi.first.outlineviewer.view.IndexedValue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
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
        ((ListView<IndexedValue<String>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
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
    ListView<IndexedValue<Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("BE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0).getValue().byteValue());
  }

  @Test
  void testToStringConverterWithLeading() {
    ListView<IndexedValue<Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("0xBE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0).getValue().byteValue());
  }

  @Test
  void testToStringConverterInvalid() throws InterruptedException {
    ListView<IndexedValue<Byte>> listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");
    ListCell<IndexedValue<Byte>> cell = from(listView).lookup(".list-cell").query();

    assertTrue(FxHelper.catchInJavaFXThread(() -> doubleClickOn(cell)
            .press(KeyCode.DELETE)
            .write(String.valueOf("Test"))
            .type(KeyCode.ENTER),
        NumberFormatException.class,
        3, TimeUnit.SECONDS));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDragDrop() {
    final Byte[] test = new Byte[]{0, 1, 2, 127, (byte) 255};
    dialog.setInitial(test);
    waitForFxEvents();

    drag("0xFF").dropTo("0x00");

    Assertions.assertArrayEquals(
        new Byte[]{(byte) 255, 0, 1, 2, 127},
        ((ListView<IndexedValue<Byte>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .toArray(Byte[]::new));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSaveOnCommitEdit() {
    final Byte[] test = new Byte[]{0};
    dialog.setInitial(test);
    waitForFxEvents();

    doubleClickOn("0x00").write("20");
    clickOn(lookup(".text-field-list-cell")
        .match(match -> ((EditableTextFieldListCell) match).getItem() == null)
        .queryAll()
        .stream()
        .sorted((node, t1) -> (int)(node.getLayoutY() - t1.getLayoutY()))
        .collect(Collectors.toList())
        .get(1));
    waitForFxEvents();

    Assertions.assertArrayEquals(
        new Byte[]{32},
        ((ListView<IndexedValue<Byte>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .toArray(Byte[]::new));
  }

}
