package edu.wpi.first.outlineviewer;

/**
 * Class for automatically updating ntcore client/server status when preferences are changed.
 */
public class AutoUpdater {

  /**
   * Initializes the updater to update whenever a network setting changes. This also performs a
   * single initial update.
   */
  public void init() {
    Prefs.ipProperty().addListener(__ -> update());
    Prefs.portProperty().addListener(__ -> update());
    Prefs.serverProperty().addListener(__ -> update());
    update();
  }

  /**
   * Performs a single update.
   */
  public void update() {
    if (Prefs.isServer()) {
      NetworkTableUtils.setServer(Prefs.getPort());
    } else {
      NetworkTableUtils.setClient(Prefs.getIp(), Prefs.getPort());
    }
  }

}
