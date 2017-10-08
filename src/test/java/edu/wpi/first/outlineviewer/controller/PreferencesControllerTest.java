package edu.wpi.first.outlineviewer.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.Preferences;
import edu.wpi.first.outlineviewer.view.dialog.PreferencesDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class PreferencesControllerTest extends ApplicationTest {

  private PreferencesController controller;

  @Override
  public void start(Stage stage) throws Exception {
    Preferences.reset();

    PreferencesDialog dialog = new PreferencesDialog(ButtonType.CANCEL, ButtonType.OK);
    controller = dialog.getController();
    stage.setScene(dialog.getDialogPane().getScene());
    stage.show();
  }

  @AfterEach
  void after() {
    NetworkTableUtilities.shutdown();
  }

  @Test
  void testServerIdDisabledInServerMode() {
    FxHelper.runAndWait(()
        -> ((ToggleSwitch) lookup("#modeSwitch").query()).setSelected(true));
    assertTrue(lookup("#idField").query().isDisable());
  }

  @Test
  void testServerIdEnabledInClientMode() {
    FxHelper.runAndWait(()
        -> ((ToggleSwitch) lookup("#modeSwitch").query()).setSelected(false));
    assertFalse(lookup("#idField").query().isDisable());
  }

  @Test
  void testServerPort() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("1234"));
    controller.save();
    assertEquals(1234, Preferences.getPort());
  }

  @Test
  void testServerPortEmpty() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).clear());
    controller.save();
    assertEquals(NetworkTableInstance.kDefaultPort, Preferences.getPort());
  }

  @Test
  void testIdEmpty() {
    FxHelper.runAndWait(() -> ((TextField) lookup("#idField").query()).clear());
    controller.save();
    assertEquals("localhost", Preferences.getIp());
  }

  @Test
  void testPortValidationTextField() {
    FxHelper.runAndWait(() -> {
      ((ToggleSwitch) lookup("#defaultPortSwitch").query()).setSelected(false);
      ((TextField) lookup("#portField").query()).setText("0");
    });
    assertTrue(controller.isInvalidPort());
    assertTrue(lookup("OK").query().isDisabled());

    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("65535"));
    assertFalse(controller.isInvalidPort());
    assertFalse(lookup("OK").query().isDisabled());

    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("65536"));
    assertTrue(controller.isInvalidPort());
    assertTrue(lookup("OK").query().isDisabled());

    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("-1"));
    assertTrue(controller.isInvalidPort());
    assertTrue(lookup("OK").query().isDisabled());

    FxHelper.runAndWait(() -> ((TextField) lookup("#portField").query()).setText("+1"));
    assertTrue(controller.isInvalidPort());
    assertTrue(lookup("OK").query().isDisabled());
  }
}
