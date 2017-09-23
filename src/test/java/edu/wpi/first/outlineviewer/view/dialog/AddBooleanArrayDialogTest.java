package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Booleans;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
        Booleans.toArray(((ListView) lookup(".list-view").query()).getItems()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final boolean[] test = new boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

}
