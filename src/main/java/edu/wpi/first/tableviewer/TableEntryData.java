/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.tableviewer;

/**
 * @author Sam
 */
public class TableEntryData {

  private String key;
  private Object value;
  private String type;

  private static final String[] typeNames = new String[]{"Boolean", "Number", "String", "Raw", "Boolean[]", "Number[]", "String[]"};

  public TableEntryData(String key, Object value) {
    this.key = NetworkTableUtils.normalize(key);
    if (value != null) {
      this.value = value;
      this.type = typeFromValue(value);
    }
  }

  public TableEntryData() {
    this("/", null);
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  public String getType() {
    return type;
  }

  /**
   * Sets the value of this TableEntryData.
   */
  public void setValue(Object newValue) {
    this.value = newValue;
    String newtype = typeFromValue(value);
    if (newtype != "ERROR")
      this.type = newtype;
  }

  /**
   * Explicitly changes the type of this data. Used on BranchNodes when a
   * metadata entry comes in and to show what kind of system it shows data
   * from (such as "Speed controller", "Subsystem", etc.).
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Generates a type string based on the value of the table entry.
   */
  private String typeFromValue(Object value) {
    if (value instanceof Boolean) return typeNames[0];
    if (value instanceof Double) return typeNames[1];
    if (value instanceof String) return typeNames[2];
    if (value instanceof byte[]) return typeNames[3];
    if (value instanceof boolean[])
      return typeNames[4].substring(0, 8) + ((boolean[]) value).length + "]";
    if (value instanceof double[])
      return typeNames[5].substring(0, 7) + ((double[]) value).length + "]";
    if (value instanceof String[])
      return typeNames[6].substring(0, 7) + ((String[]) value).length + "]";
    return "ERROR";
  }

  /**
   * Sees if the data within this structure is metadata (i.e. has a key
   * bookended by tildes ("~") or starts with a dot). Used to show/hide metadata
   * leaves in branches.
   */
  public boolean isMetadata() {
    return key.matches("^.*/(\\..+|~.+~).*$");
  }

  @Override
  public String toString() {
    return "TableEntryData(" +
        "key='" + key + '\'' +
        ", value=" + value +
        ", type='" + type + '\'' +
        ')';
  }
}
