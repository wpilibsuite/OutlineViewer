package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Booleans;
import java.util.Arrays;
import java.util.stream.Collectors;
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
    final Boolean[] test = new Boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(Booleans.toArray(Arrays.stream(test).collect(Collectors.toList())),
        Booleans.toArray(((ListView) lookup(".list-view").query()).getItems()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetData() {
    final Boolean[] test = new Boolean[]{false, true, false, true};
    dialog.setInitial(test);

    assertArrayEquals(test, dialog.getData());
  }

}
