package edu.wpi.first.outlineviewer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Tag("NonWindowsTest")
class OutlineViewerTest extends FxRobot {

  @BeforeEach
  void before() throws Exception {
    NetworkTableUtilities.createNewNetworkTableInstance();

    FxToolkit.registerPrimaryStage();
    Thread fxThread = new Thread(() -> {
      try {
        FxToolkit.setupApplication(OutlineViewer::new);
      } catch (TimeoutException ex) {
        fail();
      }
    });
    fxThread.start();
    WaitForAsyncUtils.waitForFxEvents();
  }

  @AfterEach
  void after() {
    NetworkTableUtilities.shutdown();
  }

  @Test
  void preferencesContinueToViewTest() {
    clickOn("Start");

    assertTrue(lookup("#root").tryQuery().isPresent());
  }

  @Test
  void preferencesExit() {
    clickOn("Quit");

    assertFalse(lookup("#root").tryQuery().isPresent());
  }
}
