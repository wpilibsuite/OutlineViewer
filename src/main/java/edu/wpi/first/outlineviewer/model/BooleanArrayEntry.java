package edu.wpi.first.outlineviewer.model;

import java.util.Arrays;

/**
 * An entry containing an array of boolean values.
 */
public class BooleanArrayEntry extends Entry<boolean[]> {

  @SuppressWarnings("PMD.UseVarargs")
  public BooleanArrayEntry(String key, boolean[] value) {
    super(key, value);
  }

  @Override
  @SuppressWarnings("PMD.UseVarargs")
  protected String getTypeString(boolean[] value) {
    return String.format("Boolean[%d]", value.length);
  }

  @Override
  public String getDisplayString() {
    return Arrays.toString(getValue());
  }

}
