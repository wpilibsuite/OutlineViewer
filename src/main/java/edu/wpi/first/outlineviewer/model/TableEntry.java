package edu.wpi.first.outlineviewer.model;

/**
 * An entry representing a network table. This has no value and no type.
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
