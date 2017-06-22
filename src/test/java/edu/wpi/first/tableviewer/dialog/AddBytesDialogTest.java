package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.FxHelper;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AddBytesDialogTest extends AbstractAddDialogTest<byte[]> {

  public AddBytesDialogTest() {
    super(AddBytesDialog::new);
  }

  @Override
  public void start(Stage stage) throws Exception {
    super.start(stage);
  }

  @Test
  public void testTitle() {
    assertEquals("Add Raw Bytes", dialog.getTitle());
  }

  @Test
  public void testAddItem() {
    clickOn(getAddItemButton());
    waitForFxEvents();
    assertEquals(1, getList().getItems().size());
  }

  @Test
  public void testDefaultValueIsZero() {
    testAddItem();
    assertArrayEquals(new byte[]{0}, dialog.getData());
  }

  @Test
  public void testViewedAsHex() {
    for (int i = 0; i < 256; i++) {
      final byte b = (byte) i;
      Platform.runLater(() -> {
        getList().getItems().clear();
        getList().getItems().add(b);
      });
      waitForFxEvents();
      ListCell<Byte> cell = getCells().findAny()
                                      .orElse(null);
      if (cell == null) {
        fail("No cell for " + (b & 0xFF)); // convert back to i
      }
      assertEquals(String.format("0x%02X", i), cell.getText());
    }
  }

  @Test
  public void testSetInitial() {
    final byte[] data = {-1, 0, 1, 127, -128};
    FxHelper.runAndWait(() -> ((AddBytesDialog) dialog).setInitial(data));
    ListView<Byte> list = getList();
    for (int i = 0; i < list.getItems().size(); i++) {
      assertEquals(data[i], list.getItems().get(i).byteValue());
    }
    assertArrayEquals(data, dialog.getData());
  }

  private void testEnterText(byte expectedValue, KeyCode... keyCodes) {
    testAddItem();

    ListCell<Byte> cell = getCells().findAny().orElse(null);
    Platform.runLater(cell::startEdit);
    waitForFxEvents();

    TextField editor = (TextField) cell.getGraphic();
    doubleClickOn(editor, MouseButton.PRIMARY);

    type(keyCodes);
    type(KeyCode.ENTER);
    waitForFxEvents();
    assertEquals(expectedValue, cell.getItem().byteValue());
  }

  private Stream<ListCell<Byte>> getCells() {
    Set<ListCell<Byte>> cells = lookup(".text-field-list-cell").queryAll();
    return cells.stream().filter(c -> c.getItem() != null);
  }

  private ListView<Byte> getList() {
    return lookup(n -> "list".equals(n.getId())).query();
  }

  private Button getAddItemButton() {
    return lookup(n -> "addItem".equals(n.getId())).query();
  }

}
