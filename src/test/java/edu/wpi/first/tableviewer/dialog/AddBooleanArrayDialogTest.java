package edu.wpi.first.tableviewer.dialog;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AddBooleanArrayDialogTest extends AbstractAddDialogTest<boolean[]> {

  public AddBooleanArrayDialogTest() {
    super(AddBooleanArrayDialog::new);
  }

  @Test
  public void testAddItem() {
    ListView<Boolean> list = getList();
    Button addItemButton = getAddItemButton();
    assertTrue(list.getItems().isEmpty());

    clickOn(addItemButton);
    waitForFxEvents();

    assertEquals(1, list.getItems().size());
    assertFalse(list.getItems().get(0));
  }

  private ListView<Boolean> getList() {
    return lookup(n -> "list".equals(n.getId())).query();
  }

  private Button getAddItemButton() {
    return lookup(n -> "addItem".equals(n.getId())).query();
  }

}
