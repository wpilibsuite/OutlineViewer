package edu.wpi.first.tableviewer.entry;

/**
 *
 */
public class BooleanEntry extends Entry<Boolean> {

  public BooleanEntry(String key, Boolean value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(Boolean value) {
    return "Boolean";
  }

}
