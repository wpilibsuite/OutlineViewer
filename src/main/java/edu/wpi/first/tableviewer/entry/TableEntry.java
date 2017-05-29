package edu.wpi.first.tableviewer.entry;

/**
 *
 */
public class TableEntry extends Entry<Object> {

  public TableEntry(String key) {
    super(key);
  }

  @Override
  public String getDisplayString() {
    return "";
  }

  @Override
  protected String getTypeString(Object value) {
    return "";
  }

}
