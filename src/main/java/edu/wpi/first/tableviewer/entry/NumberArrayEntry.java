package edu.wpi.first.tableviewer.entry;

import java.util.Arrays;

/**
 *
 */
public class NumberArrayEntry extends Entry<double[]> {

  public NumberArrayEntry(String key, double[] value) {
    super(key, value);
  }

  @Override
  protected String getTypeString(double[] value) {
    return String.format("Number[%d]", value.length);
  }

  @Override
  public String getDisplayString() {
    return Arrays.toString(getValue());
  }
}
