package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

public class ConnectionIndicatorControllerTest extends ApplicationTest {

  private ConnectionIndicatorController controller;

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTable.shutdown();
    FXMLLoader loader =
        new FXMLLoader(ConnectionIndicatorController.class.getResource("ConnectionIndicator.fxml"));
    Pane connectionIndicator = loader.load();
    controller = loader.getController();
    stage.setScene(new Scene(connectionIndicator));
    stage.show();
  }

  @Test
  public void testLabelInServerMode() {
    NetworkTableUtils.setServer(12345);
    sleep(200); // wait for server to start
    FxHelper.runAndWait(() -> controller.updateConnectionLabel());
    assertEquals("Running server (No clients)", getConnectionLabel().getText());
  }

  private Label getConnectionLabel() {
    return lookup(".connection-label").query();
  }

  private Pane getConnectionBackground() {
    return lookup(".connection-background").query();
  }

}
