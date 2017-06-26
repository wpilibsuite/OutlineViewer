package edu.wpi.first.outlineviewer.view;

import edu.wpi.first.outlineviewer.FxHelper;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertNull;

public class ToggleSwitchListCellTest extends ApplicationTest {

  private ToggleSwitchListCell cell;

  @Override
  public void start(Stage stage) throws Exception {
    cell = new ToggleSwitchListCell();
  }

  @Test
  public void testUpdateItemEmpty() {
    FxHelper.runAndWait(() -> cell.updateItem(false, true));

    assertNull(cell.getGraphic());
  }

}
