package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.TextField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AddNumberDialogTest extends AddEntryDialogTest {

  public AddNumberDialogTest() {
    super(AddNumberDialog::new);
  }

  @Test
  public void testGetData() {
    ((TextField) lookup("#numberField").query()).clear();
    clickOn("#numberField").write("123.456");

    assertEquals(123.456, (double) dialog.getData(), 0.0);
  }

  @Test
  public void testGetDataInvalid() {
    ((TextField) lookup("#numberField").query()).clear();
    clickOn("#numberField").write("Test");

    assertNull(dialog.getData());
  }

}
