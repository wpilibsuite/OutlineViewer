package edu.wpi.first.outlineviewer.view.dialog;

import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.function.Supplier;

@Tag("UI")
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

