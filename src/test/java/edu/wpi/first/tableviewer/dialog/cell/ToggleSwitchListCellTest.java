package edu.wpi.first.tableviewer.dialog.cell;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.*;

public class ToggleSwitchListCellTest extends ApplicationTest {

  private Stage stage;
  private ListView<Boolean> list;

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    list = new ListView<>();
    list.setCellFactory(v -> new ToggleSwitchListCell());
    list.setEditable(true);
    stage.setScene(new Scene(list));
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    stage.close();
  }

  @Test
  public void testCellAsEditor() {
    final boolean value = true;
    list.getItems().add(value);
    WaitForAsyncUtils.waitForFxEvents();
    ToggleSwitchListCell cell = lookup(".toggle-switch-list-cell").query();
    ToggleSwitch toggleSwitch = (ToggleSwitch) cell.getGraphic();
    assertEquals(value, toggleSwitch.isSelected());

    toggleSwitch.setSelected(!toggleSwitch.isSelected());

    assertEquals(1, list.getItems().size());
    assertEquals(false, list.getItems().get(0));

    toggleSwitch.setSelected(!toggleSwitch.isSelected());

    assertEquals(1, list.getItems().size());
    assertEquals(true, list.getItems().get(0));
  }

}
