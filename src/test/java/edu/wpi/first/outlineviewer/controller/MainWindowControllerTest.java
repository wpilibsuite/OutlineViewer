package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.AutoClosingApplicationTest;
import edu.wpi.first.outlineviewer.OutlineViewer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class MainWindowControllerTest extends AutoClosingApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(OutlineViewer.class.getResource("MainWindow.fxml"));
    Pane rootPane = loader.load();
    stage.setScene(new Scene(rootPane));
    stage.show();
  }

  @Test
  void testShowPrefsDialog() {
    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#preferencesMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    assertThat(lookup("#preferencesPaneRoot").query(), isVisible());
    targetWindow("Preferences").closeCurrentWindow();
  }

  @Test
  void connectionIndicatorVisibleTest() {
    assertTrue(lookup("#connectionLabel").tryQuery().isPresent());
  }

}
