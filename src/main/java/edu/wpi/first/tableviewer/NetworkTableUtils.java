package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.EntryInfo;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

import java.util.stream.Stream;

import static edu.wpi.first.wpilibj.networktables.NetworkTablesJNI.NT_NET_MODE_CLIENT;
import static edu.wpi.first.wpilibj.networktables.NetworkTablesJNI.NT_NET_MODE_FAILURE;
import static edu.wpi.first.wpilibj.networktables.NetworkTablesJNI.NT_NET_MODE_SERVER;
import static edu.wpi.first.wpilibj.networktables.NetworkTablesJNI.NT_NET_MODE_STARTING;
import static edu.wpi.first.wpilibj.networktables.NetworkTablesJNI.getNetworkMode;

/**
 * Utility methods for working with network tables.
 */
public class NetworkTableUtils {

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
    if (key.isEmpty() || key.equals("/")) {
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
    key = normalize(key);
    if (NetworkTablesJNI.containsKey(key)) {
      NetworkTablesJNI.deleteEntry(key);
    } else {
      // subtable
      EntryInfo[] entries = NetworkTablesJNI.getEntries(key, 0xFF);
      Stream.of(entries)
            .map(entryInfo -> entryInfo.name)
            .forEach(NetworkTableUtils::delete);
    }
  }

  /**
   * Checks if an entry is persistent.
   *
   * @param key the key of the entry to check
   * @return true if the entry is persistent, false if it is not
   */
  public static boolean isPersistent(String key) {
    key = normalize(key);
    return (NetworkTablesJNI.getEntryFlags(key) & NetworkTable.PERSISTENT) != 0;
  }

  /**
   * Sets the entry with the given key to be persistent. Has no effect if the entry is already
   * persistent.
   *
   * @param key the key of the entry to make persistent
   */
  public static void setPersistent(String key) {
    key = normalize(key);
    int flags = NetworkTablesJNI.getEntryFlags(key);
    NetworkTablesJNI.setEntryFlags(key, flags | NetworkTable.PERSISTENT);
  }

  /**
   * Makes the entry with the given key non-persistent. Has no effect if the entry is already
   * not persistent.
   *
   * @param key the of the entry to make non-persistent
   */
  public static void clearPersistent(String key) {
    key = normalize(key);
    int flags = NetworkTablesJNI.getEntryFlags(key);
    NetworkTablesJNI.setEntryFlags(key, flags & ~NetworkTable.PERSISTENT);
  }

  /**
   * Toggles persistence of the given key.
   *
   * @see #isPersistent(String)
   * @see #setPersistent(String)
   * @see #clearPersistent(String)
   */
  public static void togglePersistent(String key) {
    if (isPersistent(key)) {
      clearPersistent(key);
    } else {
      setPersistent(key);
    }
  }

  /**
   * Shuts down the network table client or server, then clears all entries from network tables.
   * This should be used when changing from server mode to client mode, or changing server
   * address while in client mode.
   */
  public static void shutdown() {
    NetworkTablesJNI.stopDSClient();
    NetworkTablesJNI.stopClient();
    NetworkTablesJNI.stopServer();
    NetworkTablesJNI.deleteAllEntries(); // delete AFTER shutting down the server/client
    NetworkTable.shutdown();
  }

  /**
   * Sets ntcore to server mode.
   *
   * @param port the port on the local machine to run the ntcore server on
   */
  public static void setServer(int port) {
    System.out.println("NetworkTableUtils::setServer(" + port + ")");
    shutdown();
    NetworkTablesJNI.startServer("networktables.ini", "", port);
    NetworkTable.initialize();
  }

  /**
   * Sets ntcore to client mode.
   *
   * @param serverIp   the ip of the server to connect to, eg "127.0.0.1" or "localhost"
   * @param serverPort the port of the server to connect to. This is normally 1735.
   */
  public static void setClient(String serverIp, int serverPort) {
    System.out.println("NetworkTableUtils::setClient(" + serverIp + ":" + serverPort + ")");
    shutdown();
    NetworkTablesJNI.startClient(serverIp, serverPort);
    NetworkTable.initialize();
  }

  /**
   * Checks if the given bit flags contains the given flag.
   *
   * @param flags       the bit flags to check
   * @param flagToCheck the specific flag to check
   */
  public static boolean hasFlag(int flags, int flagToCheck) {
    return (flags & flagToCheck) != 0;
  }

  /**
   * Checks if ntcore is currently running.
   */
  public static boolean isRunning() {
    return getNetworkMode() != 0;
  }

  /**
   * Checks if ntcore is in a failed server or client state. This normally happens if the
   * requested server port is already in use (server mode) or if the app can't find a server
   * at the requested address (client mode).
   */
  public static boolean failed() {
    return hasFlag(getNetworkMode(), NT_NET_MODE_FAILURE);
  }

  /**
   * Checks if ntcore is currently starting up the client or server.
   */
  public static boolean starting() {
    return hasFlag(getNetworkMode(), NT_NET_MODE_STARTING);
  }

  /**
   * Checks if ntcore is currently running in server mode.
   */
  public static boolean isServer() {
    return hasFlag(getNetworkMode(), NT_NET_MODE_SERVER);
  }

  /**
   * Checks if ntcore is currently running in client mode.
   */
  public static boolean isClient() {
    return hasFlag(getNetworkMode(), NT_NET_MODE_CLIENT);
  }

}
