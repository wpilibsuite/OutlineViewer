package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

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
    Prefs.resolvedAddressProperty().addListener(__ -> update());
    update();
  }

  /**
   * Performs a single update.
   */
  public void update() {
    if (Prefs.isServer()) {
      NetworkTableUtils.setServer(Prefs.getPort());
    } else {
      String rawAddress = Prefs.getIp();
      if (rawAddress.matches("[0-9]{1,5}")) {
        // Treat as team number. Ignore the port and resolved address to use the 5 addresses
        // used with NetworkTable.setTeam
        NetworkTableUtils.shutdown();
        NetworkTable.setTeam(Integer.parseInt(rawAddress));
      } else {
        // Treat as a general address:port
        NetworkTableUtils.setClient(Prefs.getResolvedAddress(), Prefs.getPort());
      }
    }
  }

}
