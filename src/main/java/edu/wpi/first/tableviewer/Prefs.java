package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.prefs.Preferences;

/**
 * App-global preferences.
 */
public final class Prefs {

  private static final String SHOW_METADATA = "show_metadata";
  private static final String SERVER = "server";
  private static final String IP = "ip";
  private static final String PORT = "port";

  private static final Preferences preferences = Preferences.userNodeForPackage(Main.class);

  /**
   * Whether or not metadata should be visible in the tree. Defaults to true.
   */
  private static final BooleanProperty showMetaData
      = new SimpleBooleanProperty(Prefs.class, SHOW_METADATA, true);

  /**
   * Whether or not the app should be running in server mode. Defaults to false (client mode).
   */
  private static final BooleanProperty server
      = new SimpleBooleanProperty(Prefs.class, SERVER, false);

  /**
   * The address or team number given by the user.
   */
  private static final StringProperty ip
      = new SimpleStringProperty(Prefs.class, IP, "localhost");

  private static IntegerProperty port = new SimpleIntegerProperty(Prefs.class, "port",
      NetworkTable.DEFAULT_PORT);

  // Load saved preferences and set up listeners to automatically save changes
  static {
    setShowMetaData(preferences.getBoolean(SHOW_METADATA, false));
    setServer(preferences.getBoolean(SERVER, false));
    setIp(preferences.get(IP, "localhost"));
    setPort(preferences.getInt(PORT, 1735));

    showMetaDataProperty().addListener(
        (__, oldValue, newValue) -> preferences.putBoolean(SHOW_METADATA, newValue));
    serverProperty().addListener(
        (__, oldValue, newValue) -> preferences.putBoolean(SERVER, newValue));
    ipProperty().addListener(
        (__, oldValue, newValue) -> preferences.put(IP, newValue));
    portProperty().addListener(
        (__, oldValue, newValue) -> preferences.putInt(PORT, newValue.intValue()));
  }

  private Prefs() {
    // Utility class
  }

  public static boolean isShowMetaData() {
    return showMetaData.get();
  }

  public static BooleanProperty showMetaDataProperty() {
    return showMetaData;
  }

  public static void setShowMetaData(boolean showMetaData) {
    Prefs.showMetaData.set(showMetaData);
  }

  public static boolean isServer() {
    return server.get();
  }

  public static BooleanProperty serverProperty() {
    return server;
  }

  public static void setServer(boolean server) {
    Prefs.server.set(server);
  }

  public static String getIp() {
    return ip.get();
  }

  public static StringProperty ipProperty() {
    return ip;
  }

  public static void setIp(String ip) {
    Prefs.ip.set(ip);
  }

  public static int getPort() {
    return port.get();
  }

  public static IntegerProperty portProperty() {
    return port;
  }

  public static void setPort(int port) {
    Prefs.port.set(port);
  }

}
