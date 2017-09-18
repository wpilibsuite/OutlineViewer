package edu.wpi.first.outlineviewer.view.dialog;

import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.util.function.Supplier;

public abstract class AddEntryDialogTest<T extends AddEntryDialog> extends ApplicationTest {

  T dialog;
  private final Supplier<T> dialogSupplier;

  AddEntryDialogTest(Supplier<T> dialogSupplier) {
    this.dialogSupplier = dialogSupplier;
  }

  @Override
  public void start(Stage stage) throws Exception {
    dialog = dialogSupplier.get();
    stage.setScene(dialog.getDialogPane().getScene());
    stage.show();
  }
}

