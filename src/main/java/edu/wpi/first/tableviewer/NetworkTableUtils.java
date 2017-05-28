package edu.wpi.first.tableviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 *
 */
public class NetworkTableUtils {

  public static final ITable rootTable = NetworkTable.getTable("");

  public static String normalize(String key) {
    return ("/" + key).replaceAll("/{2,}", "/");
  }

}
