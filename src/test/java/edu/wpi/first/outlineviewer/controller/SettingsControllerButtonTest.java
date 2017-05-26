package edu.wpi.first.outlineviewer.controller;

import org.junit.Assert;
import org.junit.Test;

public class SettingsControllerButtonTest extends SettingsControllerTest {

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

}
