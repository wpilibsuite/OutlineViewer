package edu.wpi.first.outlineviewer.controller;

import org.junit.Assert;
import org.junit.Test;

public class SettingsControllerButtonTest extends PreferencesControllerTest {

  @Test
  public void cancelButtonClosesStage() {
    clickOn("Cancel");

    Assert.assertFalse("Stage did not close", stage.isShowing());
  }

  @Test
  public void serverButtonClosesStage() {
    clickOn("Start Server");

    Assert.assertFalse("Stage did not close", stage.isShowing());
  }

  @Test
  public void clientButtonClosesStage() {
    lookup("#startClientButton").query().setDisable(false);
    clickOn("Start Client");

    Assert.assertFalse("Stage did not close", stage.isShowing());
  }

}