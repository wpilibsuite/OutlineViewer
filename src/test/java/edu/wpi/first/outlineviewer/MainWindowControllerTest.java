package edu.wpi.first.outlineviewer;

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

  private Stage stage;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("MainWindow.fxml"));
    Pane rootPane = loader.load();
    this.stage = stage;
    stage.setScene(new Scene(rootPane));
    stage.show();
  }

  @Test
  public void testShowPrefsDialog() {
    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#prefsMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    assertThat(lookup("#prefsPaneRoot").query(), isVisible());
    targetWindow("Preferences").closeCurrentWindow();
  }

  // Tests for the search bar

  @Test
  public void testSearchAppears() {
    clickOn(stage, MouseButton.SECONDARY);

    FxHelper.runAndWait(() -> stage.getScene().getRoot().requestFocus());

    press(KeyCode.CONTROL, KeyCode.F);

    ToolBar searchBar = lookup("#searchBar").query();
    assertTrue(searchBar.isManaged());
  }

  @Test
  public void testSearchDisappears() {
    testSearchAppears();
    press(KeyCode.ESCAPE);
    waitForFxEvents();
    assertFalse(lookup("#searchBar").query().isManaged());
  }

}
