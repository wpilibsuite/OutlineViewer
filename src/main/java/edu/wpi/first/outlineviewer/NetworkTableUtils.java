package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTableInstance;

/**
 * Utility methods for working with network tables.
 */
public final class NetworkTableUtils {

  private static NetworkTableInstance networkTableInstance = NetworkTableInstance.create();

  public static NetworkTableInstance getNetworkTableInstance() {
    return networkTableInstance;
  }

  public static NetworkTable getRootTable() {
    return networkTableInstance.getTable("");
  }

  static void createNewNetworkTableInstance() {
    if (networkTableInstance != null) {
      networkTableInstance.stopClient();
      networkTableInstance.stopServer();
      networkTableInstance.stopDSClient();
    }
    networkTableInstance = NetworkTableInstance.create();
  }

  private NetworkTableUtils() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  /**
   * Normalizes a network table key to contain no consecutive slashes and start with a single
   * forward slash.
   *
   * @param key the key to normalize
   */
  public static String normalize(String key) {
    return ("/" + key).replaceAll("/{2,}", "/");
  }

  /**
   * Concatenates multiple keys.
   *
   * @param key1 the first key
   * @param key2 the second key
   * @param more optional extra keys to concatenate
   */
  public static String concat(String key1, String key2, String... more) {
    StringBuilder builder = new StringBuilder(key1).append('/').append(key2);
    for (String s : more) {
      builder.append('/').append(s);
    }
    return normalize(builder.toString());
  }

  /**
   * Gets the simple representation of a key. If the key represents the root table, "Root" is
   * returned. Otherwise, this will return the final element in a "/"-separated key.
   *
   * @param key the key to get the simple representation of
   */
  public static String simpleKey(String key) {
    if (key.isEmpty() || "/".equals(key)) {
      return "Root";
    }
    if (!key.contains("/")) {
      return key;
    }
    return key.substring(key.lastIndexOf('/') + 1);
  }

  /**
   * Deletes a key from network tables. If the key represents a subtable, everything under that
   * subtable will be deleted.
   */
  public static void delete(String key) {
    String normalKey = normalize(key);
    if (getRootTable().containsKey(normalKey)) {
      getRootTable().delete(normalKey);
    } // else {
      // subtable
      //EntryInfo[] entries = getRootTable().get.getEntries(normalKey, 0xFF);
      //Stream.of(entries)
      //      .map(entryInfo -> entryInfo.name)
      //      .forEach(NetworkTableUtils::delete);
      //}
  }

  /**
   * Shuts down the network table client or server, then clears all entries from network tables.
   * This should be used when changing from server mode to client mode, or changing server
   * address while in client mode.
   */
  public static void shutdown() {
    networkTableInstance.stopDSClient();
    networkTableInstance.stopClient();
    networkTableInstance.stopServer();

    networkTableInstance.deleteAllEntries();
  }

  /**
   * Sets ntcore to server mode.
   *
   * @param port the port on the local machine to run the ntcore server on
   */
  public static void setServer(int port) {
    shutdown();

    networkTableInstance.setServer("OutlineViewer", port);
    networkTableInstance.startServer();
  }

  /**
   * Sets ntcore to client mode.
   *
   * @param serverId   the ip or team number of the server to connect to, eg "127.0.0.1" or "190"
   * @param serverPort the port of the server to connect to.
   */
  public static void setClient(String serverId, int serverPort) {
    shutdown();

    if (serverId.matches("[1-9](\\d{1,3})?")) {
      networkTableInstance.startClientTeam(Integer.parseInt(serverId), serverPort);
    } else {
      networkTableInstance.startClient(serverId, serverPort);
    }
  }

  /**
   * Checks if ntcore is currently running.
   */
  public static boolean isRunning() {
    return networkTableInstance.getNetworkMode() != 0;
  }

  /**
   * Checks if ntcore is in a failed server or client state. This normally happens if the
   * requested server port is already in use (server mode) or if the app can't find a server
   * at the requested address (client mode).
   */
  public static boolean failed() {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeFailure);
  }

  /**
   * Checks if ntcore is currently starting up the client or server.
   */
  public static boolean starting() {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeStarting);
  }

  /**
   * Checks if ntcore is currently running in server mode.
   */
  public static boolean isServer() {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeServer);
  }

  /**
   * Checks if ntcore is currently running in client mode.
   */
  public static boolean isClient() {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeClient);
  }

  /**
   * Checks if the given bit flags contains the given flag.
   *
   * @param flags       the bit flags to check
   * @param flagToCheck the specific flag to check
   */
  private static boolean hasFlag(int flags, int flagToCheck) {
    return (flags & flagToCheck) != 0;
  }

}
