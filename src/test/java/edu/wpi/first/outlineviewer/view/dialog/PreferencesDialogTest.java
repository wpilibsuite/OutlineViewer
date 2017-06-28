package edu.wpi.first.outlineviewer.view.dialog;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

public class PreferencesDialogTest extends ApplicationTest {

  private PreferencesDialog dialog;

  @Override
  public void start(Stage stage) throws Exception {
    dialog = new PreferencesDialog(ButtonType.CLOSE);
    stage.setScene(dialog.getDialogPane().getScene());
    stage.show();
  }

  @Test
  public void testResultConverter() {
    clickOn("Close");

    assertEquals(ButtonType.CLOSE, dialog.getResult());
  }

}
