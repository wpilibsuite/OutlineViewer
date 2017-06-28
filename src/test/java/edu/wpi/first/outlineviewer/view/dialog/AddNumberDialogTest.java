package edu.wpi.first.outlineviewer.view.dialog;

//import static org.junit.Assert.assertEquals;

import javafx.scene.control.TextField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddNumberDialogTest extends AddEntryDialogTest {

  public AddNumberDialogTest() {
    super(AddNumberDialog::new);
  }

  // TODO: Add input validation
  /*public void testStringValueInvalid() {
    final String test = "The quick brown fox jumps over the lazy dog";
    clickOn("#numberSpinner").write(test);

    assertEquals("", dialog.getData());
  }*/

  @Test
  public void testGetData() throws Exception {
    ((TextField) lookup("#numberField").query()).clear();
    clickOn("#numberField").write("123.456");

    assertEquals(123.456, (double) dialog.getData(), 0.0);
  }

}
