package edu.wpi.first.outlineviewer.view.dialog;

import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.util.function.Supplier;

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
}

