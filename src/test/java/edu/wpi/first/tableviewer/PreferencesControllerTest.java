package edu.wpi.first.tableviewer;

import edu.wpi.first.tableviewer.dialog.PreferencesDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class PreferencesControllerTest extends ApplicationTest {

  private PreferencesController controller;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(PreferencesController.class.getResource("Preferences.fxml"));
    Pane prefsPane = loader.load();
    controller = loader.getController();
    stage.setScene(new Scene(prefsPane));
    stage.show();
    Prefs.setServer(true);
    Prefs.setPort(1735);
    Prefs.setIp("localhost");
    Prefs.setShowMetaData(true);
  }

  @Test
  public void testServerIdDisabledInServerMode() {
    Prefs.setServer(true);
    TextField idField = lookup(n -> "idField".equals(n.getId())).query();
    assertTrue(idField.isDisable());
  }

  @Test
  public void testServerIdEnabledInClientMode() {
    Prefs.setServer(false);
    TextField idField = lookup(n -> "idField".equals(n.getId())).query();
    assertFalse(idField.isDisable());
  }

  @Test
  public void testChangeServerIdToTeamNumber() {
    Prefs.setServer(false);
    TextField idField = lookup(n -> "idField".equals(n.getId())).query();
    idField.setText("192");
    waitForFxEvents();
    controller.start();
    assertEquals("roborio-192-frc.local", Prefs.getResolvedAddress());
  }

  @Test
  public void testNoAddressInterpretedAsLocalhost() {
    Prefs.setServer(false);
    TextField idField = lookup(n -> "idField".equals(n.getId())).query();
    idField.setText("");
    waitForFxEvents();
    controller.start();
    assertEquals("localhost", Prefs.getResolvedAddress());
  }

  @Test
  public void testServerPortInServerMode() {
    Prefs.setServer(true);
    TextField portField = lookup(n -> "portField".equals(n.getId())).query();
    portField.setText("9999");
    waitForFxEvents();
    controller.start();
    assertEquals(9999, Prefs.getPort());
  }

  @Test
  public void testServerPortInClientMode() {
    Prefs.setServer(false);
    TextField portField = lookup(n -> "portField".equals(n.getId())).query();
    portField.setText("2084");
    waitForFxEvents();
    controller.start();
    assertEquals(2084, Prefs.getPort());
  }

  @Test
  public void testServerSwitch() {
    ToggleSwitch serverSwitch = lookup(n -> "modeSwitch".equals(n.getId())).query();
    assertEquals(Prefs.isServer(), serverSwitch.isSelected());
    serverSwitch.setSelected(!serverSwitch.isSelected());
    waitForFxEvents();
    assertEquals(serverSwitch.isSelected(), Prefs.isServer());
  }

  @Test
  public void testMetadataSwitch() {
    ToggleSwitch metadataSwitch = lookup(n -> "metadataSwitch".equals(n.getId())).query();
    assertEquals(Prefs.isShowMetaData(), metadataSwitch.isSelected());
    metadataSwitch.setSelected(!metadataSwitch.isSelected());
    waitForFxEvents();
    assertEquals(metadataSwitch.isSelected(), Prefs.isShowMetaData());
  }

}
