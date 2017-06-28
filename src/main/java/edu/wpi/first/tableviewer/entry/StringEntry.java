package edu.wpi.first.tableviewer.entry;

/**
 * An entry containing a single string.
 */
public class StringEntry extends Entry<String> {

  public StringEntry(String key, String value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(String value) {
    return "String";
  }

}
