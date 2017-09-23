package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.AutoClosingApplicationTest;
import edu.wpi.first.outlineviewer.FxHelper;
import edu.wpi.first.outlineviewer.OutlineViewer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
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
  public void testShowPrefsDialog() {
    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#preferencesMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    assertThat(lookup("#preferencesPaneRoot").query(), isVisible());
    targetWindow("Preferences").closeCurrentWindow();
  }

}
