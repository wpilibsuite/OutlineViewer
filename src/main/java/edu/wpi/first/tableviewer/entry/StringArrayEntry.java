package edu.wpi.first.tableviewer.entry;

import java.util.Arrays;

/**
 *
 */
public class StringArrayEntry extends Entry<String[]> {

  public StringArrayEntry(String key, String[] value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(String[] value) {
    return String.format("String[%d]", value.length);
  }

  @Override
  public String getDisplayString() {
    return Arrays.toString(getValue());
  }

}
