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
    Preferences.ipProperty().addListener(__ -> update());
    Preferences.portProperty().addListener(__ -> update());
    Preferences.serverProperty().addListener(__ -> update());

    update();
  }

  /**
   * Performs a single update.
   */
  public void update() {
    if (Preferences.isServer()) {
      NetworkTableUtils.setServer(Preferences.getPort());
    } else {
      NetworkTableUtils.setClient(Preferences.getIp(), Preferences.getPort());
    }
  }

}
