package edu.wpi.first.tableviewer;

import org.junit.After;
import org.testfx.framework.junit.ApplicationTest;

/**
 * A helpful version of {@link ApplicationTest} that automatically cleans up windows after it
 * finishes.
 */
public abstract class AutoClosingApplicationTest extends ApplicationTest {

  @After
  public void closeAllStages() {
    listWindows().forEach(window -> FxHelper.runAndWait(window::hide));
  }

}