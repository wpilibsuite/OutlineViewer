package edu.wpi.first.outlineviewer;

import org.junit.jupiter.api.AfterEach;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * A helpful version of {@link ApplicationTest} that automatically cleans up windows after it
 * finishes.
 */
public abstract class AutoClosingApplicationTest extends ApplicationTest {

  @AfterEach
  public void closeAllStages() {
    listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
  }

}
