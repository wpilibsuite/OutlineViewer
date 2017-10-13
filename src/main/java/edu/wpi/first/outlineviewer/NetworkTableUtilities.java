package edu.wpi.first.outlineviewer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * Utility methods for working with network tables.
 */
public final class NetworkTableUtilities {

  private static NetworkTableInstance networkTableInstance = NetworkTableInstance.create();

  public static NetworkTableInstance getNetworkTableInstance() {
    return networkTableInstance;
  }

  /**
   * Create a new instance of NetworkTables.  Kills the old instance.
   */
  public static void createNewNetworkTableInstance() {
    networkTableInstance.stopClient();
    networkTableInstance.stopServer();
    networkTableInstance.stopDSClient();
    networkTableInstance = NetworkTableInstance.create();
  }

  private NetworkTableUtilities() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  /**
   * Normalizes a network table key to contain no consecutive slashes and start with a single
   * forward slash.
   *
   * @param key the key to normalize
   */
  public static String normalize(String key) {
    return (NetworkTable.PATH_SEPARATOR + key).replaceAll("/{2,}",
        String.valueOf(NetworkTable.PATH_SEPARATOR));
  }

  /**
   * Concatenates multiple keys.
   *
   * @param key1 the first key
   * @param key2 the second key
   * @param more optional extra keys to concatenate
   */
  public static String concat(String key1, String key2, String... more) {
    StringBuilder builder
        = new StringBuilder(key1).append(NetworkTable.PATH_SEPARATOR).append(key2);
    for (String s : more) {
      builder.append(NetworkTable.PATH_SEPARATOR).append(s);
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
    if (key.isEmpty() || String.valueOf(NetworkTable.PATH_SEPARATOR).equals(key)) {
      return "Root";
    }
    if (!key.contains(String.valueOf(NetworkTable.PATH_SEPARATOR))) {
      return key;
    }
    String[] values = key.split(String.valueOf(NetworkTable.PATH_SEPARATOR));
    return values[values.length - 1];
  }

  /**
   * Deletes a key from network tables. If the key represents a subtable, everything under that
   * subtable will be deleted.
   */
  public static void delete(String key) {
    NetworkTableEntry[] entries = networkTableInstance.getEntries(key, 0xFF);

    for (NetworkTableEntry entry : entries) {
      entry.delete();
    }
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

    networkTableInstance.setNetworkIdentity("OutlineViewer");
    networkTableInstance.startServer("networktables.ini", "", port);
  }

  /**
   * Sets ntcore to client mode.
   *
   * @param serverId   the ip or team number of the server to connect to, eg "127.0.0.1" or "190"
   * @param serverPort the port of the server to connect to.
   */
  public static void setClient(String serverId, int serverPort) {
    shutdown();

    networkTableInstance.setNetworkIdentity("OutlineViewer");
    if (serverId.matches("[1-9](\\d{1,3})?")) {
      networkTableInstance.startClientTeam(Integer.parseInt(serverId), serverPort);
    } else {
      networkTableInstance.startClient(serverId, serverPort);
    }
  }

  /**
   * Checks if ntcore is currently running.
   */
  public static boolean isRunning(NetworkTableInstance networkTableInstance) {
    return networkTableInstance.getNetworkMode() != 0;
  }

  /**
   * Checks if ntcore is in a failed server or client state. This normally happens if the
   * requested server port is already in use (server mode) or if the app can't find a server
   * at the requested address (client mode).
   */
  public static boolean failed(NetworkTableInstance networkTableInstance) {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeFailure);
  }

  /**
   * Checks if ntcore is currently starting up the client or server.
   */
  public static boolean starting(NetworkTableInstance networkTableInstance) {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeStarting);
  }

  /**
   * Checks if ntcore is currently running in server mode.
   */
  public static boolean isServer(NetworkTableInstance networkTableInstance) {
    return hasFlag(networkTableInstance.getNetworkMode(), NetworkTableInstance.kNetModeServer);
  }

  /**
   * Checks if ntcore is currently running in client mode.
   */
  public static boolean isClient(NetworkTableInstance networkTableInstance) {
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
