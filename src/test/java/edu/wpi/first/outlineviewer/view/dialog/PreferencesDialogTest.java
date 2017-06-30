package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PreferencesDialogTest extends ApplicationTest {

  private PreferencesDialog dialog;

  @Override
  public void start(Stage stage) throws Exception {
    dialog = new PreferencesDialog(ButtonType.CANCEL, ButtonType.OK);
    stage.setScene(dialog.getDialogPane().getScene());
    stage.show();
  }

  @Test
  public void testResultConverterFalse() {
    clickOn("Cancel");

    assertFalse(dialog.getResult());
  }

  @Test
  public void testResultConverterTrue() {
    clickOn("OK");

    assertTrue(dialog.getResult());
  }

  @Test
  public void getControllerTest() {
    assertNotNull(dialog.getController());
  }

}
