package edu.wpi.first.tableviewer;

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
public class Prefs {

  public static final String SHOW_METADATA = "show_metadata";
  public static final String SERVER = "server";
  public static final String IP = "ip";
  public static final String PORT = "port";
  public static final String TEAM = "team";
  public static final String RESOLVED_ADDRESS = "resolved_address";

  private static final Preferences preferences = Preferences.userNodeForPackage(Main.class);

  private static final BooleanProperty showMetaData
      = new SimpleBooleanProperty(Prefs.class, SHOW_METADATA, true);

  private static final BooleanProperty server
      = new SimpleBooleanProperty(Prefs.class, SERVER, false);

  private static final StringProperty ip
      = new SimpleStringProperty(Prefs.class, IP, "localhost");

  private static final IntegerProperty port
      = new SimpleIntegerProperty(Prefs.class, PORT, 1735);

  private static final IntegerProperty team
      = new SimpleIntegerProperty(Prefs.class, TEAM, 0);

  private static final StringProperty resolvedAddress
      = new SimpleStringProperty(Prefs.class, RESOLVED_ADDRESS, null);

  // Load saved preferences and set up listeners to automatically save changes
  static {
    setShowMetaData(preferences.getBoolean(SHOW_METADATA, false));
    setServer(preferences.getBoolean(SERVER, false));
    setIp(preferences.get(IP, "localhost"));
    setPort(preferences.getInt(PORT, 1735));
    setTeam(preferences.getInt(TEAM, 0));
    setResolvedAddress(preferences.get(RESOLVED_ADDRESS, "localhost"));

    showMetaDataProperty().addListener((__, o, n) -> preferences.putBoolean(SHOW_METADATA, n));
    serverProperty().addListener((__, o, n) -> preferences.putBoolean(SERVER, n));
    ipProperty().addListener((__, o, n) -> preferences.put(IP, n));
    portProperty().addListener((__, o, n) -> preferences.putInt(PORT, n.intValue()));
    teamProperty().addListener((__, o, n) -> preferences.putInt(TEAM, n.intValue()));
    resolvedAddressProperty().addListener((__, o, n) -> preferences.put(RESOLVED_ADDRESS, n));
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

  public static int getPort() {
    return port.get();
  }

  public static IntegerProperty portProperty() {
    return port;
  }

  public static void setPort(int port) {
    Prefs.port.set(port);
  }

  public static int getTeam() {
    return team.get();
  }

  public static IntegerProperty teamProperty() {
    return team;
  }

  public static void setTeam(int team) {
    Prefs.team.set(team);
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

}
