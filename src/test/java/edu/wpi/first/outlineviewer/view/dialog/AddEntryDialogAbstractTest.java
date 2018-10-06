package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Tag("UI")
class AddEntryDialogAbstractTest
    extends AddEntryDialogTest<AddEntryDialogAbstractTest.MockAddEntryDialog> {

  AddEntryDialogAbstractTest() {
    super(MockAddEntryDialog::new);
  }

  @Test
  void testAddDisabled() {
    assertTrue(lookup("Add").query().isDisabled());
  }

  @Test
  void testAddEnabled() {
    clickOn("#keyField").write('a');

    assertFalse(lookup("Add").query().isDisabled());
  }

  @Test
  void testCancel() {
    clickOn("Cancel");

    assertNull(dialog.getResult());
  }

  @Test
  void testAdd() {
    clickOn("#keyField").write('a');
    clickOn("Add");

    assertTrue("Dialog result was actually: " + dialog.getResult(),
        dialog.getResult() instanceof Pair);
  }

  @Test
  void testSetKey() {
    final String test = "someKey";
    dialog.setKey(test);

    assertEquals(test, ((TextField) lookup("#keyField").query()).getText());
  }

  @Test
  void testSetDisableProperty() {
    dialog.setDisableKey(true);

    assertTrue(lookup("#keyField").query().isDisabled());
  }


  @Test
  void testDisableProperty() {
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
