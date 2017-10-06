package edu.wpi.first.outlineviewer.view.dialog;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

class AddBooleanDialogTest extends AddEntryDialogTest<AddBooleanDialog> {

  AddBooleanDialogTest() {
    super(AddBooleanDialog::new);
  }

  @Test
  @Tag("NonWindowsTest")
  void testGetData() {
    clickOn(".toggle-switch");

    assertTrue(dialog.getData());
  }

}
