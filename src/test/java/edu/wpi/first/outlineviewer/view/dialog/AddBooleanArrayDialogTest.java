package edu.wpi.first.outlineviewer.view.dialog;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import com.google.common.primitives.Booleans;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddBooleanArrayDialogTest extends AddEntryArrayDialogTest<AddBooleanArrayDialog> {

  AddBooleanArrayDialogTest() {
    super(AddBooleanArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInitialValue() {
    final Boolean[] test = new Boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(
        Booleans.toArray(Arrays.stream(test)
            .collect(Collectors.toList())),
        Booleans.toArray(((ListView<Pair<Integer, Boolean>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList())));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final Boolean[] test = new Boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

  @Test
  @SuppressWarnings("unchecked")
  void testDragDrop() {
    final Boolean[] test = new Boolean[]{false, true};
    dialog.setInitial(test);
    waitForFxEvents();

    drag("False").dropTo("True");

    Assertions.assertEquals(
        Arrays.stream(new Boolean[]{true, false})
            .collect(Collectors.toList()),
        ((ListView<Pair<Integer, Boolean>>) lookup(".list-view").query())
            .getItems().stream()
            .map(Pair::getValue)
            .collect(Collectors.toList()));
  }

}
