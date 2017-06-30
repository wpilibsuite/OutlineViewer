package edu.wpi.first.outlineviewer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OutlineViewerTest extends FxRobot {

  @Before
  public void before() throws Exception {
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

  @After
  public void after() {
    NetworkTableUtils.shutdown();
  }

  @Test
  public void preferencesContinueToViewTest() {
    clickOn("Start");

    assertTrue(lookup("#root").tryQuery().isPresent());
  }

  @Test
  public void preferencesExit() {
    clickOn("Quit");

    assertFalse(lookup("#root").tryQuery().isPresent());
  }
}
