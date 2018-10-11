package edu.wpi.first.outlineviewer.view.dialog;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

@Tag("UI")
class AddStringDialogTest extends AddEntryDialogTest<AddStringDialog> {

  AddStringDialogTest() {
    super(AddStringDialog::new);
  }

  @Test
  void testStringValue() {
    final String test = "The quick brown fox jumps over the lazy dog";
    clickOn("#valueField").write(test);

    assertEquals(test, dialog.getData());
  }

}
