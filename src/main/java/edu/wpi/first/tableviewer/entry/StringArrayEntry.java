package edu.wpi.first.tableviewer.entry;

import java.util.Arrays;

/**
 * An entry containing an array of strings.
 */
public class StringArrayEntry extends Entry<String[]> {

  @SuppressWarnings("PMD.UseVarargs")
  public StringArrayEntry(String key, String[] value) {
    super(key, value);
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  protected String getTypeString(String[] value) {
    return String.format("String[%d]", value.length);
  }

  @Override
  public String getDisplayString() {
    return Arrays.toString(getValue());
  }

}
