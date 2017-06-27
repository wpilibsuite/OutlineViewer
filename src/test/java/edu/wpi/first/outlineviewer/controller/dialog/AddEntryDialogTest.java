package edu.wpi.first.outlineviewer.controller.dialog;

import edu.wpi.first.outlineviewer.AutoClosingApplicationTest;
import javafx.stage.Stage;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public abstract class AddEntryDialogTest extends AutoClosingApplicationTest {

  AddEntryDialog dialog;
  private final Supplier<AddEntryDialog> dialogSupplier;

  AddEntryDialogTest(Supplier<AddEntryDialog> dialogSupplier) {
    this.dialogSupplier = dialogSupplier;
  }

  @Override
  public void start(Stage stage) throws Exception {
    dialog = dialogSupplier.get();
    dialog.show();
  }

  @Test
  public void testCanceled() {
    clickOn("Cancel");

    assertFalse(dialog.isShowing());
  }

  @Test
  public void testAddDisabled() {
    clickOn("Add");

    assertTrue(dialog.isShowing());
  }

  @Test
  public void testAddEnabled() {
    clickOn("#keyField").write('a');
    clickOn("Add");

    assertFalse(dialog.isShowing());
  }
}
