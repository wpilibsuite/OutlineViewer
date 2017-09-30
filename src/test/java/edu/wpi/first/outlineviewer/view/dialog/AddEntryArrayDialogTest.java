package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ListView;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

abstract class AddEntryArrayDialogTest<T extends AddEntryDialog>
    extends AddEntryDialogTest<T> {

  AddEntryArrayDialogTest(Supplier<T> dialogSupplier) {
    super(dialogSupplier);
  }

  @Test
  void testAddElementButton() {
    final int initialLength = ((ListView) lookup(".list-view").query()).getItems().size();
    clickOn("+");

    assertEquals(initialLength + 1,
        ((ListView) lookup(".list-view").query()).getItems().size());
  }

}
