package edu.wpi.first.outlineviewer.controller.dialog;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddStringDialogTest extends AddEntryDialogTest {

  public AddStringDialogTest() {
    super(AddStringDialog::new);
  }

  @Test
  public void testStringValue() {
    final String test = "The quick brown fox jumps over the lazy dog";
    clickOn("#valueField").write(test);

    assertEquals(test, dialog.getData());
  }

}
