package edu.wpi.first.outlineviewer.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import edu.wpi.first.outlineviewer.AutoClosingApplicationTest;
import edu.wpi.first.outlineviewer.NetworkTableUtilities;
import edu.wpi.first.outlineviewer.OutlineViewer;
import java.util.stream.Stream;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MainWindowControllerTest extends AutoClosingApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    NetworkTableUtilities.createNewNetworkTableInstance();
    FXMLLoader loader = new FXMLLoader(OutlineViewer.class.getResource("MainWindow.fxml"));
    Pane rootPane = loader.load();
    stage.setScene(new Scene(rootPane));
    stage.show();
  }

  private static Stream<Arguments> dataDisplayArguments() {
    return Stream.of(
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setNumber(987.654), "987.654"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setString("myValue"), "myValue"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setBoolean(false), "false"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setNumberArray(new Number[]{0, 1, -1}), "[0.0, 1.0, -1.0]"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setStringArray(new String[]{"A", "B" ,"C"}), "[A, B, C]"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setBooleanArray(new Boolean[]{false, true}), "[false, true]"),
        Arguments.of((Runnable) () -> NetworkTableUtilities.getNetworkTableInstance()
            .getEntry("Test").setRaw(new byte[]{0, 1, 5}), "[0, 1, 5]")
    );
  }

  @ParameterizedTest
  @MethodSource("dataDisplayArguments")
  void testDataDisplay(Runnable addEntry, String expected) {
    addEntry.run();

    waitForFxEvents();
    waitForNtcoreEvents();

    assertTrue("Could not find " + expected, lookup(expected).tryQuery().isPresent());
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

  @Test
  @Tag("NonHeadlessTests")
  void testDeleteItemsKey() {
    clickOn("Root", MouseButton.SECONDARY)
        .clickOn("Add boolean")
        .write("zz")
        .clickOn("Add")
        .clickOn("zz")
        .type(KeyCode.DELETE);
    waitForFxEvents();
    assertFalse(lookup("zz").tryQuery().isPresent());
  }

  @Test
  @Tag("NonHeadlessTests")
  void testDeleteItemsMenu() {
    clickOn("Root", MouseButton.SECONDARY)
        .clickOn("Add boolean")
        .write("zz")
        .clickOn("Add")
        .clickOn("zz", MouseButton.SECONDARY)
        .clickOn("Delete");
    waitForFxEvents();
    assertFalse(lookup("zz").tryQuery().isPresent());
  }

  /**
   * Waits for ntcore listeners to be fired. This is a <i>blocking operation</i>.
   */
  private void waitForNtcoreEvents() {
    NetworkTableUtilities.getNetworkTableInstance().waitForEntryListenerQueue(3);
  }
}
