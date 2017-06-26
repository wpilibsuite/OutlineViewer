package edu.wpi.first.outlineviewer.dialog.cell;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ToggleSwitchListCellTest extends ApplicationTest {

  private ListView<Boolean> list;

  @Override
  public void start(Stage stage) throws Exception {
    list = new ListView<>();
    list.setCellFactory(v -> new ToggleSwitchListCell());
    list.setEditable(true);
    stage.setScene(new Scene(list));
    stage.show();
  }

  @Test
  public void testCellAsEditor() {
    final boolean value = true;
    list.getItems().add(value);
    WaitForAsyncUtils.waitForFxEvents();
    ToggleSwitchListCell cell = lookup(".toggle-switch-list-cell").query();
    ToggleSwitch toggleSwitch = (ToggleSwitch) cell.getGraphic();
    assertEquals(value, toggleSwitch.isSelected());

    clickOn(toggleSwitch);

    assertEquals(1, list.getItems().size());
    assertFalse(list.getItems().get(0));

    clickOn(toggleSwitch);

    assertEquals(1, list.getItems().size());
    assertTrue(list.getItems().get(0));
  }

}
