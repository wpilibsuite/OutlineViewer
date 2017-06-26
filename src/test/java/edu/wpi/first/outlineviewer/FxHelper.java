package edu.wpi.first.outlineviewer;

import javafx.application.Platform;
import org.testfx.util.WaitForAsyncUtils;

public final class FxHelper {

  private FxHelper() {
    // Utility class
  }

  /**
   * Runs the given runnable on the JavaFX application thread and waits for it to complete.
   *
   * @param runnable the action to run on the application thread
   */
  public static void runAndWait(Runnable runnable) {
    Platform.runLater(runnable);
    WaitForAsyncUtils.waitForFxEvents();
  }

}
