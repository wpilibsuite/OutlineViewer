package edu.wpi.first.outlineviewer.controller;

import javafx.application.Platform;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

public class MenuBarTest extends OutlineViewerControllerTest {

  @Test
  public void testOpenSettings() throws TimeoutException, InterruptedException {
    clickOn("File").clickOn("Settings");

    targetWindow("Settings");
    Assert.assertTrue(targetWindow().isShowing());

    Platform.runLater(() -> targetWindow().hide());
    WaitForAsyncUtils.waitForFxEvents();

    // Run again to test second branch of if statement
    clickOn("File").clickOn("Settings");

    targetWindow("Settings");
    Assert.assertTrue(targetWindow().isShowing());

    Platform.runLater(() -> targetWindow().hide());
    WaitForAsyncUtils.waitForFxEvents();
  }

}
