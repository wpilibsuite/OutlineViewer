package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

class AddNumberDialogTest extends AddEntryDialogTest<AddNumberDialog> {

  AddNumberDialogTest() {
    super(AddNumberDialog::new);
  }

  @Test
  void testGetData() {
    ((TextField) lookup("#numberField").query()).clear();
    clickOn("#numberField").write("123.456");

    assertEquals(123.456, dialog.getData(), 0.0);
  }

  @Test
  void testGetDataInvalid() {
    ((TextField) lookup("#numberField").query()).clear();
    clickOn("#numberField").write("Test");

    assertNull(dialog.getData());
  }

}
