package edu.wpi.first.outlineviewer.controller;

import edu.wpi.first.outlineviewer.AutoClosingApplicationTest;
import edu.wpi.first.outlineviewer.OutlineViewer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
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
  void testShowPreferencesDialog() {
    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#preferencesMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    assertThat(lookup("#preferencesPaneRoot").query(), isVisible());
  }

  @Test
  void testPreferencesDialogCentered() {
    Window main = window(0);

    main.setX(Screen.getPrimary().getVisualBounds().getMinX());
    main.setY(Screen.getPrimary().getVisualBounds().getMinY());

    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#preferencesMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    Window dialog = window("Preferences");

    assertAll("Window in bounds",
        () -> assertTrue("x", dialog.getX() > main.getX()),
        () -> assertTrue("y", dialog.getY() > main.getY()),
        () -> assertTrue("x width", dialog.getX() + dialog.getWidth()
            < main.getX() + main.getWidth()),
        () -> assertTrue("y height", dialog.getY() + dialog.getHeight()
            < main.getY() + main.getHeight())
    );
  }

  @Test
  void connectionIndicatorVisibleTest() {
    assertTrue(lookup("#connectionLabel").tryQuery().isPresent());
  }

  @Test
  void exitMenuTest() {
    clickOn("#fileMenu", MouseButton.PRIMARY);
    waitForFxEvents();

    clickOn("#exitMenuButton", MouseButton.PRIMARY);
    waitForFxEvents();

    assertTrue(listWindows().isEmpty());
  }
}
