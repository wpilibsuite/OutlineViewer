package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 *
 */
public class NetworkTableUtils {

  public static String normalize(String key) {
    return ("/" + key).replaceAll("/{2,}", "/");
  }

  public static String concat(String key1, String key2, String... more) {
    StringBuilder b = new StringBuilder(key1).append('/').append(key2);
    for (String s : more) {
      b.append('/').append(s);
    }
    return normalize(b.toString());
  }

  public static String simpleKey(String key) {
    if (key.isEmpty() || key.equals("/")) {
      return "Root";
    }
    if (!key.contains("/")) {
      return key;
    }
    return key.substring(key.lastIndexOf('/') + 1);
  }

  public static ITable getRootTable() {
    return NetworkTable.getTable("");
  }

}
