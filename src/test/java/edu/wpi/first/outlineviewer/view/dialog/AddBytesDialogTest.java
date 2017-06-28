package edu.wpi.first.outlineviewer.view.dialog;

import com.google.common.primitives.Bytes;
import javafx.scene.control.ListView;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AddBytesDialogTest extends AddEntryArrayDialogTest {

  public AddBytesDialogTest() {
    super(AddBytesDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialValue() {
    final byte[] test = new byte[]{0, 1, 2, 127, (byte) 255};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test,
        Bytes.toArray(((ListView) lookup(".list-view").query()).getItems()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetData() {
    final byte[] test = new byte[]{0, 1, 2, 127, (byte) 255};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test, (byte[]) dialog.getData());
  }

}
