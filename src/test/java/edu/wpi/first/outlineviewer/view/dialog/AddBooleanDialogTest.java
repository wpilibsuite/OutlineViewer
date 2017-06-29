package edu.wpi.first.outlineviewer.view.dialog;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AddBooleanDialogTest extends AddEntryDialogTest {

  public AddBooleanDialogTest() {
    super(AddBooleanDialog::new);
  }

  @Test
  public void testGetData() {
    clickOn(".toggle-switch");

    assertTrue((boolean) dialog.getData());
  }

}
