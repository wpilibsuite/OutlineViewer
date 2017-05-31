package edu.wpi.first.tableviewer;

import javafx.application.Platform;
import org.testfx.util.WaitForAsyncUtils;

public class FxHelper {

  private static Thread fxApplicationThread = null;

  /**
   * Runs the given runnable on the JavaFX application thread and waits for it to complete.
   *
   * @param runnable the action to run on the application thread
   */
  public static void runAndWait(Runnable runnable) {
    Platform.runLater(runnable);
    WaitForAsyncUtils.waitForFxEvents();
  }

  public static Thread getFxApplicationThread() {
    if (fxApplicationThread == null) {
      runAndWait(() -> fxApplicationThread = Thread.currentThread());
    }
    return fxApplicationThread;
  }

}
