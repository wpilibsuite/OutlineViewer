package edu.wpi.first.outlineviewer;

import javafx.application.Application;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OutlineViewerTest extends FxRobot {

  Application app;

  @Before
  public void before() throws Exception {
    FxToolkit.registerPrimaryStage();
    Thread fxThread = new Thread(() -> {
      try {
        app = FxToolkit.setupApplication(OutlineViewer::new);
      } catch (TimeoutException ex) {
        fail();
      }
    });
    fxThread.start();
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  public void preferencesContinueToViewTest() {
    clickOn("Start");

    assertTrue(lookup("#root").tryQuery().isPresent());
  }

  @Test
  public void preferencesExit() {
    clickOn("Quit");
  }
}
