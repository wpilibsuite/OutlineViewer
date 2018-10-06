package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.OutlineViewer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.Assert.assertEquals;

@Tag("UI")
public class ConnectionIndicatorControllerTest extends ApplicationTest {

  private ConnectionIndicatorController controller;

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtilities.shutdown();
    FXMLLoader loader = new FXMLLoader(OutlineViewer.class
        .getResource("ConnectionIndicator.fxml"));
    Pane connectionIndicator = loader.load();
    controller = loader.getController();
    stage.setScene(new Scene(connectionIndicator));
    stage.show();
  }

  @Test
  void testLabelInServerMode() {
    NetworkTableUtilities.setServer(12345);
    sleep(200); // wait for server to start
    FxHelper.runAndWait(() -> controller.updateConnectionLabel());
    assertEquals("Running server (No clients)", getConnectionLabel().getText());
  }

  private Label getConnectionLabel() {
    return lookup(".connection-label").query();
  }

}
