package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Bytes;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testfx.matcher.control.ListViewMatchers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddBytesDialogTest extends AddEntryArrayDialogTest<AddBytesDialog> {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  public AddBytesDialogTest() {
    super(AddBytesDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialValue() {
    final byte[] test = new byte[]{0, 1, 2, 127, (byte) 255};
    dialog.setInitial(test);

    assertArrayEquals(test,
        Bytes.toArray(((ListView) lookup(".list-view").query()).getItems()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetData() {
    final byte[] test = new byte[]{0, 1, 2, 127, (byte) 255};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

  @Test
  public void testToStringConverter() {
    ListView listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("BE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0));
  }

  @Test
  public void testToStringConverterWithLeading() {
    ListView listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");

    doubleClickOn((Node) from(listView).lookup(".list-cell").query()).press(KeyCode.DELETE)
        .write(String.valueOf("0xBE")).type(KeyCode.ENTER);

    assertEquals((byte) 190, listView.getItems().get(0));
  }

  @Test
  public void testToStringConverterInvalid() {
    ListView listView = lookup(ListViewMatchers.isEmpty()).query();
    clickOn("+");
    ListCell cell = from(listView).lookup(".list-cell").query();

    doubleClickOn(cell).press(KeyCode.DELETE).write(String.valueOf("Test")).type(KeyCode.ENTER);

    assertTrue(cell.isEditing());
  }

}
