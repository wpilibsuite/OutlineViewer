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

  @Test
  @Ignore("The search field can't be typed in for some reason")
  public void testClearSearch() {
    testSearchAppears();
    clickOn("#searchField", MouseButton.PRIMARY);
    type(KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.ENTER);
    assertEquals("ABC", this.lookup("#searchField").<TextField>query().getText());
  }

  // Tests for the table row context menus
  // The dialogs have their own unit tests; this is just to make sure the menus work

  @Test
  public void testAddString() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add string")).query();
    clickOn(add);
    assertThat(lookup(".add-string-dialog").query(), isVisible());
  }

  @Test
  public void testAddNumber() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add number")).query();
    clickOn(add);
    assertThat(lookup(".add-number-dialog").query(), isVisible());
  }

  @Test
  public void testAddBoolean() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add boolean")).query();
    clickOn(add);
    assertThat(lookup(".add-boolean-dialog").query(), isVisible());
  }

  @Test
  public void testAddStringArray() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add string array")).query();
    clickOn(add);
    assertThat(lookup(".add-string-array-dialog").query(), isVisible());
  }

  @Test
  public void testAddNumberArray() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add number array")).query();
    clickOn(add);
    assertThat(lookup(".add-number-array-dialog").query(), isVisible());
  }

  @Test
  public void testAddBooleanArray() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add boolean array")).query();
    clickOn(add);
    assertThat(lookup(".add-boolean-array-dialog").query(), isVisible());
  }

  @Test
  public void testAddBytes() {
    TreeTableCell cell = lookup(NodeMatchers.hasText("Root")).query();
    clickOn(cell, MouseButton.SECONDARY);
    Node add = lookup(NodeMatchers.hasText("Add raw bytes")).query();
    clickOn(add);
    assertThat(lookup(".add-bytes-dialog").query(), isVisible());
  }

}
