package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import com.google.common.primitives.Booleans;
import edu.wpi.first.outlineviewer.view.IndexedValue;
import java.util.stream.Collectors;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.Test;

class AddBooleanArrayDialogTest extends AddEntryArrayDialogTest<AddBooleanArrayDialog> {

  AddBooleanArrayDialogTest() {
    super(AddBooleanArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final boolean[] test = new boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(test,
        Booleans.toArray(((ListView<IndexedValue<Boolean>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .collect(Collectors.toList())));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final boolean[] test = new boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDragDrop() {
    final boolean[] test = new boolean[]{false, true};
    dialog.setInitial(test);
    waitForFxEvents();

    drag("False").dropTo("True");

    assertArrayEquals(
        new Boolean[]{true, false},
        ((ListView<IndexedValue<Boolean>>) lookup(".list-view").query())
            .getItems().stream()
            .map(IndexedValue::getValue)
            .toArray(Boolean[]::new));
  }

}
