package edu.wpi.first.tableviewer.dialog;

import edu.wpi.first.tableviewer.NetworkTableUtils;
import edu.wpi.first.tableviewer.entry.Entry;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.*;

public class AddBooleanDialogTest extends AbstractAddDialogTest<Boolean> {

  public AddBooleanDialogTest() {
    super(AddBooleanDialog::new);
  }

  @Test
  public void testTitle() {
    assertEquals("Add Boolean", dialog.getTitle());
  }

  @Test
  public void testEmptyKey() {
    TextField keyField = getKeyField();
    assertEquals("", keyField.getText());
    Button addButton = getAddButton();
    assertTrue(addButton.isDisable());
  }

  @Test
  public void testKey() {
    final String key = "test boolean key";
    TextField keyField = getKeyField();
    keyField.setText(key);
    Button addButton = getAddButton();
    clickOn(addButton);
    WaitForAsyncUtils.waitForFxEvents();
    Entry<Boolean> result = dialog.getResult();
    assertEquals(NetworkTableUtils.normalize(key), result.getKey());
  }

  @Test
  public void testSwitch() {
    final String key = "test switch";
    TextField keyField = getKeyField();
    keyField.setText(key);
    ToggleSwitch toggleSwitch = lookup(n -> "toggleSwitch".equals(n.getId())).query();
    Button addButton = getAddButton();

    clickOn(toggleSwitch);
    clickOn(addButton);
    WaitForAsyncUtils.waitForFxEvents();
    Entry<Boolean> result = dialog.getResult();
    assertEquals(NetworkTableUtils.normalize(key), result.getKey());
    assertTrue(result.getValue());
  }

  @Test
  public void testDisableKey() {
    dialog.setDisableKey(true);
    TextField keyField = getKeyField();
    Button addButton = getAddButton();
    assertTrue(keyField.isDisable());
    assertFalse(addButton.isDisable());

    dialog.setDisableKey(false);
    assertFalse(keyField.isDisable());
    assertTrue(addButton.isDisable());

    keyField.setText("something");
    assertFalse(addButton.isDisable());
  }

}
