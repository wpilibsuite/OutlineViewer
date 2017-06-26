package edu.wpi.first.outlineviewer.controller.dialog;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.util.function.Supplier;

/**
 * Common superclass to help tests of the "AddFooDialog"s.
 */
public abstract class AbstractAddDialogTest<T> extends ApplicationTest {

  protected AddEntryDialog<T> dialog;
  private final Supplier<AddEntryDialog<T>> dialogSupplier;

  public AbstractAddDialogTest(Supplier<AddEntryDialog<T>> dialogSupplier) {
    this.dialogSupplier = dialogSupplier;
  }

  @Override
  public void start(Stage stage) throws Exception {
    dialog = dialogSupplier.get();
    dialog.show();
  }

  @Override
  public void stop() throws Exception {
    dialog.close();
  }

  protected final Button getAddButton() {
    return (Button) dialog.getDialogPane().lookupButton(AddEntryDialog.add);
  }

  protected final TextField getKeyField() {
    return lookup(n -> "keyField".equals(n.getId())).query();
  }

}
