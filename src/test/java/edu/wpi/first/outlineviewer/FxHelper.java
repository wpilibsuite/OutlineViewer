package edu.wpi.first.outlineviewer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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

  /**
   * Try to catch an uncaught exception on the JavaFX thread. The UncaughtExceptionHandler is set on
   * the JavaFX thread and the provided Runnable is run. If the provided Exception class is found,
   * return true. If not, return false. If the provided timeout expires, throw an
   * InterruptedException.
   *
   * @param runnable Runnable to run on this thread
   * @param exceptionClass Exception class to look for in the JavaFX thread
   * @param timeout Timeout for waiting for the exception to be thrown
   * @param timeoutUnit Timeout units
   * @return True if a matching exception was thrown from the JavaFX thread, false otherwise
   * @throws InterruptedException If the current thread is interrupted while waiting
   */
  public static boolean catchInJavaFXThread(Runnable runnable,
                                            Class<?> exceptionClass,
                                            long timeout,
                                            TimeUnit timeoutUnit) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final boolean[] exceptionWasThrown = new boolean[1];
    exceptionWasThrown[0] = false;

    runAndWait(() -> Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
      if (throwable.getClass().equals(exceptionClass)) {
        exceptionWasThrown[0] = true;
        latch.countDown();
      }
    }));

    runnable.run();
    return latch.await(timeout, timeoutUnit) && exceptionWasThrown[0];
  }

}
