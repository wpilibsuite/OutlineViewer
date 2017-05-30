package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.dialog.cell.ToggleSwitchListCell;
import edu.wpi.first.tableviewer.entry.Entry;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;

import static org.junit.Assert.*;
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

  @Test
  public void testEditArray() {
    dialog.setKey("testEditArray");
    testAddItem(); // add an item
    ToggleSwitchListCell cell = lookup(".toggle-switch-list-cell").query();
    ToggleSwitch toggleSwitch = (ToggleSwitch) cell.getGraphic();

    toggleSwitch.setSelected(true);
    assertTrue(getList().getItems().get(0));

    toggleSwitch.setSelected(false);
    assertFalse(getList().getItems().get(0));
  }

  @Test
  public void testAdd() {
    testEditArray();
    getKeyField().setText("testAdd");

    clickOn(getAddButton());
    waitForFxEvents();

    Entry<boolean[]> result = dialog.getResult();
    assertEquals("/testAdd", result.getKey());
    assertArrayEquals(new boolean[]{false}, result.getValue());
  }

  private ListView<Boolean> getList() {
    return lookup(n -> "list".equals(n.getId())).query();
  }

  private Button getAddItemButton() {
    return lookup(n -> "addItem".equals(n.getId())).query();
  }

}
