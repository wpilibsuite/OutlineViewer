package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.FxHelper;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ToggleSwitchListCellTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    ListView<Boolean> listView = new ListView<>();
    listView.setCellFactory(__ -> new ToggleSwitchListCell());
    listView.setEditable(true);
    listView.getItems().add(false);

    stage.setScene(new Scene(listView));
    stage.show();
  }

  @Test
  public void testTextFalse() {
    FxHelper.runAndWait(() -> getToggleSwitch().setSelected(false));

    assertEquals("False", getToggleSwitchListCell().getText());
  }

  @Test
  public void testTextTrue() {
    FxHelper.runAndWait(() -> getToggleSwitch().setSelected(true));

    assertEquals("True", getToggleSwitchListCell().getText());
  }

  @Test
  public void testUpdateItemTrue() {
    FxHelper.runAndWait(() -> getToggleSwitchListCell().updateItem(true, false));

    assertTrue(getToggleSwitch().isSelected());
  }

  @Test
  public void testUpdateItemFalse() {
    FxHelper.runAndWait(() -> getToggleSwitchListCell().updateItem(false, false));

    assertFalse(getToggleSwitch().isSelected());
  }

  @Test
  public void testUpdateItemEmpty() {
    FxHelper.runAndWait(() -> getToggleSwitchListCell().updateItem(false, true));

    assertNull(getToggleSwitch());
  }

  private ToggleSwitchListCell getToggleSwitchListCell() {
    return lookup(".toggle-switch-list-cell").queryAll().stream()
        .filter(ToggleSwitchListCell.class::isInstance)
        .map(ToggleSwitchListCell.class::cast)
        .filter(t -> t.getGraphic().isVisible())
        .findAny()
        .get();
  }

  private ToggleSwitch getToggleSwitch() {
    return lookup(".toggle-switch").match(ToggleSwitch::isVisible).query();
  }

}
