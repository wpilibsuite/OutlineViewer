package edu.wpi.first.tableviewer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.matcher.base.NodeMatchers;

import static org.junit.Assert.*;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class MainWindowControllerTest extends AutoClosingApplicationTest {

  private Stage stage;
  private MainWindowController controller;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("MainWindow.fxml"));
    Pane rootPane = loader.load();
    controller = loader.getController();
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
