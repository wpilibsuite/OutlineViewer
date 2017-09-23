package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.Preferences;
import edu.wpi.first.outlineviewer.view.dialog.PreferencesDialog;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PreferencesControllerTest extends ApplicationTest {

  private PreferencesController controller;

  @Override
  public void start(Stage stage) throws Exception {
    Preferences.reset();

    FXMLLoader loader
        = new FXMLLoader(PreferencesDialog.class.getResource("PreferencesDialog.fxml"));
    Pane pane = loader.load();
    controller = loader.getController();
    stage.setScene(new Scene(pane));
    stage.show();
  }

  @After
  public void after() {
    NetworkTableUtilities.shutdown();
  }

  @Test
  public void testServerIdDisabledInServerMode() {
    FxHelper.runAndWait(()
        -> ((ToggleSwitch) lookup("#modeSwitch").query()).setSelected(true));
    assertTrue(lookup("#idField").query().isDisable());
  }

  @Test
  public void testServerIdEnabledInClientMode() {
    FxHelper.runAndWait(()
        -> ((ToggleSwitch) lookup("#modeSwitch").query()).setSelected(false));
    assertFalse(lookup("#idField").query().isDisable());
  }

  @Test
  public void testServerPort() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("1234"));
    controller.save();
    assertEquals(1234, Preferences.getPort());
  }

  @Test
  public void testServerPortEmpty() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).clear());
    controller.save();
    assertEquals(NetworkTable.DEFAULT_PORT, Preferences.getPort());
  }

  @Test
  public void testIdEmpty() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#idField").query()).clear());
    controller.save();
    assertEquals("localhost", Preferences.getIp());
  }
}
