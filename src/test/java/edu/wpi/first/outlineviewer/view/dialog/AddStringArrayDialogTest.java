package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ListView;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class AddStringArrayDialogTest extends AddEntryArrayDialogTest {

  public AddStringArrayDialogTest() {
    super(AddStringArrayDialog::new);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialValue() {
    final String[] test = new String[]{"", "A String", "And another!"};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test, ((ListView) lookup(".list-view").query()).getItems().toArray());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetData() {
    final String[] test = new String[]{"", "A String", "And another!"};
    ((AddEntryArrayDialog) dialog).setInitial(test);

    assertArrayEquals(test, (String[]) dialog.getData());
  }

}
