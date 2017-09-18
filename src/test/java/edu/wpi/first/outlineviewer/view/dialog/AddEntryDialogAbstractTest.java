package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AddEntryDialogAbstractTest
    extends AddEntryDialogTest<AddEntryDialogAbstractTest.MockAddEntryDialog> {

  public AddEntryDialogAbstractTest() {
    super(MockAddEntryDialog::new);
  }

  @Test
  public void testAddDisabled() {
    assertTrue(lookup("Add").query().isDisabled());
  }

  @Test
  public void testAddEnabled() {
    clickOn("#keyField").write('a');

    assertFalse(lookup("Add").query().isDisabled());
  }

  @Test
  public void testCancel() {
    clickOn("Cancel");

    assertNull(dialog.getResult());
  }

  @Test
  public void testAdd() {
    clickOn("#keyField").write('a');
    clickOn("Add");

    assertTrue("Dialog result was actually: " + dialog.getResult(),
        dialog.getResult() instanceof Pair);
  }

  @Test
  public void testSetKey() {
    final String test = "someKey";
    dialog.setKey(test);

    assertEquals(test, ((TextField) lookup("#keyField").query()).getText());
  }

  @Test
  public void testSetDisableProperty() {
    dialog.setDisableKey(true);

    assertTrue(lookup("#keyField").query().isDisabled());
  }


  @Test
  public void testDisableProperty() {
    dialog.setDisableKey(true);

    assertTrue(dialog.disableKeyProperty().get());
  }

  static class MockAddEntryDialog extends AddEntryDialog {

    MockAddEntryDialog() {
      super("Mock Dialog");
    }

    @Override
    protected Node createCustomControl() {
      return new Pane();
    }

    @Override
    protected Object getData() {
      return "";
    }
  }
}
