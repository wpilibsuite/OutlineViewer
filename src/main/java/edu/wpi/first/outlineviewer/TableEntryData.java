package edu.wpi.first.outlineviewer;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.tables.ITable;

import static com.google.common.base.Preconditions.checkNotNull;

public class TableEntryData extends TableEntry {

  /**
   * Create a new TableEntryData.
   *
   * @param key The NetworkTable key associated with this entry
   * @param value The NetworkTable value associated with this entry
   */
  public TableEntryData(String key, Object value) {
    super(key, value, typeFromValue(key, value));
  }

  /**
   * Explicitly changes the type of this data. Used on BranchNodes when a
   * metadata entry comes in and to show what kind of system it shows data
   * from (such as "Speed controller", "Subsystem", etc.).
   */
  public void setType(String type) {
    checkNotNull(type, "Type cannot be null");
    this.type.setValue(type);
  }

  /**
   * Generates a type string based on the value of the table entry.
   */
  public static String typeFromValue(String key, Object value) {
    checkNotNull(key, "Key cannot be null");
    checkNotNull(value, "Value cannot be null");

    if (isMetadata(key)) {
      return "Metadata";
    } else if (value instanceof Boolean) {
      return "Boolean";
    } else if (value instanceof Double) {
      return "Number";
    } else if (value instanceof String) {
      return "String";
    } else if (value instanceof byte[]) {
      return "Raw";
    } else if (value instanceof boolean[]) {
      return "Boolean[" + ((boolean[]) value).length + "]";
    } else if (value instanceof double[]) {
      return "Number[" + ((double[]) value).length + "]";
    } else if (value instanceof String[]) {
      return "String[" + ((String[]) value).length + "]";
    }
    return "Unknown";
  }

  protected void setupListener() {
    System.out.println("Setting up new data listener:\t" + this + "\t" + getNetworkTablePath());
    NetworkTablesJNI.addEntryListener(getNetworkTablePath(), (uid, key, value, flags) -> {
      if ((flags & ITable.NOTIFY_DELETE) != 0) {
        System.out.println("Delete");
        getTreeItem().getParent().getChildren().remove(getTreeItem());
        NetworkTablesJNI.removeEntryListener(uid);
      } else {
        getValue().setValue(value);
      }
    }, ITable.NOTIFY_IMMEDIATE
        | ITable.NOTIFY_LOCAL
        | ITable.NOTIFY_DELETE
        | ITable.NOTIFY_UPDATE);
  }
}
