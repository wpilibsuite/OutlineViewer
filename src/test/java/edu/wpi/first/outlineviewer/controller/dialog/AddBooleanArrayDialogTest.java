package edu.wpi.first.outlineviewer.controller.dialog;

import com.google.common.primitives.Booleans;
import javafx.scene.control.ListView;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AddBooleanArrayDialogTest extends AddEntryArrayDialogTest {

  public AddBooleanArrayDialogTest() {
    super(AddBooleanArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialValue() {
    final boolean[] test = new boolean[]{false, true, false, true};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test,
        Booleans.toArray(((ListView) lookup(".list-view").query()).getItems()));
  }

}
