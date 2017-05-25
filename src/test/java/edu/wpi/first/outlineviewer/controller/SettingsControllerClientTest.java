package edu.wpi.first.outlineviewer.controller;

import javafx.scene.control.Button;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SettingsControllerClientTest extends SettingsControllerTest {

  private final String testText;
  private final boolean expectedResult;

  @Parameters
  @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"0", false},
        {"1", true},
        {"12", true},
        {"123", true},
        {"1234", true},
        {"190", true},
        {"1001", true},
        {"12345", false},
        {"localhost", true},
        {"127.0.0.1", true},
        {"10.1.90.2", true},
        {"roboRIO-190-FRC.local", true},
        {"", false}
    });
  }

  public SettingsControllerClientTest(String testText, boolean expectedResult) {
    this.testText = testText;
    this.expectedResult = !expectedResult;
  }

  @Test
  public void clientButtonValidation() {
    clickOn("#hostTextField").write(testText);

    Button clientButton = lookup("#startClientButton").query();
    Assert.assertTrue("Host validation failed for test case: " + testText,
        clientButton.isDisabled() == expectedResult);

    clickOn("Start Client");
    Assert.assertEquals("Stage did not close", expectedResult, stage.isShowing());
  }

}
