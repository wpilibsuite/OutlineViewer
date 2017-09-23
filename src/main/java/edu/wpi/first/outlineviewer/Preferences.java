package edu.wpi.first.outlineviewer;

import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * App-global preferences.
 */
public final class Preferences {

  private static final String SERVER = "serverMode";
  private static final String IP = "ip";
  private static final String PORT = "port";

  private static final java.util.prefs.Preferences preferences
      = java.util.prefs.Preferences.userNodeForPackage(OutlineViewer.class);

  /**
   * Whether or not the app should be running in server mode. Defaults to false (client mode).
   */
  private static final BooleanProperty server
      = new SimpleBooleanProperty(Preferences.class, SERVER, false);

  /**
   * The address or team number given by the user.
   */
  private static final StringProperty ip
      = new SimpleStringProperty(Preferences.class, IP, "localhost");

  private static IntegerProperty port = new SimpleIntegerProperty(Preferences.class, "port",
      NetworkTableInstance.kDefaultPort);

  // Load saved preferences and set up listeners to automatically save changes
  static {
    setServer(preferences.getBoolean(SERVER, false));
    setIp(preferences.get(IP, "localhost"));
    setPort(preferences.getInt(PORT, NetworkTableInstance.kDefaultPort));

    serverProperty().addListener(
        (__, oldValue, newValue) -> preferences.putBoolean(SERVER, newValue));
    ipProperty().addListener(
        (__, oldValue, newValue) -> preferences.put(IP, newValue));
    portProperty().addListener(
        (__, oldValue, newValue) -> preferences.putInt(PORT, newValue.intValue()));
  }

  private Preferences() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  /**
   * Resets all preferences to their defaults.
   */
  public static void reset() {
    setServer(false);
    setIp("localhost");
    setPort(NetworkTableInstance.kDefaultPort);
  }

  public static boolean isServer() {
    return server.get();
  }

  public static BooleanProperty serverProperty() {
    return server;
  }

  public static void setServer(boolean server) {
    Preferences.server.set(server);
  }

  public static String getIp() {
    return ip.get();
  }

  public static StringProperty ipProperty() {
    return ip;
  }

  public static void setIp(String ip) {
    Preferences.ip.set(ip);
  }

  public static int getPort() {
    return port.get();
  }

  public static IntegerProperty portProperty() {
    return port;
  }

  public static void setPort(int port) {
    Preferences.port.set(port);
  }

}
