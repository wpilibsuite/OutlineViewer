package edu.wpi.first.outlineviewer.view.dialog;

import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public abstract class AddEntryDialogTest extends ApplicationTest {

  AddEntryDialog dialog;
  private final Supplier<AddEntryDialog> dialogSupplier;

  AddEntryDialogTest(Supplier<AddEntryDialog> dialogSupplier) {
    this.dialogSupplier = dialogSupplier;
  }

  @Override
  public void start(Stage stage) throws Exception {
    dialog = dialogSupplier.get();
    stage.setScene(dialog.getDialogPane().getScene());
    stage.show();
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
}
