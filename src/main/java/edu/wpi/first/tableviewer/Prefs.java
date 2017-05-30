package edu.wpi.first.tableviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * App-global preferences.
 */
public class Prefs {

  private static final Logger logger = Logger.getLogger(Prefs.class.getName());

  public static final String SHOW_METADATA = "show_metadata";
  public static final String SERVER = "server";
  public static final String IP = "ip";
  public static final String RESOLVED_ADDRESS = "resolved_address";
  public static final String PORT = "port";

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
   * The address given by the user. This is coerced into a port (if applicable)
   * and a {@link #resolvedAddress resolved address}. For example, if this is "192" and the app
   * is in client mode, the resolved address is "roborio-192-frc.local". If this is "192" and the
   * app is in server mode, the resolved address is not affected but the port is set to 192. If
   * this is "localhost:192" and the app is in client mode, the resolved address will be localhost
   * and the remote server port will be set to 192.
   */
  private static final StringProperty ip
      = new SimpleStringProperty(Prefs.class, IP, "localhost");

  private static IntegerProperty port = new SimpleIntegerProperty(Prefs.class, "port", 1735);


  /**
   * The actual address for the client to connect to. Does nothing if the app is in server mode.
   *
   * @see #server
   */
  private static final StringProperty resolvedAddress
      = new SimpleStringProperty(Prefs.class, RESOLVED_ADDRESS, null);

  // Load saved preferences and set up listeners to automatically save changes
  static {
    showMetaData.addListener((__, prev, cur) -> logger.info("Show metadata changed to " + cur));
    server.addListener((__, prev, cur) -> logger.info("Server mode changed to " + cur));
    ip.addListener((__, prev, cur) -> logger.info("Raw address changed from " + prev + " to " + cur));
    resolvedAddress.addListener((__, prev, cur) -> logger.info("Resolved address changed from " + prev + " to " + cur));
    port.addListener((__, prev, cur) -> logger.info("Port changed from " + prev + " to " + cur));

    setShowMetaData(preferences.getBoolean(SHOW_METADATA, false));
    setServer(preferences.getBoolean(SERVER, false));
    setIp(preferences.get(IP, "localhost"));
    setResolvedAddress(preferences.get(RESOLVED_ADDRESS, "localhost"));
    setPort(preferences.getInt(PORT, 1735));

    showMetaDataProperty().addListener(
        (__, oldValue, newValue) -> preferences.putBoolean(SHOW_METADATA, newValue));
    serverProperty().addListener(
        (__, oldValue, newValue) -> preferences.putBoolean(SERVER, newValue));
    ipProperty().addListener(
        (__, oldValue, newValue) -> preferences.put(IP, newValue));
    resolvedAddressProperty().addListener(
        (__, oldValue, newValue) -> preferences.put(RESOLVED_ADDRESS, newValue));
    portProperty().addListener(
        (__, oldValue, newValue) -> preferences.putInt(PORT, newValue.intValue()));
  }

  private Prefs() {
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

  public static String getResolvedAddress() {
    return resolvedAddress.get();
  }

  public static StringProperty resolvedAddressProperty() {
    return resolvedAddress;
  }

  public static void setResolvedAddress(String resolvedAddress) {
    Prefs.resolvedAddress.set(resolvedAddress);
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
